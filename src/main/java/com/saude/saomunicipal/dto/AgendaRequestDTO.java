package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Dados para cadastro de horário na agenda do profissional")
public record AgendaRequestDTO(

        @Schema(description = "Data da agenda", example = "2026-04-10")
        @NotNull(message = "Data é obrigatória")
        @FutureOrPresent(message = "A data da agenda deve ser hoje ou futura")
        LocalDate data,

        @Schema(description = "Hora de início do atendimento", example = "08:00:00")
        @NotNull(message = "Hora de início é obrigatória")
        LocalTime horaInicio,

        @Schema(description = "ID do profissional", example = "1")
        @NotNull(message = "Profissional é obrigatório")
        Long profissionalId,

        @Schema(description = "ID da unidade de saúde", example = "2")
        @NotNull(message = "Unidade de saúde é obrigatória")
        Long unidadeId

) {
}