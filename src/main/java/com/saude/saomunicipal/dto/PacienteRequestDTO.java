package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Dados para cadastro de paciente")
public record PacienteRequestDTO(

        @Schema(
                description = "Nome completo do paciente",
                example = "Maria da Silva"
        )
        @NotBlank(message = "Nome completo é obrigatório")
        @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
        String nomeCompleto,

        @Schema(
                description = "CPF do paciente (apenas números)",
                example = "12345678901"
        )
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(
                regexp = "\\d{11}",
                message = "CPF deve conter 11 dígitos numéricos"
        )
        String cpf,

        @Schema(
                description = "Cartão Nacional de Saúde",
                example = "123456789012345"
        )
        @Size(max = 20, message = "CNS deve ter no máximo 20 caracteres")
        String cns,

        @Schema(
                description = "Data de nascimento",
                example = "1990-05-20"
        )
        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve estar no passado")
        LocalDate dataNascimento,

        @Schema(
                description = "Sexo do paciente",
                example = "FEMININO"
        )
        @Size(max = 20)
        String sexo,

        @Schema(
                description = "Telefone do paciente",
                example = "51999998888"
        )
        @Pattern(
                regexp = "\\d{10,11}",
                message = "Telefone deve conter 10 ou 11 dígitos"
        )
        String telefone,

        @Schema(
                description = "Email do paciente",
                example = "maria@email.com"
        )
        @Email(message = "Email inválido")
        @Size(max = 120)
        String email,

        @Schema(
                description = "Endereço completo",
                example = "Rua das Flores, 123"
        )
        @Size(max = 255)
        String endereco,

        @Schema(
                description = "ID da unidade de saúde",
                example = "1"
        )
        @NotNull(message = "Unidade de saúde é obrigatória")
        Long unidadeId

) {
}