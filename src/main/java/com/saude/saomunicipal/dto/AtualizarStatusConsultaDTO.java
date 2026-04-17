package com.saude.saomunicipal.dto;

import com.saude.saomunicipal.entity.StatusConsulta;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AtualizarStatusConsultaDTO {

    @NotNull(message = "Status é obrigatório")
    private StatusConsulta status;
}