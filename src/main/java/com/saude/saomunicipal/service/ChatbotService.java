package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.ConsultaPorCpfResponseDTO;
import com.saude.saomunicipal.dto.HorarioDisponivelDTO;
import com.saude.saomunicipal.dto.MensagemChatbotResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final AgendaService agendaService;
    private final ConsultaService consultaService;

    public MensagemChatbotResponseDTO listarHorariosPorProfissional(Long profissionalId, LocalDate data) {
        List<HorarioDisponivelDTO> horarios =
                agendaService.listarHorariosDisponiveisPorProfissional(profissionalId, data);

        if (horarios.isEmpty()) {
            return new MensagemChatbotResponseDTO(
                    "Não há horários disponíveis para este profissional na data " + data + "."
            );
        }

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Horários disponíveis para ").append(data).append(":\n");

        for (int i = 0; i < horarios.size(); i++) {
            mensagem.append(i + 1)
                    .append(". ")
                    .append(horarios.get(i).hora())
                    .append("\n");
        }

        return new MensagemChatbotResponseDTO(mensagem.toString().trim());
    }

    public MensagemChatbotResponseDTO listarHorariosPorUnidade(Long unidadeId, LocalDate data) {
        List<HorarioDisponivelDTO> horarios =
                agendaService.listarHorariosDisponiveisPorUnidade(unidadeId, data);

        if (horarios.isEmpty()) {
            return new MensagemChatbotResponseDTO(
                    "Não há horários disponíveis para a unidade na data " + data + "."
            );
        }

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Horários disponíveis da unidade em ").append(data).append(":\n");

        for (int i = 0; i < horarios.size(); i++) {
            mensagem.append(i + 1)
                    .append(". ")
                    .append(horarios.get(i).hora())
                    .append(" (Profissional ID: ")
                    .append(horarios.get(i).profissionalId())
                    .append(")")
                    .append("\n");
        }

        return new MensagemChatbotResponseDTO(mensagem.toString().trim());
    }

    public MensagemChatbotResponseDTO consultarConsultasPorCpf(String cpf) {
        List<ConsultaPorCpfResponseDTO> consultas = consultaService.listarPorCpf(cpf);

        if (consultas.isEmpty()) {
            return new MensagemChatbotResponseDTO(
                    "Nenhuma consulta encontrada para o CPF informado."
            );
        }

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Consultas encontradas:\n");

        for (int i = 0; i < consultas.size(); i++) {
            ConsultaPorCpfResponseDTO consulta = consultas.get(i);

            mensagem.append(i + 1)
                    .append(". Data/Hora: ")
                    .append(consulta.dataHora())
                    .append(" | Status: ")
                    .append(consulta.status())
                    .append(" | Unidade ID: ")
                    .append(consulta.unidadeId())
                    .append("\n");
        }

        return new MensagemChatbotResponseDTO(mensagem.toString().trim());
    }

    public MensagemChatbotResponseDTO mensagemBoasVindas() {
        return new MensagemChatbotResponseDTO(
                """
                Olá! Bem-vindo ao atendimento digital da Saúde Municipal.
                                
                Você pode:
                1. Consultar horários disponíveis
                2. Consultar suas consultas por CPF
                3. Agendar consulta
                4. Remarcar consulta
                5. Cancelar consulta
                """
        );
    }
}