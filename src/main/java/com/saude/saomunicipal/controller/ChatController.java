package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.ChatRequestDTO;
import com.saude.saomunicipal.dto.ChatResponseDTO;
import com.saude.saomunicipal.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponseDTO> conversar(
            @RequestBody @Valid ChatRequestDTO dto
    ) {
        return ResponseEntity.ok(chatService.conversar(dto.mensagem()));
    }
}