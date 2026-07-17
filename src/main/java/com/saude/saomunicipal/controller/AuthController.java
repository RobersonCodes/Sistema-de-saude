package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.AdminCriarUsuarioRequestDTO;
import com.saude.saomunicipal.dto.AlterarRoleRequestDTO;
import com.saude.saomunicipal.dto.AuthRequestDTO;
import com.saude.saomunicipal.dto.AuthResponseDTO;
import com.saude.saomunicipal.dto.MeResponseDTO;
import com.saude.saomunicipal.dto.RegisterRequestDTO;
import com.saude.saomunicipal.entity.Usuario;
import com.saude.saomunicipal.exception.BusinessException;
import com.saude.saomunicipal.repository.UsuarioRepository;
import com.saude.saomunicipal.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação JWT")
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário (auto-cadastro; perfil nunca é escolhido pelo cliente)")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar usuário com perfil explícito (somente ADMIN)")
    public ResponseEntity<MeResponseDTO> criarUsuario(@RequestBody @Valid AdminCriarUsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.criarUsuario(dto));
    }

    @PatchMapping("/usuarios/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alterar o perfil de um usuário existente (somente ADMIN)")
    public ResponseEntity<MeResponseDTO> alterarRole(
            @PathVariable Long id,
            @RequestBody @Valid AlterarRoleRequestDTO dto
    ) {
        return ResponseEntity.ok(authService.alterarRole(id, dto.role()));
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @GetMapping("/me")
    @Operation(summary = "Retornar usuário autenticado")
    public ResponseEntity<MeResponseDTO> me(Authentication authentication) {

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        MeResponseDTO response = new MeResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name()
        );

        return ResponseEntity.ok(response);
    }
}