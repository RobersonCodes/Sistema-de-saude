package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.saude.saomunicipal.entity.UsuarioRole;

@Schema(description = "Dados para criação de usuário por um administrador, com perfil explícito")
public record AdminCriarUsuarioRequestDTO(

        @Schema(example = "Dra. Ana Souza")
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Schema(example = "ana.souza@saude.com")
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(example = "123456")
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        @Schema(example = "MEDICO")
        @NotNull(message = "Perfil é obrigatório")
        UsuarioRole role

) {
}
