package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Dados para cadastro de uma consulta médica")
public record ConsultaRequestDTO(

        @Schema(
                description = "Data e hora da consulta",
                example = "2026-04-10T14:30:00"
        )
        @NotNull(message = "Data e hora são obrigatórias")
        @Future(message = "A consulta deve ser marcada para o futuro")
        LocalDateTime dataHora,

        @Schema(
                description = "Observações da consulta",
                example = "Paciente com dor de cabeça persistente"
        )
        String observacoes,

        @Schema(
                description = "ID do paciente",
                example = "1"
        )
        @NotNull(message = "Paciente é obrigatório")
        Long pacienteId,

        @Schema(
                description = "ID do profissional de saúde",
                example = "2"
        )
        @NotNull(message = "Profissional é obrigatório")
        Long profissionalId,

        @Schema(
                description = "ID da unidade de saúde",
                example = "3"
        )
        @NotNull(message = "Unidade de saúde é obrigatória")
        Long unidadeId

) {
}