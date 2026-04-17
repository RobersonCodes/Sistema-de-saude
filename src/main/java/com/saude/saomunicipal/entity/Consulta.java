package com.saude.saomunicipal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "consultas",
        indexes = {

                @Index(
                        name = "idx_consulta_data_hora",
                        columnList = "data_hora"
                ),

                @Index(
                        name = "idx_consulta_status",
                        columnList = "status"
                ),

                @Index(
                        name = "idx_consulta_paciente",
                        columnList = "paciente_id"
                ),

                @Index(
                        name = "idx_consulta_profissional",
                        columnList = "profissional_id"
                ),

                @Index(
                        name = "idx_consulta_unidade",
                        columnList = "unidade_id"
                )

        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "data_hora",
            nullable = false
    )
    private LocalDateTime dataHora;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private StatusConsulta status;


    @Column(
            name = "observacoes",
            length = 500
    )
    private String observacoes;


    @Builder.Default
    @Column(
            name = "ativo",
            nullable = false
    )
    private Boolean ativo = true;



    /*
     RELACIONAMENTOS
     */


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "paciente_id",
            nullable = false
    )
    private Paciente paciente;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "profissional_id",
            nullable = false
    )
    private ProfissionalSaude profissional;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "unidade_id",
            nullable = false
    )
    private UnidadeSaude unidade;

}