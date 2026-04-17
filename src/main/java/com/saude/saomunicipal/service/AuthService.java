package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.AuthRequestDTO;
import com.saude.saomunicipal.dto.AuthResponseDTO;
import com.saude.saomunicipal.dto.RegisterRequestDTO;
import com.saude.saomunicipal.entity.Usuario;
import com.saude.saomunicipal.repository.UsuarioRepository;
import com.saude.saomunicipal.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponseDTO register(RegisterRequestDTO dto) {

        if (repository.existsByEmail(dto.email())) {
            throw new RuntimeException("Já existe usuário com esse e-mail.");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(encoder.encode(dto.senha()))
                .role(dto.role() == null || dto.role().isBlank() ? "ADMIN" : dto.role())
                .ativo(true)
                .build();

        repository.save(usuario);

        var userDetails = User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities("ROLE_" + usuario.getRole())
                .build();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(
                token,
                "Bearer",
                usuario.getEmail(),
                usuario.getRole()
        );
    }

    public AuthResponseDTO login(AuthRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.senha()
                )
        );

        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        var userDetails = User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities("ROLE_" + usuario.getRole())
                .build();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(
                token,
                "Bearer",
                usuario.getEmail(),
                usuario.getRole()
        );
    }
}