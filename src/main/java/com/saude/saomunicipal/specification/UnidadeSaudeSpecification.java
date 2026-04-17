package com.saude.saomunicipal.specification;

import com.saude.saomunicipal.dto.UnidadeFiltroDTO;
import com.saude.saomunicipal.entity.UnidadeSaude;
import org.springframework.data.jpa.domain.Specification;

public class UnidadeSaudeSpecification {

    private UnidadeSaudeSpecification() {
    }

    public static Specification<UnidadeSaude> comFiltros(UnidadeFiltroDTO filtro) {
        Specification<UnidadeSaude> spec = null;

        if (filtro.nome() != null && !filtro.nome().isBlank()) {
            spec = add(spec, nomeContem(filtro.nome()));
        }

        if (filtro.tipo() != null && !filtro.tipo().isBlank()) {
            spec = add(spec, tipoIgual(filtro.tipo()));
        }

        if (filtro.bairro() != null && !filtro.bairro().isBlank()) {
            spec = add(spec, bairroContem(filtro.bairro()));
        }

        if (filtro.ativa() != null) {
            spec = add(spec, ativaIgual(filtro.ativa()));
        }

        return spec;
    }

    private static Specification<UnidadeSaude> add(
            Specification<UnidadeSaude> atual,
            Specification<UnidadeSaude> nova
    ) {
        return atual == null ? nova : atual.and(nova);
    }

    private static Specification<UnidadeSaude> nomeContem(String nome) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    private static Specification<UnidadeSaude> tipoIgual(String tipo) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("tipo")), tipo.toLowerCase());
    }

    private static Specification<UnidadeSaude> bairroContem(String bairro) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("bairro")), "%" + bairro.toLowerCase() + "%");
    }

    private static Specification<UnidadeSaude> ativaIgual(Boolean ativa) {
        return (root, query, cb) ->
                cb.equal(root.get("ativa"), ativa);
    }
}