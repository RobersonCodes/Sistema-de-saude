package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import com.saude.saomunicipal.entity.UsuarioRole;

@Schema(description = "Novo perfil a ser atribuído a um usuário")
public record AlterarRoleRequestDTO(

        @Schema(example = "GESTOR")
        @NotNull(message = "Perfil é obrigatório")
        UsuarioRole role

) {
}
