package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.PageResponseDTO;
import com.saude.saomunicipal.dto.PacienteFiltroDTO;
import com.saude.saomunicipal.dto.PacienteRequestDTO;
import com.saude.saomunicipal.dto.PacienteResponseDTO;
import com.saude.saomunicipal.entity.Paciente;
import com.saude.saomunicipal.entity.UnidadeSaude;
import com.saude.saomunicipal.exception.BusinessException;
import com.saude.saomunicipal.repository.PacienteRepository;
import com.saude.saomunicipal.repository.UnidadeSaudeRepository;
import com.saude.saomunicipal.specification.PacienteSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UnidadeSaudeRepository unidadeRepository;

    public PacienteResponseDTO cadastrar(PacienteRequestDTO dto) {
        if (pacienteRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF já cadastrado.");
        }

        UnidadeSaude unidade = unidadeRepository.findById(dto.unidadeId())
                .orElseThrow(() -> new BusinessException("Unidade de saúde não encontrada."));

        Paciente paciente = Paciente.builder()
                .nomeCompleto(dto.nomeCompleto())
                .cpf(dto.cpf())
                .cns(dto.cns())
                .dataNascimento(dto.dataNascimento())
                .sexo(dto.sexo())
                .telefone(dto.telefone())
                .email(dto.email())
                .endereco(dto.endereco())
                .ativo(true)
                .unidade(unidade)
                .build();

        Paciente salvo = pacienteRepository.save(paciente);

        return toResponseDTO(salvo);
    }

    public PageResponseDTO<PacienteResponseDTO> listar(PacienteFiltroDTO filtro, Pageable pageable) {
        Page<Paciente> page = pacienteRepository.findAll(
                PacienteSpecification.comFiltros(filtro),
                pageable
        );

        Page<PacienteResponseDTO> dtoPage = page.map(this::toResponseDTO);

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

    private PacienteResponseDTO toResponseDTO(Paciente paciente) {
        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNomeCompleto(),
                paciente.getCpf(),
                paciente.getCns(),
                paciente.getDataNascimento(),
                paciente.getSexo(),
                paciente.getTelefone(),
                paciente.getEmail(),
                paciente.getEndereco(),
                paciente.getAtivo(),
                paciente.getUnidade() != null ? paciente.getUnidade().getId() : null
        );
    }
}