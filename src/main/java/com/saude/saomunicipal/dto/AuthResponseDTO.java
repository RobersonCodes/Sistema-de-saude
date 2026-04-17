package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação com token JWT")
public record AuthResponseDTO(

        @Schema(description = "Token JWT")
        String token,

        @Schema(description = "Tipo do token", example = "Bearer")
        String tipo,

        @Schema(description = "Email do usuário autenticado", example = "admin@saude.com")
        String email,

        @Schema(description = "Perfil do usuário", example = "ADMIN")
        String role

) {
}