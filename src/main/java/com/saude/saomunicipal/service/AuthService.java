package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.AdminCriarUsuarioRequestDTO;
import com.saude.saomunicipal.dto.AuthRequestDTO;
import com.saude.saomunicipal.dto.AuthResponseDTO;
import com.saude.saomunicipal.dto.MeResponseDTO;
import com.saude.saomunicipal.dto.RegisterRequestDTO;
import com.saude.saomunicipal.entity.Usuario;
import com.saude.saomunicipal.entity.UsuarioRole;
import com.saude.saomunicipal.exception.BusinessException;
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

    /**
     * Auto-cadastro público. O cliente nunca escolhe o próprio perfil: o
     * primeiro usuário do sistema vira ADMIN (bootstrap) e todos os demais
     * entram com o perfil de menor privilégio (ATENDENTE). Elevar um perfil
     * exige um ADMIN autenticado via {@link #criarUsuario} ou {@link #alterarRole}.
     */
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        UsuarioRole role = repository.count() == 0 ? UsuarioRole.ADMIN : UsuarioRole.ATENDENTE;
        Usuario usuario = criarUsuarioInterno(dto.nome(), dto.email(), dto.senha(), role);
        return gerarAuthResponse(usuario);
    }

    /** Criação de usuário por um ADMIN, com perfil explícito. */
    public MeResponseDTO criarUsuario(AdminCriarUsuarioRequestDTO dto) {
        Usuario usuario = criarUsuarioInterno(dto.nome(), dto.email(), dto.senha(), dto.role());
        return toMeResponse(usuario);
    }

    public MeResponseDTO alterarRole(Long usuarioId, UsuarioRole novaRole) {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        usuario.setRole(novaRole);
        repository.save(usuario);

        return toMeResponse(usuario);
    }

    public AuthResponseDTO login(AuthRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.senha()
                )
        );

        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        return gerarAuthResponse(usuario);
    }

    private Usuario criarUsuarioInterno(String nome, String email, String senha, UsuarioRole role) {
        if (repository.existsByEmail(email)) {
            throw new BusinessException("Já existe usuário com esse e-mail.");
        }

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(encoder.encode(senha))
                .role(role)
                .ativo(true)
                .build();

        return repository.save(usuario);
    }

    private AuthResponseDTO gerarAuthResponse(Usuario usuario) {
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
                usuario.getRole().name()
        );
    }

    private MeResponseDTO toMeResponse(Usuario usuario) {
        return new MeResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name()
        );
    }
}
