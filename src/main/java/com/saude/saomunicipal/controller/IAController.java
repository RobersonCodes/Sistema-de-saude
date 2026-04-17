package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.IntencaoIAResponseDTO;
import com.saude.saomunicipal.dto.MensagemIARequestDTO;
import com.saude.saomunicipal.service.IAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ia")
@RequiredArgsConstructor
public class IAController {

    private final IAService service;

    @PostMapping("/interpretar")
    public ResponseEntity<IntencaoIAResponseDTO> interpretar(
            @RequestBody @Valid MensagemIARequestDTO dto
    ) {

        return ResponseEntity.ok(
                service.interpretarMensagem(dto.mensagem())
        );

    }

}