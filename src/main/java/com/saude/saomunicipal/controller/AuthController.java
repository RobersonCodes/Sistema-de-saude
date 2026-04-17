package com.saude.saomunicipal.controller;

import com.saude.saomunicipal.dto.AuthRequestDTO;
import com.saude.saomunicipal.dto.AuthResponseDTO;
import com.saude.saomunicipal.dto.MeResponseDTO;
import com.saude.saomunicipal.dto.RegisterRequestDTO;
import com.saude.saomunicipal.entity.Usuario;
import com.saude.saomunicipal.repository.UsuarioRepository;
import com.saude.saomunicipal.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Registrar novo usuário")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
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
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        MeResponseDTO response = new MeResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );

        return ResponseEntity.ok(response);
    }
}