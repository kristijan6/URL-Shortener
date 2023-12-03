package com.kristijanpalic.urlshortener.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class UrlEncoding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;

    @Column(unique = true)
    private String shortUrl;
    private Integer numberOfTimesUsed;
    private String ownerAccountId;
    private Integer redirectType;

    public UrlEncoding(String originalUrl, String shortUrl, String ownerAccountId, Integer redirectType) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.numberOfTimesUsed = 0;
        this.ownerAccountId = ownerAccountId;
        this.redirectType = redirectType;
    }
}
