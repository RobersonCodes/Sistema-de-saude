package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados do profissional de saúde")
public record ProfissionalSaudeResponseDTO(

        @Schema(description = "ID do profissional", example = "1")
        Long id,

        @Schema(description = "Nome do profissional", example = "Dr. João Silva")
        String nome,

        @Schema(description = "Cargo", example = "Médico")
        String cargo,

        @Schema(description = "Especialidade", example = "Clínico Geral")
        String especialidade,

        @Schema(description = "Telefone", example = "51999998888")
        String telefone,

        @Schema(description = "ID da unidade de saúde", example = "2")
        Long unidadeId

) {
}