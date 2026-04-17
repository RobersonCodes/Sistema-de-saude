package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.AgendaRequestDTO;
import com.saude.saomunicipal.dto.AgendaResponseDTO;
import com.saude.saomunicipal.dto.HorarioDisponivelDTO;
import com.saude.saomunicipal.entity.AgendaProfissional;
import com.saude.saomunicipal.entity.ProfissionalSaude;
import com.saude.saomunicipal.entity.UnidadeSaude;
import com.saude.saomunicipal.exception.BusinessException;
import com.saude.saomunicipal.repository.AgendaProfissionalRepository;
import com.saude.saomunicipal.repository.ProfissionalSaudeRepository;
import com.saude.saomunicipal.repository.UnidadeSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaProfissionalRepository agendaProfissionalRepository;
    private final ProfissionalSaudeRepository profissionalRepository;
    private final UnidadeSaudeRepository unidadeRepository;

    public AgendaResponseDTO cadastrar(AgendaRequestDTO dto) {
        ProfissionalSaude profissional = profissionalRepository.findById(dto.profissionalId())
                .orElseThrow(() -> new BusinessException("Profissional não encontrado."));

        UnidadeSaude unidade = unidadeRepository.findById(dto.unidadeId())
                .orElseThrow(() -> new BusinessException("Unidade não encontrada."));

        AgendaProfissional agenda = AgendaProfissional.builder()
                .data(dto.data())
                .horaInicio(dto.horaInicio())
                .disponivel(true)
                .profissional(profissional)
                .unidade(unidade)
                .build();

        AgendaProfissional salva = agendaProfissionalRepository.save(agenda);

        return toResponseDTO(salva);
    }

    public List<AgendaResponseDTO> listarPorProfissionalEData(Long profissionalId, LocalDate data) {
        return agendaProfissionalRepository.findByProfissionalIdAndData(profissionalId, data)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AgendaResponseDTO> listarDisponiveisPorProfissionalEData(Long profissionalId, LocalDate data) {
        return agendaProfissionalRepository.findByProfissionalIdAndDataAndDisponivelTrue(profissionalId, data)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AgendaResponseDTO> listarDisponiveisPorUnidadeEData(Long unidadeId, LocalDate data) {
        return agendaProfissionalRepository.findByUnidadeIdAndDataAndDisponivelTrue(unidadeId, data)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<HorarioDisponivelDTO> listarHorariosDisponiveisPorProfissional(Long profissionalId, LocalDate data) {
        return agendaProfissionalRepository.findByProfissionalIdAndDataAndDisponivelTrue(profissionalId, data)
                .stream()
                .map(this::toHorarioDisponivelDTO)
                .toList();
    }

    public List<HorarioDisponivelDTO> listarHorariosDisponiveisPorUnidade(Long unidadeId, LocalDate data) {
        return agendaProfissionalRepository.findByUnidadeIdAndDataAndDisponivelTrue(unidadeId, data)
                .stream()
                .map(this::toHorarioDisponivelDTO)
                .toList();
    }

    private AgendaResponseDTO toResponseDTO(AgendaProfissional agenda) {
        return new AgendaResponseDTO(
                agenda.getId(),
                agenda.getData(),
                agenda.getHoraInicio(),
                agenda.getDisponivel(),
                agenda.getProfissional().getId(),
                agenda.getUnidade().getId()
        );
    }

    private HorarioDisponivelDTO toHorarioDisponivelDTO(AgendaProfissional agenda) {
        return new HorarioDisponivelDTO(
                agenda.getData().format(DateTimeFormatter.ISO_LOCAL_DATE),
                agenda.getHoraInicio().toString(),
                agenda.getProfissional().getId(),
                agenda.getUnidade().getId()
        );
    }
}