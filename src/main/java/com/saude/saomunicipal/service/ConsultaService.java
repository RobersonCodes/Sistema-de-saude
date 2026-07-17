package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.AgendamentoCidadaoRequestDTO;
import com.saude.saomunicipal.dto.AtualizarStatusConsultaDTO;
import com.saude.saomunicipal.dto.ConsultaPorCpfResponseDTO;
import com.saude.saomunicipal.dto.ConsultaRequestDTO;
import com.saude.saomunicipal.dto.ConsultaResponseDTO;
import com.saude.saomunicipal.dto.PageResponseDTO;
import com.saude.saomunicipal.dto.RemarcarConsultaDTO;
import com.saude.saomunicipal.entity.AgendaProfissional;
import com.saude.saomunicipal.entity.Consulta;
import com.saude.saomunicipal.entity.Paciente;
import com.saude.saomunicipal.entity.ProfissionalSaude;
import com.saude.saomunicipal.entity.StatusConsulta;
import com.saude.saomunicipal.entity.UnidadeSaude;
import com.saude.saomunicipal.exception.BusinessException;
import com.saude.saomunicipal.integration.sus.service.SusIntegrationService;
import com.saude.saomunicipal.repository.AgendaProfissionalRepository;
import com.saude.saomunicipal.repository.ConsultaRepository;
import com.saude.saomunicipal.repository.PacienteRepository;
import com.saude.saomunicipal.repository.ProfissionalSaudeRepository;
import com.saude.saomunicipal.repository.UnidadeSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private static final Logger log = LoggerFactory.getLogger(ConsultaService.class);

    private static final Map<StatusConsulta, Set<StatusConsulta>> TRANSICOES_VALIDAS = Map.of(
            StatusConsulta.AGENDADA, Set.of(StatusConsulta.CONFIRMADA, StatusConsulta.CANCELADA, StatusConsulta.FALTOU),
            StatusConsulta.CONFIRMADA, Set.of(StatusConsulta.EM_ATENDIMENTO, StatusConsulta.CANCELADA, StatusConsulta.FALTOU),
            StatusConsulta.EM_ATENDIMENTO, Set.of(StatusConsulta.REALIZADA, StatusConsulta.CANCELADA),
            StatusConsulta.REMARCADA, Set.of(StatusConsulta.CONFIRMADA, StatusConsulta.CANCELADA, StatusConsulta.FALTOU),
            StatusConsulta.REALIZADA, Set.of(),
            StatusConsulta.CANCELADA, Set.of(),
            StatusConsulta.FALTOU, Set.of()
    );

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalRepository;
    private final UnidadeSaudeRepository unidadeRepository;
    private final AgendaProfissionalRepository agendaProfissionalRepository;
    private final SusIntegrationService susIntegrationService;

    @Transactional
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

        AgendaProfissional slot = reservarSlot(profissional.getId(), dto.dataHora());

        Consulta consulta = Consulta.builder()
                .dataHora(dto.dataHora())
                .status(StatusConsulta.AGENDADA)
                .observacoes(dto.observacoes())
                .paciente(paciente)
                .profissional(profissional)
                .unidade(unidade)
                .agenda(slot)
                .build();

        Consulta salva = consultaRepository.save(consulta);

        tentarEnviarIntegracaoSus(salva.getId());

        return toResponseDTO(salva);
    }

    @Transactional
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

        AgendaProfissional slot = reservarSlot(profissional.getId(), dto.dataHora());

        Consulta consulta = Consulta.builder()
                .dataHora(dto.dataHora())
                .status(StatusConsulta.AGENDADA)
                .observacoes(dto.observacoes())
                .paciente(paciente)
                .profissional(profissional)
                .unidade(unidade)
                .agenda(slot)
                .build();

        Consulta salva = consultaRepository.save(consulta);

        tentarEnviarIntegracaoSus(salva.getId());

        return toResponseDTO(salva);
    }

    public PageResponseDTO<ConsultaResponseDTO> listar(Pageable pageable) {
        Page<ConsultaResponseDTO> dtoPage = consultaRepository.findAll(pageable)
                .map(this::toResponseDTO);

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

    @Transactional
    public ConsultaResponseDTO atualizarStatus(Long id, AtualizarStatusConsultaDTO dto) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Consulta não encontrada."));

        validarMudancaStatus(consulta.getStatus(), dto.getStatus());

        consulta.setStatus(dto.getStatus());

        if (dto.getStatus() == StatusConsulta.CANCELADA) {
            liberarSlot(consulta);
        }

        Consulta atualizada = consultaRepository.save(consulta);

        return toResponseDTO(atualizada);
    }

    @Transactional
    public ConsultaResponseDTO cancelarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Consulta não encontrada."));

        validarMudancaStatus(consulta.getStatus(), StatusConsulta.CANCELADA);

        consulta.setStatus(StatusConsulta.CANCELADA);
        liberarSlot(consulta);

        return toResponseDTO(consultaRepository.save(consulta));
    }

    @Transactional
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

        AgendaProfissional slotAntigo = consulta.getAgenda();
        AgendaProfissional slotNovo = reservarSlot(consulta.getProfissional().getId(), dto.novaDataHora());

        if (slotAntigo != null) {
            slotAntigo.setDisponivel(true);
            agendaProfissionalRepository.save(slotAntigo);
        }

        consulta.setAgenda(slotNovo);
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

        boolean profissionalOcupado = consultaRepository
                .existsByProfissionalIdAndDataHoraAndStatusNotAndIdNot(
                        profissionalId,
                        novaDataHora,
                        StatusConsulta.CANCELADA,
                        consultaId
                );

        if (profissionalOcupado) {
            throw new BusinessException("Já existe consulta para este profissional neste novo horário.");
        }

        boolean pacienteOcupado = consultaRepository
                .existsByPacienteIdAndDataHoraAndStatusNotAndIdNot(
                        pacienteId,
                        novaDataHora,
                        StatusConsulta.CANCELADA,
                        consultaId
                );

        if (pacienteOcupado) {
            throw new BusinessException("O paciente já possui outra consulta neste novo horário.");
        }
    }

    private void validarMudancaStatus(StatusConsulta atual, StatusConsulta novo) {
        if (novo == StatusConsulta.REMARCADA) {
            throw new BusinessException("Para remarcar uma consulta, utilize o endpoint de remarcação.");
        }

        Set<StatusConsulta> permitidos = TRANSICOES_VALIDAS.getOrDefault(atual, Set.of());

        if (!permitidos.contains(novo)) {
            throw new BusinessException(
                    "Transição de status inválida: " + atual + " -> " + novo + "."
            );
        }
    }

    /**
     * Reserva atomicamente um horário publicado na agenda do profissional
     * para a data/hora exata da consulta. Usa lock pessimista na linha da
     * agenda para impedir que duas requisições concorrentes reservem o
     * mesmo horário.
     */
    private AgendaProfissional reservarSlot(Long profissionalId, LocalDateTime dataHora) {
        AgendaProfissional slot = agendaProfissionalRepository
                .buscarParaReserva(profissionalId, dataHora.toLocalDate(), dataHora.toLocalTime())
                .orElseThrow(() -> new BusinessException(
                        "Não há horário publicado na agenda deste profissional para a data/hora informada."
                ));

        if (!Boolean.TRUE.equals(slot.getDisponivel())) {
            throw new BusinessException("Este horário já foi reservado.");
        }

        slot.setDisponivel(false);

        return agendaProfissionalRepository.save(slot);
    }

    private void liberarSlot(Consulta consulta) {
        AgendaProfissional slot = consulta.getAgenda();

        if (slot != null) {
            slot.setDisponivel(true);
            agendaProfissionalRepository.save(slot);
            consulta.setAgenda(null);
        }
    }

    private void tentarEnviarIntegracaoSus(Long consultaId) {
        try {
            susIntegrationService.enviarConsultaParaEsusAps(consultaId);
        } catch (Exception e) {
            log.error("Falha na integração SUS da consulta {}", consultaId, e);
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
