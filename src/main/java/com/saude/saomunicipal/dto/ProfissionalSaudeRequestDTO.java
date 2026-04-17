package com.saude.saomunicipal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfissionalSaudeRequestDTO(
        @NotBlank String nome,
        @NotBlank String cargo,
        String especialidade,
        String telefone,
        @NotNull Long unidadeId
) {
}