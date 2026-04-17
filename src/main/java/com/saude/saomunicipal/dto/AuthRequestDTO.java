package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para login no sistema")
public record AuthRequestDTO(

        @Schema(example = "admin@saude.com")
        String email,

        @Schema(example = "123456")
        String senha

) {
}