package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.AgendamentoCidadaoRequestDTO;
import com.saude.saomunicipal.dto.CampoExtraidoDTO;
import com.saude.saomunicipal.dto.ChatResponseDTO;
import com.saude.saomunicipal.dto.ConsultaPorCpfResponseDTO;
import com.saude.saomunicipal.dto.ConsultaResponseDTO;
import com.saude.saomunicipal.dto.HorarioDisponivelDTO;
import com.saude.saomunicipal.dto.IntencaoIAResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final IAService iaService;
    private final ConsultaService consultaService;
    private final AgendaService agendaService;

    public ChatResponseDTO conversar(String mensagem) {
        IntencaoIAResponseDTO interpretacao = iaService.interpretarMensagem(mensagem);

        String intent = interpretacao.intent();

        return switch (intent) {
            case "CONSULTAR_CONSULTAS" -> responderConsultas(interpretacao);
            case "CONSULTAR_HORARIOS" -> responderHorarios(interpretacao);
            case "AGENDAR_CONSULTA" -> responderAgendamento(interpretacao);
            default -> new ChatResponseDTO(
                    intent,
                    "Entendi sua mensagem, mas ainda não tenho uma ação automática para esse caso."
            );
        };
    }

    private ChatResponseDTO responderConsultas(IntencaoIAResponseDTO interpretacao) {
        String cpf = buscarCampo(interpretacao.campos(), "cpf");
        String cns = buscarCampo(interpretacao.campos(), "cns");
        String cartaoSus = buscarCampo(interpretacao.campos(), "cartao_sus");

        String documento = primeiroNaoVazio(cpf, cns, cartaoSus);

        if (documento == null || documento.isBlank()) {
            return new ChatResponseDTO(
                    interpretacao.intent(),
                    "Para consultar suas consultas, informe seu CPF ou Cartão SUS (CNS)."
            );
        }

        List<ConsultaPorCpfResponseDTO> consultas = consultaService.listarPorDocumento(documento);

        if (consultas.isEmpty()) {
            return new ChatResponseDTO(
                    interpretacao.intent(),
                    "Não encontrei consultas para o documento informado."
            );
        }

        StringBuilder resposta = new StringBuilder("Encontrei estas consultas:\n");

        for (int i = 0; i < consultas.size(); i++) {
            ConsultaPorCpfResponseDTO consulta = consultas.get(i);

            resposta.append(i + 1)
                    .append(". Data/Hora: ")
                    .append(consulta.dataHora())
                    .append(" | Status: ")
                    .append(consulta.status())
                    .append(" | Profissional ID: ")
                    .append(consulta.profissionalId())
                    .append(" | Unidade ID: ")
                    .append(consulta.unidadeId())
                    .append("\n");
        }

        return new ChatResponseDTO(
                interpretacao.intent(),
                resposta.toString().trim()
        );
    }

    private ChatResponseDTO responderHorarios(IntencaoIAResponseDTO interpretacao) {
        String profissionalIdTexto = buscarCampo(interpretacao.campos(), "profissionalId");
        String dataTexto = buscarCampo(interpretacao.campos(), "data");

        if (profissionalIdTexto == null || profissionalIdTexto.isBlank()) {
            return new ChatResponseDTO(
                    interpretacao.intent(),
                    "Para consultar horários, informe o profissionalId."
            );
        }

        if (dataTexto == null || dataTexto.isBlank()) {
            return new ChatResponseDTO(
                    interpretacao.intent(),
                    "Para consultar horários, informe também a data no formato yyyy-MM-dd."
            );
        }

        Long profissionalId;
        LocalDate data;

        try {
            profissionalId = Long.parseLong(profissionalIdTexto);
            data = LocalDate.parse(dataTexto);
        } catch (Exception e) {
            return new ChatResponseDTO(
                    interpretacao.intent(),
                    "Não consegui entender profissionalId ou data. Use profissionalId numérico e data no formato yyyy-MM-dd."
            );
        }

        List<HorarioDisponivelDTO> horarios =
                agendaService.listarHorariosDisponiveisPorProfissional(profissionalId, data);

        if (horarios.isEmpty()) {
            return new ChatResponseDTO(
                    interpretacao.intent(),
                    "Não encontrei horários disponíveis para esse profissional nessa data."
            );
        }

        StringBuilder resposta = new StringBuilder("Horários disponíveis:\n");

        for (int i = 0; i < horarios.size(); i++) {
            HorarioDisponivelDTO horario = horarios.get(i);

            resposta.append(i + 1)
                    .append(". ")
                    .append(horario.data())
                    .append(" às ")
                    .append(horario.hora())
                    .append("\n");
        }

        return new ChatResponseDTO(
                interpretacao.intent(),
                resposta.toString().trim()
        );
    }

    private ChatResponseDTO responderAgendamento(IntencaoIAResponseDTO interpretacao) {
        String cpf = buscarCampo(interpretacao.campos(), "cpf");
        String cns = buscarCampo(interpretacao.campos(), "cns");
        String cartaoSus = buscarCampo(interpretacao.campos(), "cartao_sus");
        String documento = primeiroNaoVazio(cpf, cns, cartaoSus);

        String profissionalIdTexto = buscarCampo(interpretacao.campos(), "profissionalId");
        String unidadeIdTexto = buscarCampo(interpretacao.campos(), "unidadeId");
        String dataHoraTexto = buscarCampo(interpretacao.campos(), "dataHora");

        String especialidade = buscarCampo(interpretacao.campos(), "especialidade");
        String data = buscarCampo(interpretacao.campos(), "data");
        String turno = buscarCampo(interpretacao.campos(), "turno");

        if (documento != null && profissionalIdTexto != null && unidadeIdTexto != null && dataHoraTexto != null) {
            try {
                Long profissionalId = Long.parseLong(profissionalIdTexto);
                Long unidadeId = Long.parseLong(unidadeIdTexto);
                LocalDateTime dataHora = LocalDateTime.parse(dataHoraTexto);

                AgendamentoCidadaoRequestDTO dto = new AgendamentoCidadaoRequestDTO(
                        documento,
                        profissionalId,
                        unidadeId,
                        dataHora,
                        "Agendamento realizado pelo chat com IA"
                );

                ConsultaResponseDTO consulta = consultaService.agendarPorDocumento(dto);

                return new ChatResponseDTO(
                        interpretacao.intent(),
                        "Consulta agendada com sucesso para " + consulta.dataHora()
                                + ". Profissional ID: " + consulta.profissionalId()
                                + " | Unidade ID: " + consulta.unidadeId()
                        );
            } catch (Exception e) {
                return new ChatResponseDTO(
                        interpretacao.intent(),
                        "Entendi a intenção de agendamento, mas não consegui concluir automaticamente. Verifique documento, profissionalId, unidadeId e dataHora no formato yyyy-MM-ddTHH:mm:ss."
                );
            }
        }

        StringBuilder resposta = new StringBuilder("Entendi que você quer agendar uma consulta");

        if (especialidade != null && !especialidade.isBlank()) {
            resposta.append(" com especialidade ").append(especialidade);
        }

        if (data != null && !data.isBlank()) {
            resposta.append(" para ").append(data);
        }

        if (turno != null && !turno.isBlank()) {
            resposta.append(" no turno ").append(turno);
        }

        resposta.append(".\n");
        resposta.append("Para eu agendar automaticamente, envie também:\n");
        resposta.append("- seu CPF ou Cartão SUS\n");
        resposta.append("- profissionalId\n");
        resposta.append("- unidadeId\n");
        resposta.append("- dataHora no formato yyyy-MM-ddTHH:mm:ss");

        return new ChatResponseDTO(
                interpretacao.intent(),
                resposta.toString()
        );
    }

    private String buscarCampo(List<CampoExtraidoDTO> campos, String nomeCampo) {
        return campos.stream()
                .filter(c -> nomeCampo.equalsIgnoreCase(c.campo()))
                .map(CampoExtraidoDTO::valor)
                .findFirst()
                .orElse(null);
    }

    private String primeiroNaoVazio(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
    }
}