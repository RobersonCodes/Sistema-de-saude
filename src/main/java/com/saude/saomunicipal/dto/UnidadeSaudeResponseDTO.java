package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados da unidade de saúde")
public record UnidadeSaudeResponseDTO(

        @Schema(description = "ID da unidade", example = "1")
        Long id,

        @Schema(description = "Nome da unidade", example = "UBS Centro")
        String nome,

        @Schema(description = "Tipo da unidade", example = "UBS")
        String tipo,

        @Schema(description = "Bairro", example = "Centro")
        String bairro,

        @Schema(description = "Endereço", example = "Rua Principal, 100")
        String endereco,

        @Schema(description = "Telefone", example = "5133334444")
        String telefone,

        @Schema(description = "Indica se a unidade está ativa", example = "true")
        Boolean ativa

) {
}