package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.PageResponseDTO;
import com.saude.saomunicipal.dto.PacienteFiltroDTO;
import com.saude.saomunicipal.dto.PacienteRequestDTO;
import com.saude.saomunicipal.dto.PacienteResponseDTO;
import com.saude.saomunicipal.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "Gerenciamento de pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @PostMapping
    @Operation(summary = "Cadastrar novo paciente")
    public ResponseEntity<PacienteResponseDTO> cadastrar(
            @RequestBody @Valid PacienteRequestDTO dto
    ) {
        PacienteResponseDTO response = pacienteService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar pacientes com paginação, filtros e ordenação")
    public ResponseEntity<PageResponseDTO<PacienteResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) Long unidadeId,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        PacienteFiltroDTO filtro = new PacienteFiltroDTO(
                nome,
                cpf,
                unidadeId,
                ativo
        );

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(pacienteService.listar(filtro, pageable));
    }
}