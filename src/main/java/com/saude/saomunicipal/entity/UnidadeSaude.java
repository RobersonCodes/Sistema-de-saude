package com.saude.saomunicipal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "unidades_saude",
        indexes = {

                @Index(
                        name = "idx_unidade_nome",
                        columnList = "nome"
                ),

                @Index(
                        name = "idx_unidade_tipo",
                        columnList = "tipo"
                ),

                @Index(
                        name = "idx_unidade_bairro",
                        columnList = "bairro"
                ),

                @Index(
                        name = "idx_unidade_ativa",
                        columnList = "ativa"
                )

        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadeSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 150)
    private String nome;


    @Column(nullable = false, length = 50)
    private String tipo;


    @Column(nullable = false, length = 100)
    private String bairro;


    @Column(nullable = false, length = 255)
    private String endereco;


    @Column(length = 20)
    private String telefone;


    @Builder.Default
    @Column(nullable = false)
    private Boolean ativa = true;

}