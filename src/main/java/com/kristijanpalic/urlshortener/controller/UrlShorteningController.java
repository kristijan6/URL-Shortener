package com.kristijanpalic.urlshortener.controller;

import com.kristijanpalic.urlshortener.model.UrlEncoding;
import com.kristijanpalic.urlshortener.model.dto.AccountRequestDTO;
import com.kristijanpalic.urlshortener.model.dto.AccountResponseDTO;
import com.kristijanpalic.urlshortener.model.dto.RegisterUrlRequestDTO;
import com.kristijanpalic.urlshortener.model.dto.RegisterUrlResponseDTO;
import com.kristijanpalic.urlshortener.service.UrlShorteningService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
public class UrlShorteningController {

    @Autowired
    private UrlShorteningService urlShorteningService;

    @PostMapping("/account")
    public AccountResponseDTO account(@RequestBody AccountRequestDTO accountRequestDTO) {
        AccountResponseDTO response = urlShorteningService.registerAccount(accountRequestDTO);
        return response;
    }

    @PostMapping("/register")
    public RegisterUrlResponseDTO shortenUrl(@RequestBody RegisterUrlRequestDTO registerUrlDTO, @RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        String accountId = readAccountIdFromHeader(auth);
        if (accountId == null) return null;

        String shortUrl = urlShorteningService.shortenUrl(registerUrlDTO, accountId);

        RegisterUrlResponseDTO registerUrlResponseDTO = new RegisterUrlResponseDTO();
        registerUrlResponseDTO.setShortUrl(shortUrl);

        return registerUrlResponseDTO;
    }

    @GetMapping("/statistic/{accountId}")
    public Map<String, Integer> getStatistics(@PathVariable String accountId) {
        return urlShorteningService.getStatistics(accountId);
    }

    @GetMapping("/{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletResponse httpServletResponse) throws IOException {
        UrlEncoding urlEncoding = urlShorteningService.getOriginalUrl(shortUrl);

        if (urlEncoding == null) {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        httpServletResponse.sendRedirect(urlEncoding.getOriginalUrl());
        httpServletResponse.setStatus(urlEncoding.getRedirectType());
    }

    @GetMapping("/help")
    public void help(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect("https://github.com/kristijan6/URL-Shortener");
        httpServletResponse.setStatus(302);
    }

    private String readAccountIdFromHeader(String auth) {
        if (auth != null && auth.toLowerCase().startsWith("basic")) {
            String base64Credentials = auth.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);

            // accountId:password
            final String[] values = credentials.split(":", 2);

            return values[0];
        }

        return null;
    }

}
