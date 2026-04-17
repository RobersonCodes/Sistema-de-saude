package com.saude.saomunicipal.dto;

import jakarta.validation.constraints.NotBlank;

public record MensagemIARequestDTO(

        @NotBlank
        String mensagem

) {
}