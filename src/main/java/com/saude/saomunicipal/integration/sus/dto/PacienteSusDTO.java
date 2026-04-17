package com.saude.saomunicipal.integration.sus.dto;

import java.time.LocalDate;

public record PacienteSusDTO(
        Long pacienteId,
        String nomeCompleto,
        String cpf,
        String cns,
        LocalDate dataNascimento,
        String sexo,
        String telefone,
        String email,
        String endereco,
        Long unidadeId
) {
}