package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Estrutura padrão de resposta de erro da API")
public record ErrorResponseDTO(

        @Schema(example = "2026-04-02T15:10:00")
        LocalDateTime timestamp,

        @Schema(example = "400")
        int status,

        @Schema(example = "VALIDATION_ERROR")
        String error,

        @Schema(example = "Um ou mais campos estão inválidos.")
        String message,

        @Schema(example = "/api/v1/pacientes")
        String path,

        @Schema(example = "[\"cpf: CPF inválido\"]")
        List<String> details

) {
}