package com.saude.saomunicipal.dto;

import java.util.List;

public record IntencaoIAResponseDTO(

        String intent,
        String mensagemNormalizada,
        List<CampoExtraidoDTO> campos

) {
}