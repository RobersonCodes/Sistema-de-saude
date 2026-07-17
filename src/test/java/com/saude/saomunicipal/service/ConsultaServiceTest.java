package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.AgendaRequestDTO;
import com.saude.saomunicipal.dto.AtualizarStatusConsultaDTO;
import com.saude.saomunicipal.dto.ConsultaRequestDTO;
import com.saude.saomunicipal.dto.ConsultaResponseDTO;
import com.saude.saomunicipal.dto.RemarcarConsultaDTO;
import com.saude.saomunicipal.entity.AgendaProfissional;
import com.saude.saomunicipal.entity.Paciente;
import com.saude.saomunicipal.entity.ProfissionalSaude;
import com.saude.saomunicipal.entity.StatusConsulta;
import com.saude.saomunicipal.entity.UnidadeSaude;
import com.saude.saomunicipal.exception.BusinessException;
import com.saude.saomunicipal.repository.AgendaProfissionalRepository;
import com.saude.saomunicipal.repository.PacienteRepository;
import com.saude.saomunicipal.repository.ProfissionalSaudeRepository;
import com.saude.saomunicipal.repository.UnidadeSaudeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ConsultaServiceTest {

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private AgendaService agendaService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalRepository;

    @Autowired
    private UnidadeSaudeRepository unidadeRepository;

    @Autowired
    private AgendaProfissionalRepository agendaProfissionalRepository;

    private UnidadeSaude unidade;
    private ProfissionalSaude profissional;
    private Paciente paciente;
    private Paciente outroPaciente;
    private LocalDateTime horarioSlot;

    @BeforeEach
    void setUp() {
        unidade = unidadeRepository.save(UnidadeSaude.builder()
                .nome("UBS Central")
                .tipo("UBS")
                .bairro("Centro")
                .endereco("Rua Principal, 100")
                .ativa(true)
                .build());

        profissional = profissionalRepository.save(ProfissionalSaude.builder()
                .nome("Dr. João")
                .cargo("Médico")
                .especialidade("Clínico Geral")
                .ativo(true)
                .unidade(unidade)
                .build());

        paciente = pacienteRepository.save(Paciente.builder()
                .nomeCompleto("Maria Silva")
                .cpf("11111111111")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .ativo(true)
                .unidade(unidade)
                .build());

        outroPaciente = pacienteRepository.save(Paciente.builder()
                .nomeCompleto("José Souza")
                .cpf("22222222222")
                .dataNascimento(LocalDate.of(1985, 5, 5))
                .ativo(true)
                .unidade(unidade)
                .build());

        horarioSlot = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);

        agendaService.cadastrar(new AgendaRequestDTO(
                horarioSlot.toLocalDate(),
                horarioSlot.toLocalTime(),
                profissional.getId(),
                unidade.getId()
        ));
    }

    @Test
    void naoPermiteAgendarSemHorarioPublicadoNaAgenda() {
        LocalDateTime semSlot = horarioSlot.plusHours(3);

        ConsultaRequestDTO dto = new ConsultaRequestDTO(
                semSlot, "Observação", paciente.getId(), profissional.getId(), unidade.getId()
        );

        assertThatThrownBy(() -> consultaService.cadastrar(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("agenda");
    }

    @Test
    void agendarConsultaReservaOSlotDaAgenda() {
        ConsultaResponseDTO response = consultaService.cadastrar(new ConsultaRequestDTO(
                horarioSlot, "Dor de cabeça", paciente.getId(), profissional.getId(), unidade.getId()
        ));

        assertThat(response.status()).isEqualTo(StatusConsulta.AGENDADA);

        AgendaProfissional slot = agendaProfissionalRepository
                .findByProfissionalIdAndData(profissional.getId(), horarioSlot.toLocalDate())
                .get(0);

        assertThat(slot.getDisponivel()).isFalse();
    }

    @Test
    void naoPermiteDuploAgendamentoNoMesmoHorario() {
        consultaService.cadastrar(new ConsultaRequestDTO(
                horarioSlot, null, paciente.getId(), profissional.getId(), unidade.getId()
        ));

        ConsultaRequestDTO segundaTentativa = new ConsultaRequestDTO(
                horarioSlot, null, outroPaciente.getId(), profissional.getId(), unidade.getId()
        );

        assertThatThrownBy(() -> consultaService.cadastrar(segundaTentativa))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void cancelarConsultaLiberaOHorarioNaAgendaNovamente() {
        ConsultaResponseDTO consulta = consultaService.cadastrar(new ConsultaRequestDTO(
                horarioSlot, null, paciente.getId(), profissional.getId(), unidade.getId()
        ));

        consultaService.cancelarConsulta(consulta.id());

        AgendaProfissional slot = agendaProfissionalRepository
                .findByProfissionalIdAndData(profissional.getId(), horarioSlot.toLocalDate())
                .get(0);

        assertThat(slot.getDisponivel()).isTrue();
    }

    @Test
    void naoPermiteCancelarConsultaJaCancelada() {
        ConsultaResponseDTO consulta = consultaService.cadastrar(new ConsultaRequestDTO(
                horarioSlot, null, paciente.getId(), profissional.getId(), unidade.getId()
        ));

        consultaService.cancelarConsulta(consulta.id());

        assertThatThrownBy(() -> consultaService.cancelarConsulta(consulta.id()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void remarcarConsultaLiberaSlotAntigoEReservaONovo() {
        ConsultaResponseDTO consulta = consultaService.cadastrar(new ConsultaRequestDTO(
                horarioSlot, null, paciente.getId(), profissional.getId(), unidade.getId()
        ));

        LocalDateTime novoHorario = horarioSlot.plusHours(1);
        agendaService.cadastrar(new AgendaRequestDTO(
                novoHorario.toLocalDate(), novoHorario.toLocalTime(), profissional.getId(), unidade.getId()
        ));

        ConsultaResponseDTO remarcada = consultaService.remarcarConsulta(
                consulta.id(), new RemarcarConsultaDTO(novoHorario)
        );

        assertThat(remarcada.status()).isEqualTo(StatusConsulta.REMARCADA);
        assertThat(remarcada.dataHora()).isEqualTo(novoHorario);

        AgendaProfissional slotAntigo = agendaProfissionalRepository
                .findByProfissionalIdAndData(profissional.getId(), horarioSlot.toLocalDate())
                .get(0);
        AgendaProfissional slotNovo = agendaProfissionalRepository
                .findByProfissionalIdAndData(profissional.getId(), novoHorario.toLocalDate())
                .stream()
                .filter(a -> a.getHoraInicio().equals(novoHorario.toLocalTime()))
                .findFirst()
                .orElseThrow();

        assertThat(slotAntigo.getDisponivel()).isTrue();
        assertThat(slotNovo.getDisponivel()).isFalse();
    }

    @Test
    void naoPermitePularEtapasNaMaquinaDeEstados() {
        ConsultaResponseDTO consulta = consultaService.cadastrar(new ConsultaRequestDTO(
                horarioSlot, null, paciente.getId(), profissional.getId(), unidade.getId()
        ));

        AtualizarStatusConsultaDTO paraRealizada = new AtualizarStatusConsultaDTO();
        paraRealizada.setStatus(StatusConsulta.REALIZADA);

        assertThatThrownBy(() -> consultaService.atualizarStatus(consulta.id(), paraRealizada))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void permiteFluxoCompletoDeStatusNaOrdemCorreta() {
        ConsultaResponseDTO consulta = consultaService.cadastrar(new ConsultaRequestDTO(
                horarioSlot, null, paciente.getId(), profissional.getId(), unidade.getId()
        ));

        consultaService.atualizarStatus(consulta.id(), statusDto(StatusConsulta.CONFIRMADA));
        consultaService.atualizarStatus(consulta.id(), statusDto(StatusConsulta.EM_ATENDIMENTO));
        ConsultaResponseDTO finalizada =
                consultaService.atualizarStatus(consulta.id(), statusDto(StatusConsulta.REALIZADA));

        assertThat(finalizada.status()).isEqualTo(StatusConsulta.REALIZADA);
    }

    @Test
    void naoPermiteReabrirConsultaJaRealizada() {
        ConsultaResponseDTO consulta = consultaService.cadastrar(new ConsultaRequestDTO(
                horarioSlot, null, paciente.getId(), profissional.getId(), unidade.getId()
        ));

        consultaService.atualizarStatus(consulta.id(), statusDto(StatusConsulta.CONFIRMADA));
        consultaService.atualizarStatus(consulta.id(), statusDto(StatusConsulta.EM_ATENDIMENTO));
        consultaService.atualizarStatus(consulta.id(), statusDto(StatusConsulta.REALIZADA));

        assertThatThrownBy(() ->
                consultaService.atualizarStatus(consulta.id(), statusDto(StatusConsulta.CONFIRMADA))
        ).isInstanceOf(BusinessException.class);
    }

    private AtualizarStatusConsultaDTO statusDto(StatusConsulta status) {
        AtualizarStatusConsultaDTO dto = new AtualizarStatusConsultaDTO();
        dto.setStatus(status);
        return dto;
    }
}
