package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.PageResponseDTO;
import com.saude.saomunicipal.dto.ProfissionalFiltroDTO;
import com.saude.saomunicipal.dto.ProfissionalSaudeRequestDTO;
import com.saude.saomunicipal.dto.ProfissionalSaudeResponseDTO;
import com.saude.saomunicipal.entity.ProfissionalSaude;
import com.saude.saomunicipal.entity.UnidadeSaude;
import com.saude.saomunicipal.exception.BusinessException;
import com.saude.saomunicipal.repository.ProfissionalSaudeRepository;
import com.saude.saomunicipal.repository.UnidadeSaudeRepository;
import com.saude.saomunicipal.specification.ProfissionalSaudeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfissionalSaudeService {

    private final ProfissionalSaudeRepository profissionalRepository;
    private final UnidadeSaudeRepository unidadeRepository;

    public ProfissionalSaudeResponseDTO cadastrar(ProfissionalSaudeRequestDTO dto) {
        UnidadeSaude unidade = unidadeRepository.findById(dto.unidadeId())
                .orElseThrow(() -> new BusinessException("Unidade de saúde não encontrada."));

        ProfissionalSaude profissional = ProfissionalSaude.builder()
                .nome(dto.nome())
                .cargo(dto.cargo())
                .especialidade(dto.especialidade())
                .telefone(dto.telefone())
                .unidade(unidade)
                .build();

        ProfissionalSaude salvo = profissionalRepository.save(profissional);

        return toResponseDTO(salvo);
    }

    public PageResponseDTO<ProfissionalSaudeResponseDTO> listar(ProfissionalFiltroDTO filtro, Pageable pageable) {
        Page<ProfissionalSaude> page = profissionalRepository.findAll(
                ProfissionalSaudeSpecification.comFiltros(filtro),
                pageable
        );

        Page<ProfissionalSaudeResponseDTO> dtoPage = page.map(this::toResponseDTO);

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

    private ProfissionalSaudeResponseDTO toResponseDTO(ProfissionalSaude profissional) {
        return new ProfissionalSaudeResponseDTO(
                profissional.getId(),
                profissional.getNome(),
                profissional.getCargo(),
                profissional.getEspecialidade(),
                profissional.getTelefone(),
                profissional.getUnidade() != null ? profissional.getUnidade().getId() : null
        );
    }
}