package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Horário disponível para agendamento")
public record HorarioDisponivelDTO(

        @Schema(description = "Data do horário", example = "2026-04-15")
        String data,

        @Schema(description = "Hora disponível", example = "09:00")
        String hora,

        @Schema(description = "ID do profissional", example = "1")
        Long profissionalId,

        @Schema(description = "ID da unidade", example = "2")
        Long unidadeId

) {
}