package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.PageResponseDTO;
import com.saude.saomunicipal.dto.UnidadeFiltroDTO;
import com.saude.saomunicipal.dto.UnidadeSaudeRequestDTO;
import com.saude.saomunicipal.dto.UnidadeSaudeResponseDTO;
import com.saude.saomunicipal.entity.UnidadeSaude;
import com.saude.saomunicipal.repository.UnidadeSaudeRepository;
import com.saude.saomunicipal.specification.UnidadeSaudeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnidadeSaudeService {

    private final UnidadeSaudeRepository unidadeRepository;

    public UnidadeSaudeResponseDTO cadastrar(UnidadeSaudeRequestDTO dto) {
        UnidadeSaude unidade = UnidadeSaude.builder()
                .nome(dto.nome())
                .tipo(dto.tipo())
                .bairro(dto.bairro())
                .endereco(dto.endereco())
                .telefone(dto.telefone())
                .ativa(true)
                .build();

        UnidadeSaude salva = unidadeRepository.save(unidade);

        return toResponseDTO(salva);
    }

    public PageResponseDTO<UnidadeSaudeResponseDTO> listar(UnidadeFiltroDTO filtro, Pageable pageable) {
        Page<UnidadeSaude> page = unidadeRepository.findAll(
                UnidadeSaudeSpecification.comFiltros(filtro),
                pageable
        );

        Page<UnidadeSaudeResponseDTO> dtoPage = page.map(this::toResponseDTO);

        return new PageResponseDTO<>(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages(),
                dtoPage.isFirst(),
                dtoPage.isLast()
        );
    }

    private UnidadeSaudeResponseDTO toResponseDTO(UnidadeSaude unidade) {
        return new UnidadeSaudeResponseDTO(
                unidade.getId(),
                unidade.getNome(),
                unidade.getTipo(),
                unidade.getBairro(),
                unidade.getEndereco(),
                unidade.getTelefone(),
                unidade.getAtiva()
        );
    }
}