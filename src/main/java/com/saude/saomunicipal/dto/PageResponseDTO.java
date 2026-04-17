package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resposta paginada da API")
public record PageResponseDTO<T>(

        @Schema(description = "Conteúdo da página atual")
        List<T> content,

        @Schema(description = "Número da página atual", example = "0")
        int page,

        @Schema(description = "Quantidade de registros por página", example = "10")
        int size,

        @Schema(description = "Total de elementos", example = "25")
        long totalElements,

        @Schema(description = "Total de páginas", example = "3")
        int totalPages,

        @Schema(description = "Indica se é a primeira página", example = "true")
        boolean first,

        @Schema(description = "Indica se é a última página", example = "false")
        boolean last

) {
}