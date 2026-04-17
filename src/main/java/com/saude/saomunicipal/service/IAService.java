package com.saude.saomunicipal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saude.saomunicipal.dto.CampoExtraidoDTO;
import com.saude.saomunicipal.dto.IntencaoIAResponseDTO;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IAService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public IntencaoIAResponseDTO interpretarMensagem(String mensagem) {
        System.out.println("API KEY LIDA: " + apiKey);
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();

        String prompt = """
                Você é uma IA de atendimento de um sistema de saúde municipal.

                Sua tarefa é identificar a intenção do usuário e extrair dados úteis.
                Responda SOMENTE em JSON válido.

                Intenções possíveis:
                - AGENDAR_CONSULTA
                - REMARCAR_CONSULTA
                - CANCELAR_CONSULTA
                - CONSULTAR_CONSULTAS
                - CONSULTAR_HORARIOS
                - CONSULTAR_UNIDADE
                - DUVIDA_GERAL

                Campos que podem ser extraídos:
                - cpf
                - cns
                - cartao_sus
                - especialidade
                - data
                - turno
                - dataHora
                - profissionalId
                - unidadeId
                - consultaId

                Se o usuário disser "cartão sus", "cartao sus" ou "cns", normalize como cns ou cartao_sus.
                Se houver data e hora exatas no texto, prefira preencher dataHora em formato yyyy-MM-ddTHH:mm:ss.

                Formato obrigatório:
                {
                  "intent": "NOME_DA_INTENCAO",
                  "mensagemNormalizada": "frase curta explicando o entendimento",
                  "campos": [
                    { "campo": "cpf", "valor": "12345678901" },
                    { "campo": "cns", "valor": "123456789012345" },
                    { "campo": "especialidade", "valor": "Cardiologia" },
                    { "campo": "profissionalId", "valor": "1" },
                    { "campo": "unidadeId", "valor": "2" },
                    { "campo": "dataHora", "valor": "2026-04-10T09:00:00" },
                    { "campo": "consultaId", "valor": "5" }
                  ]
                }

                Mensagem do usuário:
                """ + mensagem;

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(model)
                .input(prompt)
                .build();

        Response response = client.responses().create(params);

        String texto = extrairTexto(response);

        try {
            JsonNode root = objectMapper.readTree(texto);

            String intent = root.path("intent").asText("DUVIDA_GERAL");
            String mensagemNormalizada = root.path("mensagemNormalizada")
                    .asText("Não foi possível interpretar a mensagem.");

            List<CampoExtraidoDTO> campos = new ArrayList<>();
            JsonNode camposNode = root.path("campos");

            if (camposNode.isArray()) {
                for (JsonNode node : camposNode) {
                    campos.add(new CampoExtraidoDTO(
                            node.path("campo").asText(),
                            node.path("valor").asText()
                    ));
                }
            }

            return new IntencaoIAResponseDTO(intent, mensagemNormalizada, campos);

        } catch (Exception e) {
            e.printStackTrace();

            return new IntencaoIAResponseDTO(
                    "DUVIDA_GERAL",
                    "Não foi possível interpretar a mensagem de forma estruturada.",
                    List.of()
            );
        }
    }

    private String extrairTexto(Response response) {
        StringBuilder sb = new StringBuilder();

        response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .forEach(outputText -> sb.append(outputText.text()));

        return sb.toString().trim();
    }
}