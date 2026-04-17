package com.saude.saomunicipal.integration.sus.dto;

import java.time.LocalDateTime;

public record ConsultaSusDTO(
        Long consultaId,
        LocalDateTime dataHora,
        String status,
        String observacoes,
        PacienteSusDTO paciente,
        ProfissionalSusDTO profissional,
        EstabelecimentoSusDTO estabelecimento
) {
}