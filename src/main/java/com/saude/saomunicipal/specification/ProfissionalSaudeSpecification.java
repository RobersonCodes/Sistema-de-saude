package com.saude.saomunicipal.specification;

import com.saude.saomunicipal.dto.ProfissionalFiltroDTO;
import com.saude.saomunicipal.entity.ProfissionalSaude;
import org.springframework.data.jpa.domain.Specification;

public class ProfissionalSaudeSpecification {

    private ProfissionalSaudeSpecification() {
    }

    public static Specification<ProfissionalSaude> comFiltros(ProfissionalFiltroDTO filtro) {
        Specification<ProfissionalSaude> spec = null;

        if (filtro.nome() != null && !filtro.nome().isBlank()) {
            spec = add(spec, nomeContem(filtro.nome()));
        }

        if (filtro.cargo() != null && !filtro.cargo().isBlank()) {
            spec = add(spec, cargoIgual(filtro.cargo()));
        }

        if (filtro.especialidade() != null && !filtro.especialidade().isBlank()) {
            spec = add(spec, especialidadeContem(filtro.especialidade()));
        }

        if (filtro.unidadeId() != null) {
            spec = add(spec, unidadeIdIgual(filtro.unidadeId()));
        }

        return spec;
    }

    private static Specification<ProfissionalSaude> add(
            Specification<ProfissionalSaude> atual,
            Specification<ProfissionalSaude> nova
    ) {
        return atual == null ? nova : atual.and(nova);
    }

    private static Specification<ProfissionalSaude> nomeContem(String nome) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    private static Specification<ProfissionalSaude> cargoIgual(String cargo) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("cargo")), cargo.toLowerCase());
    }

    private static Specification<ProfissionalSaude> especialidadeContem(String especialidade) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("especialidade")), "%" + especialidade.toLowerCase() + "%");
    }

    private static Specification<ProfissionalSaude> unidadeIdIgual(Long unidadeId) {
        return (root, query, cb) ->
                cb.equal(root.get("unidade").get("id"), unidadeId);
    }
}