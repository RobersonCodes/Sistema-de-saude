package com.saude.saomunicipal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "profissionais_saude",
        indexes = {

                @Index(
                        name = "idx_profissional_nome",
                        columnList = "nome"
                ),

                @Index(
                        name = "idx_profissional_cargo",
                        columnList = "cargo"
                ),

                @Index(
                        name = "idx_profissional_especialidade",
                        columnList = "especialidade"
                ),

                @Index(
                        name = "idx_profissional_unidade",
                        columnList = "unidade_id"
                )

        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfissionalSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 150)
    private String nome;


    @Column(nullable = false, length = 80)
    private String cargo;


    @Column(length = 100)
    private String especialidade;


    @Column(length = 20)
    private String telefone;


    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;



    /*
     RELACIONAMENTO
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "unidade_id",
            nullable = false
    )
    private UnidadeSaude unidade;

}