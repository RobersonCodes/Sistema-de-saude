package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.AtualizarStatusConsultaDTO;
import com.saude.saomunicipal.dto.ConsultaPorCpfResponseDTO;
import com.saude.saomunicipal.dto.ConsultaRequestDTO;
import com.saude.saomunicipal.dto.ConsultaResponseDTO;
import com.saude.saomunicipal.dto.RemarcarConsultaDTO;
import com.saude.saomunicipal.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas", description = "Gerenciamento de consultas médicas")
public class ConsultaController {

    private final ConsultaService consultaService;

    @PostMapping
    @Operation(summary = "Cadastrar nova consulta")
    public ResponseEntity<ConsultaResponseDTO> cadastrar(
            @RequestBody @Valid ConsultaRequestDTO dto
    ) {
        ConsultaResponseDTO response = consultaService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar consultas com paginação")
    public ResponseEntity<Page<ConsultaResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(consultaService.listar(pageable));
    }

    @GetMapping("/cidadao/{cpf}")
    @Operation(summary = "Listar consultas do cidadão por CPF")
    public ResponseEntity<java.util.List<ConsultaPorCpfResponseDTO>> listarPorCpf(
            @PathVariable
            @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
            String cpf
    ) {
        return ResponseEntity.ok(consultaService.listarPorCpf(cpf));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da consulta")
    public ResponseEntity<ConsultaResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarStatusConsultaDTO dto
    ) {
        ConsultaResponseDTO response = consultaService.atualizarStatus(id, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancelamento")
    @Operation(summary = "Cancelar consulta")
    public ResponseEntity<ConsultaResponseDTO> cancelarConsulta(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(consultaService.cancelarConsulta(id));
    }

    @PatchMapping("/{id}/remarcacao")
    @Operation(summary = "Remarcar consulta")
    public ResponseEntity<ConsultaResponseDTO> remarcarConsulta(
            @PathVariable Long id,
            @RequestBody @Valid RemarcarConsultaDTO dto
    ) {
        ConsultaResponseDTO response = consultaService.remarcarConsulta(id, dto);
        return ResponseEntity.ok(response);
    }
}