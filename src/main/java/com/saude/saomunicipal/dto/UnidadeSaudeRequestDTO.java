package com.saude.saomunicipal.dto;

import jakarta.validation.constraints.NotBlank;

public record UnidadeSaudeRequestDTO(
        @NotBlank String nome,
        @NotBlank String tipo,
        @NotBlank String bairro,
        @NotBlank String endereco,
        String telefone
) {
}