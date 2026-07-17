package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.AuthResponseDTO;
import com.saude.saomunicipal.dto.RegisterRequestDTO;
import com.saude.saomunicipal.entity.UsuarioRole;
import com.saude.saomunicipal.exception.BusinessException;
import com.saude.saomunicipal.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void primeiroUsuarioCadastradoVirdaAdmin() {
        assertThat(usuarioRepository.count()).isZero();

        AuthResponseDTO response = authService.register(
                new RegisterRequestDTO("Primeiro Admin", "primeiro@saude.com", "123456")
        );

        assertThat(response.role()).isEqualTo(UsuarioRole.ADMIN.name());
    }

    @Test
    void usuariosSubsequentesEntramComoAtendente() {
        authService.register(new RegisterRequestDTO("Admin", "admin1@saude.com", "123456"));

        AuthResponseDTO segundo = authService.register(
                new RegisterRequestDTO("Segundo Usuário", "segundo@saude.com", "123456")
        );

        assertThat(segundo.role()).isEqualTo(UsuarioRole.ATENDENTE.name());
    }

    @Test
    void naoPermiteEmailDuplicado() {
        authService.register(new RegisterRequestDTO("Usuário", "duplicado@saude.com", "123456"));

        assertThatThrownBy(() ->
                authService.register(new RegisterRequestDTO("Outro", "duplicado@saude.com", "654321"))
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    void adminPodeCriarUsuarioComPerfilExplicito() {
        var criado = authService.criarUsuario(
                new com.saude.saomunicipal.dto.AdminCriarUsuarioRequestDTO(
                        "Dra. Ana", "ana@saude.com", "123456", UsuarioRole.MEDICO
                )
        );

        assertThat(criado.role()).isEqualTo(UsuarioRole.MEDICO.name());
    }

    @Test
    void adminPodeAlterarRoleDeUsuarioExistente() {
        AuthResponseDTO usuario = authService.register(
                new RegisterRequestDTO("Usuário", "promover@saude.com", "123456")
        );

        Long id = usuarioRepository.findByEmail(usuario.email()).orElseThrow().getId();

        var atualizado = authService.alterarRole(id, UsuarioRole.GESTOR);

        assertThat(atualizado.role()).isEqualTo(UsuarioRole.GESTOR.name());
    }
}
