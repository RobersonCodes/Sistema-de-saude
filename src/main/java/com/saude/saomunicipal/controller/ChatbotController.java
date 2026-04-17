package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.MensagemChatbotResponseDTO;
import com.saude.saomunicipal.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
@Tag(name = "Chatbot", description = "Respostas formatadas para WhatsApp, app e chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @GetMapping("/boas-vindas")
    @Operation(summary = "Mensagem inicial do chatbot")
    public ResponseEntity<MensagemChatbotResponseDTO> mensagemBoasVindas() {
        return ResponseEntity.ok(chatbotService.mensagemBoasVindas());
    }

    @GetMapping("/horarios/profissional")
    @Operation(summary = "Horários disponíveis formatados por profissional")
    public ResponseEntity<MensagemChatbotResponseDTO> listarHorariosPorProfissional(
            @RequestParam Long profissionalId,
            @RequestParam LocalDate data
    ) {
        return ResponseEntity.ok(
                chatbotService.listarHorariosPorProfissional(profissionalId, data)
        );
    }

    @GetMapping("/horarios/unidade")
    @Operation(summary = "Horários disponíveis formatados por unidade")
    public ResponseEntity<MensagemChatbotResponseDTO> listarHorariosPorUnidade(
            @RequestParam Long unidadeId,
            @RequestParam LocalDate data
    ) {
        return ResponseEntity.ok(
                chatbotService.listarHorariosPorUnidade(unidadeId, data)
        );
    }

    @GetMapping("/consultas/{cpf}")
    @Operation(summary = "Consultar consultas do cidadão por CPF em formato de mensagem")
    public ResponseEntity<MensagemChatbotResponseDTO> consultarConsultasPorCpf(
            @PathVariable
            @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
            String cpf
    ) {
        return ResponseEntity.ok(chatbotService.consultarConsultasPorCpf(cpf));
    }
}