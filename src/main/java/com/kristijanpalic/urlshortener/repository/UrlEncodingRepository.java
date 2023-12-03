package com.kristijanpalic.urlshortener.repository;

import com.kristijanpalic.urlshortener.model.UrlEncoding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlEncodingRepository extends JpaRepository<UrlEncoding, Long> {

    Optional<UrlEncoding> findOneByShortUrl(String shortUrl);
    List<UrlEncoding> findAllByOwnerAccountId(String ownerAccountId);
}
