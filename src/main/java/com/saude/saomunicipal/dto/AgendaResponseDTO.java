package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Resposta com informações da agenda do profissional de saúde")
public record AgendaResponseDTO(

        @Schema(description = "Identificador único da agenda", example = "1")
        Long id,

        @Schema(description = "Data do atendimento", example = "2026-04-10")
        LocalDate data,

        @Schema(description = "Horário inicial da consulta", example = "08:00")
        LocalTime horaInicio,

        @Schema(description = "Indica se o horário está livre para agendamento", example = "true")
        Boolean disponivel,

        @Schema(description = "Identificador do profissional de saúde", example = "1")
        Long profissionalId,

        @Schema(description = "Identificador da unidade de saúde", example = "2")
        Long unidadeId

) {
}