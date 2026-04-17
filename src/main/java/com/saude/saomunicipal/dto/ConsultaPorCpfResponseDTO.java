package com.saude.saomunicipal.dto;

import com.saude.saomunicipal.entity.StatusConsulta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Consulta retornada para o cidadão por CPF")
public record ConsultaPorCpfResponseDTO(

        @Schema(example = "1")
        Long id,

        @Schema(example = "2026-04-15T09:00:00")
        LocalDateTime dataHora,

        @Schema(example = "AGENDADA")
        StatusConsulta status,

        @Schema(example = "Agendamento via WhatsApp")
        String observacoes,

        @Schema(example = "1")
        Long profissionalId,

        @Schema(example = "2")
        Long unidadeId

) {
}