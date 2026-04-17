package com.saude.saomunicipal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "pacientes",
        indexes = {
                @Index(name = "idx_paciente_cpf", columnList = "cpf"),
                @Index(name = "idx_paciente_nome", columnList = "nome_completo"),
                @Index(name = "idx_paciente_unidade", columnList = "unidade_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(unique = true, length = 20)
    private String cns;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(length = 20)
    private String sexo;

    @Column(length = 20)
    private String telefone;

    @Column(length = 120)
    private String email;

    @Column(length = 255)
    private String endereco;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private UnidadeSaude unidade;
}