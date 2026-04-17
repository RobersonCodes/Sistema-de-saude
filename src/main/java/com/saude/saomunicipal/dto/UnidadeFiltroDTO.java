package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filtros para busca de unidades de saúde")
public record UnidadeFiltroDTO(

        @Schema(description = "Nome da unidade", example = "UBS Centro")
        String nome,

        @Schema(description = "Tipo da unidade", example = "UBS")
        String tipo,

        @Schema(description = "Bairro", example = "Centro")
        String bairro,

        @Schema(description = "Status da unidade", example = "true")
        Boolean ativa

) {
}