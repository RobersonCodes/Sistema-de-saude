package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do usuário autenticado")
public record MeResponseDTO(

        Long id,
        String nome,
        String email,
        String role

) {
}