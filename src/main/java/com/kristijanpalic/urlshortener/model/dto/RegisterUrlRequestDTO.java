package com.kristijanpalic.urlshortener.model.dto;

import lombok.Data;

@Data
public class RegisterUrlRequestDTO {
    private String url;
    private Integer redirectType = 302;
}
