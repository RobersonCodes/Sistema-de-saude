package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para cadastro de usuário")
public record RegisterRequestDTO(

        @Schema(example = "Administrador do Sistema")
        String nome,

        @Schema(example = "admin@saude.com")
        String email,

        @Schema(example = "123456")
        String senha,

        @Schema(example = "ADMIN")
        String role

) {
}