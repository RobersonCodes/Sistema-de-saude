package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.PageResponseDTO;
import com.saude.saomunicipal.dto.UnidadeFiltroDTO;
import com.saude.saomunicipal.dto.UnidadeSaudeRequestDTO;
import com.saude.saomunicipal.dto.UnidadeSaudeResponseDTO;
import com.saude.saomunicipal.service.UnidadeSaudeService;
import com.saude.saomunicipal.util.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "Gerenciamento de unidades de saúde")
public class UnidadeSaudeController {

    private static final Set<String> CAMPOS_ORDENACAO_PERMITIDOS = Set.of(
            "id", "nome", "tipo", "bairro", "ativa"
    );

    private final UnidadeSaudeService unidadeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
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

        Pageable pageable = PageableUtils.build(page, size, sortBy, direction, CAMPOS_ORDENACAO_PERMITIDOS);

        return ResponseEntity.ok(unidadeService.listar(filtro, pageable));
    }
}