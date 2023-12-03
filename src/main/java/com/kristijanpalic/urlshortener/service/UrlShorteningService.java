package com.kristijanpalic.urlshortener.service;

import com.kristijanpalic.urlshortener.model.UrlEncoding;
import com.kristijanpalic.urlshortener.model.dto.AccountRequestDTO;
import com.kristijanpalic.urlshortener.model.dto.AccountResponseDTO;
import com.kristijanpalic.urlshortener.model.dto.RegisterUrlRequestDTO;
import com.kristijanpalic.urlshortener.repository.UrlEncodingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UrlShorteningService {

    @Autowired
    private UrlEncodingRepository urlEncodingRepository;

    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final int SHORTCODE_LENGTH = 10;
    private final int PASSWORD_LENGTH = 8;

    @Value("${server.port}")
    private int port;

    public AccountResponseDTO registerAccount(AccountRequestDTO accountRequestDTO) {
        AccountResponseDTO responseDTO = new AccountResponseDTO();

        if (inMemoryUserDetailsManager.userExists(accountRequestDTO.getAccountId())) {
            responseDTO.setSuccess(false);
            responseDTO.setDescription(String.format("User with id '%s' has already been created. Please user another account id.", accountRequestDTO.getAccountId()));
            return responseDTO;
        }

        String password = generateNDigitCode(PASSWORD_LENGTH);

        UserDetails userDetails = User.builder()
                .username(accountRequestDTO.getAccountId())
                .password(passwordEncoder.encode(password))
                .roles("USER")
                .build();
        inMemoryUserDetailsManager.createUser(userDetails);

        responseDTO.setSuccess(true);
        responseDTO.setDescription("User has been successfully created.");
        responseDTO.setPassword(password);

        return responseDTO;
    }

    public String shortenUrl(RegisterUrlRequestDTO registerUrlRequestDTO, String accountId) {
        String shortUrl = generateNDigitCode(SHORTCODE_LENGTH);

        // Ensure shortcode is unique
        while (urlEncodingRepository.findOneByShortUrl(shortUrl).isPresent()) {
            shortUrl = generateNDigitCode(SHORTCODE_LENGTH);
        }

        UrlEncoding urlEncoding = new UrlEncoding(registerUrlRequestDTO.getUrl(), shortUrl, accountId, registerUrlRequestDTO.getRedirectType());
        urlEncodingRepository.save(urlEncoding);

        return buildStartOfTheShortUrl() + shortUrl;
    }

    public UrlEncoding getOriginalUrl(String shortUrl) {
        Optional<UrlEncoding> urlEncodingOptional = urlEncodingRepository.findOneByShortUrl(shortUrl);

        UrlEncoding urlEncoding = urlEncodingOptional.get();
        urlEncoding.setNumberOfTimesUsed(urlEncoding.getNumberOfTimesUsed()+1);
        urlEncodingRepository.save(urlEncoding);

        return urlEncoding;
    }

    private String buildStartOfTheShortUrl() {
        return "http://localhost:" + port + "/";
    }

    public Map<String, Integer> getStatistics(String accountId) {
        List<UrlEncoding> urlEncodings = urlEncodingRepository.findAllByOwnerAccountId(accountId);

        Map<String, Integer> statistics = new HashMap<>(urlEncodings.size());

        for(UrlEncoding urlEncoding : urlEncodings) {
            // If there are multiple shortcodes that redirect to the same URL, sum their times used
            if (statistics.containsKey(urlEncoding.getOriginalUrl())) {
                statistics.put(
                        urlEncoding.getOriginalUrl(),
                        statistics.get(urlEncoding.getOriginalUrl()) + urlEncoding.getNumberOfTimesUsed()
                );
            } else {
                statistics.put(urlEncoding.getOriginalUrl(), urlEncoding.getNumberOfTimesUsed());
            }
        }

        return statistics;
    }

    private String generateNDigitCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
