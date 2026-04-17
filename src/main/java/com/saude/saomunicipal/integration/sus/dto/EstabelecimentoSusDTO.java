package com.saude.saomunicipal.integration.sus.dto;

public record EstabelecimentoSusDTO(
        Long unidadeId,
        String nome,
        String tipo,
        String cnes,
        String bairro,
        String endereco,
        String telefone
) {
}