package com.saude.saomunicipal.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Status da consulta médica")
public enum StatusConsulta {

    AGENDADA("Agendada"),

    CONFIRMADA("Confirmada presença do paciente"),

    EM_ATENDIMENTO("Paciente em atendimento"),

    REALIZADA("Consulta finalizada"),

    CANCELADA("Consulta cancelada"),

    FALTOU("Paciente não compareceu"),

    REMARCADA("Consulta remarcada para outra data");

    private final String descricao;

    StatusConsulta(String descricao) {
        this.descricao = descricao;
    }
}