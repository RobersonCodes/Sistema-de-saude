package com.saude.saomunicipal.dto;

import com.saude.saomunicipal.entity.StatusConsulta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados retornados de uma consulta médica")
public record ConsultaResponseDTO(

        @Schema(
                description = "ID da consulta",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Data e hora da consulta",
                example = "2026-04-10T14:30:00"
        )
        LocalDateTime dataHora,

        @Schema(
                description = "Status atual da consulta",
                example = "AGENDADA"
        )
        StatusConsulta status,

        @Schema(
                description = "Observações da consulta",
                example = "Paciente com dor de cabeça persistente"
        )
        String observacoes,

        @Schema(
                description = "ID do paciente",
                example = "5"
        )
        Long pacienteId,

        @Schema(
                description = "ID do profissional de saúde",
                example = "2"
        )
        Long profissionalId,

        @Schema(
                description = "ID da unidade de saúde",
                example = "1"
        )
        Long unidadeId

) {
}