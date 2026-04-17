package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filtros para busca de pacientes")
public record PacienteFiltroDTO(

        @Schema(description = "Nome do paciente", example = "Maria")
        String nome,

        @Schema(description = "CPF do paciente", example = "12345678901")
        String cpf,

        @Schema(description = "ID da unidade de saúde", example = "2")
        Long unidadeId,

        @Schema(description = "Status do paciente", example = "true")
        Boolean ativo

) {
}