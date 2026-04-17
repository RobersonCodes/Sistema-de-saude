package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Dados retornados de um paciente")
public record PacienteResponseDTO(

        @Schema(
                description = "ID do paciente",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Nome completo",
                example = "Maria da Silva"
        )
        String nomeCompleto,

        @Schema(
                description = "CPF",
                example = "12345678901"
        )
        String cpf,

        @Schema(
                description = "Cartão Nacional de Saúde",
                example = "123456789012345"
        )
        String cns,

        @Schema(
                description = "Data de nascimento",
                example = "1990-05-20"
        )
        LocalDate dataNascimento,

        @Schema(
                description = "Sexo",
                example = "FEMININO"
        )
        String sexo,

        @Schema(
                description = "Telefone",
                example = "51999998888"
        )
        String telefone,

        @Schema(
                description = "Email",
                example = "maria@email.com"
        )
        String email,

        @Schema(
                description = "Endereço",
                example = "Rua Central, 123"
        )
        String endereco,

        @Schema(
                description = "Indica se o paciente está ativo",
                example = "true"
        )
        Boolean ativo,

        @Schema(
                description = "ID da unidade de saúde vinculada",
                example = "2"
        )
        Long unidadeId

) {
}