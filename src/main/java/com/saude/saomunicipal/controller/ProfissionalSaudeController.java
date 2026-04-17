package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.PageResponseDTO;
import com.saude.saomunicipal.dto.ProfissionalFiltroDTO;
import com.saude.saomunicipal.dto.ProfissionalSaudeRequestDTO;
import com.saude.saomunicipal.dto.ProfissionalSaudeResponseDTO;
import com.saude.saomunicipal.service.ProfissionalSaudeService;
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
@RequestMapping("/api/v1/profissionais")
@RequiredArgsConstructor
@Tag(name = "Profissionais", description = "Gerenciamento de profissionais de saúde")
public class ProfissionalSaudeController {

    private final ProfissionalSaudeService profissionalService;

    @PostMapping
    @Operation(summary = "Cadastrar novo profissional")
    public ResponseEntity<ProfissionalSaudeResponseDTO> cadastrar(
            @RequestBody @Valid ProfissionalSaudeRequestDTO dto
    ) {
        ProfissionalSaudeResponseDTO response = profissionalService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar profissionais com paginação, filtros e ordenação")
    public ResponseEntity<PageResponseDTO<ProfissionalSaudeResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cargo,
            @RequestParam(required = false) String especialidade,
            @RequestParam(required = false) Long unidadeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        ProfissionalFiltroDTO filtro = new ProfissionalFiltroDTO(
                nome,
                cargo,
                especialidade,
                unidadeId
        );

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(profissionalService.listar(filtro, pageable));
    }
}