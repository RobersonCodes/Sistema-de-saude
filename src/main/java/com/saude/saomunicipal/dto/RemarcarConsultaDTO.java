package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Dados para remarcação da consulta")
public record RemarcarConsultaDTO(

        @Schema(description = "Nova data e hora da consulta", example = "2026-04-15T09:00:00")
        @NotNull(message = "Nova data e hora são obrigatórias")
        @Future(message = "A nova consulta deve estar no futuro")
        LocalDateTime novaDataHora

) {
}