package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filtros para busca de profissionais de saúde")
public record ProfissionalFiltroDTO(

        @Schema(description = "Nome do profissional", example = "João")
        String nome,

        @Schema(description = "Cargo do profissional", example = "Médico")
        String cargo,

        @Schema(description = "Especialidade", example = "Clínico Geral")
        String especialidade,

        @Schema(description = "ID da unidade", example = "1")
        Long unidadeId

) {
}