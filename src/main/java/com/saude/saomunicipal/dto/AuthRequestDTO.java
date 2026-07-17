package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para login no sistema")
public record AuthRequestDTO(

        @Schema(example = "admin@saude.com")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @Schema(example = "123456")
        @NotBlank(message = "Senha é obrigatória")
        String senha

) {
}
