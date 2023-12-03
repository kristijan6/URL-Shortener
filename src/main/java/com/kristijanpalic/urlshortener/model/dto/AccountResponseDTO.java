package com.kristijanpalic.urlshortener.model.dto;

import lombok.Data;

@Data
public class AccountResponseDTO {
    private boolean success;
    private String description;
    private String password;
}
