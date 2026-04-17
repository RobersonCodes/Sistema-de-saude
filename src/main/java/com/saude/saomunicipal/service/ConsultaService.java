package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.AgendamentoCidadaoRequestDTO;
import com.saude.saomunicipal.dto.AtualizarStatusConsultaDTO;
import com.saude.saomunicipal.dto.ConsultaPorCpfResponseDTO;
import com.saude.saomunicipal.dto.ConsultaRequestDTO;
import com.saude.saomunicipal.dto.ConsultaResponseDTO;
import com.saude.saomunicipal.dto.RemarcarConsultaDTO;
import com.saude.saomunicipal.entity.Consulta;
import com.saude.saomunicipal.entity.Paciente;
import com.saude.saomunicipal.entity.ProfissionalSaude;
import com.saude.saomunicipal.entity.StatusConsulta;
import com.saude.saomunicipal.entity.UnidadeSaude;
import com.saude.saomunicipal.exception.BusinessException;
import com.saude.saomunicipal.integration.sus.service.SusIntegrationService;
import com.saude.saomunicipal.repository.ConsultaRepository;
import com.saude.saomunicipal.repository.PacienteRepository;
import com.saude.saomunicipal.repository.ProfissionalSaudeRepository;
import com.saude.saomunicipal.repository.UnidadeSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalRepository;
    private final UnidadeSaudeRepository unidadeRepository;
    private final SusIntegrationService susIntegrationService;

    public ConsultaResponseDTO cadastrar(ConsultaRequestDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new BusinessException("Paciente não encontrado."));

        ProfissionalSaude profissional = profissionalRepository.findById(dto.profissionalId())
                .orElseThrow(() -> new BusinessException("Profissional não encontrado."));

        UnidadeSaude unidade = unidadeRepository.findById(dto.unidadeId())
                .orElseThrow(() -> new BusinessException("Unidade não encontrada."));

        validarAgendamento(
                paciente.getId(),
                profissional.getId(),
                unidade.getId(),
                profissional.getUnidade().getId(),
                dto.dataHora()
        );

        Consulta consulta = Consulta.builder()
                .dataHora(dto.dataHora())
                .status(StatusConsulta.AGENDADA)
                .observacoes(dto.observacoes())
                .paciente(paciente)
                .profissional(profissional)
                .unidade(unidade)
                .build();

        Consulta salva = consultaRepository.save(consulta);

        tentarEnviarIntegracaoSus(salva.getId());

        return toResponseDTO(salva);
    }

    public ConsultaResponseDTO agendarPorDocumento(AgendamentoCidadaoRequestDTO dto) {
        Paciente paciente = pacienteRepository.findByCpfOrCns(dto.documento(), dto.documento())
                .orElseThrow(() -> new BusinessException("Paciente não encontrado para o documento informado."));

        ProfissionalSaude profissional = profissionalRepository.findById(dto.profissionalId())
                .orElseThrow(() -> new BusinessException("Profissional não encontrado."));

        UnidadeSaude unidade = unidadeRepository.findById(dto.unidadeId())
                .orElseThrow(() -> new BusinessException("Unidade não encontrada."));

        validarAgendamento(
                paciente.getId(),
                profissional.getId(),
                unidade.getId(),
                profissional.getUnidade().getId(),
                dto.dataHora()
        );

        Consulta consulta = Consulta.builder()
                .dataHora(dto.dataHora())
                .status(StatusConsulta.AGENDADA)
                .observacoes(dto.observacoes())
                .paciente(paciente)
                .profissional(profissional)
                .unidade(unidade)
                .build();

        Consulta salva = consultaRepository.save(consulta);

        tentarEnviarIntegracaoSus(salva.getId());

        return toResponseDTO(salva);
    }

    public Page<ConsultaResponseDTO> listar(Pageable pageable) {
        return consultaRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    public List<ConsultaPorCpfResponseDTO> listarPorCpf(String cpf) {
        Paciente paciente = pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new BusinessException("Paciente não encontrado para o CPF informado."));

        return consultaRepository.findByPacienteId(paciente.getId())
                .stream()
                .map(this::toConsultaDocumentoDTO)
                .toList();
    }

    public List<ConsultaPorCpfResponseDTO> listarPorDocumento(String documento) {
        Paciente paciente = pacienteRepository.findByCpfOrCns(documento, documento)
                .orElseThrow(() -> new BusinessException("Paciente não encontrado para o documento informado."));

        return consultaRepository.findByPacienteId(paciente.getId())
                .stream()
                .map(this::toConsultaDocumentoDTO)
                .toList();
    }

    public ConsultaResponseDTO atualizarStatus(Long id, AtualizarStatusConsultaDTO dto) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Consulta não encontrada."));

        validarMudancaStatus(consulta.getStatus(), dto.getStatus());

        consulta.setStatus(dto.getStatus());

        Consulta atualizada = consultaRepository.save(consulta);

        return toResponseDTO(atualizada);
    }

    public ConsultaResponseDTO cancelarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Consulta não encontrada."));

        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new BusinessException("Consulta já está cancelada.");
        }

        if (consulta.getStatus() == StatusConsulta.REALIZADA) {
            throw new BusinessException("Consulta já foi realizada e não pode ser cancelada.");
        }

        consulta.setStatus(StatusConsulta.CANCELADA);

        return toResponseDTO(consultaRepository.save(consulta));
    }

    public ConsultaResponseDTO remarcarConsulta(Long id, RemarcarConsultaDTO dto) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Consulta não encontrada."));

        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new BusinessException("Consulta cancelada não pode ser remarcada.");
        }

        if (consulta.getStatus() == StatusConsulta.REALIZADA) {
            throw new BusinessException("Consulta já foi finalizada e não pode ser remarcada.");
        }

        validarRemarcacao(
                consulta.getId(),
                consulta.getPaciente().getId(),
                consulta.getProfissional().getId(),
                consulta.getUnidade().getId(),
                consulta.getProfissional().getUnidade().getId(),
                dto.novaDataHora()
        );

        consulta.setDataHora(dto.novaDataHora());
        consulta.setStatus(StatusConsulta.REMARCADA);

        Consulta atualizada = consultaRepository.save(consulta);

        return toResponseDTO(atualizada);
    }

    private void validarAgendamento(
            Long pacienteId,
            Long profissionalId,
            Long unidadeId,
            Long unidadeDoProfissionalId,
            LocalDateTime dataHora
    ) {
        if (dataHora == null) {
            throw new BusinessException("Data e hora da consulta são obrigatórias.");
        }

        if (!dataHora.isAfter(LocalDateTime.now())) {
            throw new BusinessException("Não é permitido agendar consulta em data/hora passada.");
        }

        if (!unidadeDoProfissionalId.equals(unidadeId)) {
            throw new BusinessException("O profissional não pertence à unidade informada.");
        }

        boolean profissionalOcupado = consultaRepository
                .existsByProfissionalIdAndDataHoraAndStatusNot(
                        profissionalId,
                        dataHora,
                        StatusConsulta.CANCELADA
                );

        if (profissionalOcupado) {
            throw new BusinessException("Já existe consulta para este profissional neste horário.");
        }

        boolean pacienteOcupado = consultaRepository
                .existsByPacienteIdAndDataHoraAndStatusNot(
                        pacienteId,
                        dataHora,
                        StatusConsulta.CANCELADA
                );

        if (pacienteOcupado) {
            throw new BusinessException("O paciente já possui consulta neste horário.");
        }
    }

    private void validarRemarcacao(
            Long consultaId,
            Long pacienteId,
            Long profissionalId,
            Long unidadeId,
            Long unidadeDoProfissionalId,
            LocalDateTime novaDataHora
    ) {
        if (novaDataHora == null) {
            throw new BusinessException("Nova data e hora são obrigatórias.");
        }

        if (!novaDataHora.isAfter(LocalDateTime.now())) {
            throw new BusinessException("Não é permitido remarcar para data/hora passada.");
        }

        if (!unidadeDoProfissionalId.equals(unidadeId)) {
            throw new BusinessException("O profissional não pertence à unidade informada.");
        }

        List<Consulta> consultasDoProfissional = consultaRepository.findAll().stream()
                .filter(c -> c.getId() != null && !c.getId().equals(consultaId))
                .filter(c -> c.getProfissional().getId().equals(profissionalId))
                .filter(c -> c.getDataHora().equals(novaDataHora))
                .filter(c -> c.getStatus() != StatusConsulta.CANCELADA)
                .toList();

        if (!consultasDoProfissional.isEmpty()) {
            throw new BusinessException("Já existe consulta para este profissional neste novo horário.");
        }

        List<Consulta> consultasDoPaciente = consultaRepository.findAll().stream()
                .filter(c -> c.getId() != null && !c.getId().equals(consultaId))
                .filter(c -> c.getPaciente().getId().equals(pacienteId))
                .filter(c -> c.getDataHora().equals(novaDataHora))
                .filter(c -> c.getStatus() != StatusConsulta.CANCELADA)
                .toList();

        if (!consultasDoPaciente.isEmpty()) {
            throw new BusinessException("O paciente já possui outra consulta neste novo horário.");
        }
    }

    private void validarMudancaStatus(StatusConsulta atual, StatusConsulta novo) {
        if (atual == StatusConsulta.CANCELADA) {
            throw new BusinessException("Consulta cancelada não pode ser alterada.");
        }

        if (atual == StatusConsulta.REALIZADA) {
            throw new BusinessException("Consulta já foi finalizada.");
        }

        if (atual == novo) {
            throw new BusinessException("Consulta já está com esse status.");
        }
    }

    private void tentarEnviarIntegracaoSus(Long consultaId) {
        try {
            susIntegrationService.enviarConsultaParaEsusAps(consultaId);
        } catch (Exception e) {
            System.out.println("Falha na integração SUS da consulta " + consultaId + ": " + e.getMessage());
        }
    }

    private ConsultaResponseDTO toResponseDTO(Consulta consulta) {
        return new ConsultaResponseDTO(
                consulta.getId(),
                consulta.getDataHora(),
                consulta.getStatus(),
                consulta.getObservacoes(),
                consulta.getPaciente().getId(),
                consulta.getProfissional().getId(),
                consulta.getUnidade().getId()
        );
    }

    private ConsultaPorCpfResponseDTO toConsultaDocumentoDTO(Consulta consulta) {
        return new ConsultaPorCpfResponseDTO(
                consulta.getId(),
                consulta.getDataHora(),
                consulta.getStatus(),
                consulta.getObservacoes(),
                consulta.getProfissional().getId(),
                consulta.getUnidade().getId()
        );
    }
}