package com.saude.saomunicipal.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AgendamentoCidadaoRequestDTO(

        @NotBlank(message = "Documento é obrigatório")
        String documento,

        @NotNull(message = "Profissional é obrigatório")
        Long profissionalId,

        @NotNull(message = "Unidade é obrigatória")
        Long unidadeId,

        @NotNull(message = "Data e hora são obrigatórias")
        @Future(message = "A consulta deve ser marcada para o futuro")
        LocalDateTime dataHora,

        @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
        String observacoes

) {
}