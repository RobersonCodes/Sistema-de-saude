package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.AgendaRequestDTO;
import com.saude.saomunicipal.dto.AgendaResponseDTO;
import com.saude.saomunicipal.dto.HorarioDisponivelDTO;
import com.saude.saomunicipal.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/agenda")
@RequiredArgsConstructor
@Tag(name = "Agenda", description = "Gerenciamento de agenda dos profissionais")
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    @Operation(summary = "Cadastrar horário na agenda do profissional")
    public ResponseEntity<AgendaResponseDTO> cadastrar(@RequestBody @Valid AgendaRequestDTO dto) {
        AgendaResponseDTO response = agendaService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profissional")
    @Operation(summary = "Listar agenda do profissional por data")
    public ResponseEntity<List<AgendaResponseDTO>> listarPorProfissionalEData(
            @RequestParam Long profissionalId,
            @RequestParam LocalDate data
    ) {
        return ResponseEntity.ok(
                agendaService.listarPorProfissionalEData(profissionalId, data)
        );
    }

    @GetMapping("/profissional/disponiveis")
    @Operation(summary = "Listar horários disponíveis do profissional por data")
    public ResponseEntity<List<AgendaResponseDTO>> listarDisponiveisPorProfissionalEData(
            @RequestParam Long profissionalId,
            @RequestParam LocalDate data
    ) {
        return ResponseEntity.ok(
                agendaService.listarDisponiveisPorProfissionalEData(profissionalId, data)
        );
    }

    @GetMapping("/unidade/disponiveis")
    @Operation(summary = "Listar horários disponíveis da unidade por data")
    public ResponseEntity<List<AgendaResponseDTO>> listarDisponiveisPorUnidadeEData(
            @RequestParam Long unidadeId,
            @RequestParam LocalDate data
    ) {
        return ResponseEntity.ok(
                agendaService.listarDisponiveisPorUnidadeEData(unidadeId, data)
        );
    }

    @GetMapping("/horarios/profissional")
    @Operation(summary = "Listar horários disponíveis por profissional para app, site ou WhatsApp")
    public ResponseEntity<List<HorarioDisponivelDTO>> listarHorariosPorProfissional(
            @RequestParam Long profissionalId,
            @RequestParam LocalDate data
    ) {
        return ResponseEntity.ok(
                agendaService.listarHorariosDisponiveisPorProfissional(profissionalId, data)
        );
    }

    @GetMapping("/horarios/unidade")
    @Operation(summary = "Listar horários disponíveis por unidade para app, site ou WhatsApp")
    public ResponseEntity<List<HorarioDisponivelDTO>> listarHorariosPorUnidade(
            @RequestParam Long unidadeId,
            @RequestParam LocalDate data
    ) {
        return ResponseEntity.ok(
                agendaService.listarHorariosDisponiveisPorUnidade(unidadeId, data)
        );
    }
}