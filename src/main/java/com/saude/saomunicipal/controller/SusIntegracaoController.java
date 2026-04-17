package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.integration.sus.dto.SusIntegrationResponseDTO;
import com.saude.saomunicipal.integration.sus.service.SusIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/integracoes/sus")
@RequiredArgsConstructor
public class SusIntegracaoController {

    private final SusIntegrationService service;

    @PostMapping("/consultas/{consultaId}/esus-aps")
    public ResponseEntity<SusIntegrationResponseDTO> enviarConsultaEsusAps(
            @PathVariable Long consultaId
    ) {
        return ResponseEntity.ok(service.enviarConsultaParaEsusAps(consultaId));
    }
}