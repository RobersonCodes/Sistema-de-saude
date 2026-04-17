package com.saude.saomunicipal.integration.sus.dto;

public record ProfissionalSusDTO(
        Long profissionalId,
        String nome,
        String cargo,
        String especialidade,
        String telefone,
        String cbo,
        String registroConselho,
        String ufConselho,
        Long unidadeId
) {
}