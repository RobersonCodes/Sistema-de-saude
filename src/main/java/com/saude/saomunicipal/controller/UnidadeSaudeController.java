package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.PageResponseDTO;
import com.saude.saomunicipal.dto.UnidadeFiltroDTO;
import com.saude.saomunicipal.dto.UnidadeSaudeRequestDTO;
import com.saude.saomunicipal.dto.UnidadeSaudeResponseDTO;
import com.saude.saomunicipal.service.UnidadeSaudeService;
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
@RequestMapping("/api/v1/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "Gerenciamento de unidades de saúde")
public class UnidadeSaudeController {

    private final UnidadeSaudeService unidadeService;

    @PostMapping
    @Operation(summary = "Cadastrar nova unidade")
    public ResponseEntity<UnidadeSaudeResponseDTO> cadastrar(
            @RequestBody @Valid UnidadeSaudeRequestDTO dto
    ) {
        UnidadeSaudeResponseDTO response = unidadeService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar unidades com paginação, filtros e ordenação")
    public ResponseEntity<PageResponseDTO<UnidadeSaudeResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String bairro,
            @RequestParam(required = false) Boolean ativa,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        UnidadeFiltroDTO filtro = new UnidadeFiltroDTO(
                nome,
                tipo,
                bairro,
                ativa
        );

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(unidadeService.listar(filtro, pageable));
    }
}