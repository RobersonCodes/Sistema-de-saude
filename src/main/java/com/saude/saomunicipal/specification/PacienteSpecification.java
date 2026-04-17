package com.saude.saomunicipal.specification;

import com.saude.saomunicipal.dto.PacienteFiltroDTO;
import com.saude.saomunicipal.entity.Paciente;
import org.springframework.data.jpa.domain.Specification;

public class PacienteSpecification {

    private PacienteSpecification() {
    }

    public static Specification<Paciente> comFiltros(PacienteFiltroDTO filtro) {
        Specification<Paciente> spec = null;

        if (filtro.nome() != null && !filtro.nome().isBlank()) {
            spec = add(spec, nomeContem(filtro.nome()));
        }

        if (filtro.cpf() != null && !filtro.cpf().isBlank()) {
            spec = add(spec, cpfIgual(filtro.cpf()));
        }

        if (filtro.unidadeId() != null) {
            spec = add(spec, unidadeIdIgual(filtro.unidadeId()));
        }

        if (filtro.ativo() != null) {
            spec = add(spec, ativoIgual(filtro.ativo()));
        }

        return spec;
    }

    private static Specification<Paciente> add(
            Specification<Paciente> atual,
            Specification<Paciente> nova
    ) {
        return atual == null ? nova : atual.and(nova);
    }

    private static Specification<Paciente> nomeContem(String nome) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("nomeCompleto")),
                        "%" + nome.toLowerCase() + "%"
                );
    }

    private static Specification<Paciente> cpfIgual(String cpf) {
        return (root, query, cb) ->
                cb.equal(root.get("cpf"), cpf);
    }

    private static Specification<Paciente> unidadeIdIgual(Long unidadeId) {
        return (root, query, cb) ->
                cb.equal(root.get("unidade").get("id"), unidadeId);
    }

    private static Specification<Paciente> ativoIgual(Boolean ativo) {
        return (root, query, cb) ->
                cb.equal(root.get("ativo"), ativo);
    }
}