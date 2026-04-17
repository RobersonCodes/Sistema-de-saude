package com.saude.saomunicipal.integration.sus.entity;

import com.saude.saomunicipal.integration.sus.enums.StatusIntegracao;
import com.saude.saomunicipal.integration.sus.enums.TipoIntegracaoSus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "integracao_log", indexes = {
        @Index(name = "idx_integracao_log_tipo", columnList = "tipo_integracao"),
        @Index(name = "idx_integracao_log_status", columnList = "status_integracao"),
        @Index(name = "idx_integracao_log_entidade", columnList = "entidade_referencia, referencia_id"),
        @Index(name = "idx_integracao_log_data", columnList = "data_evento")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegracaoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_integracao", nullable = false, length = 30)
    private TipoIntegracaoSus tipoIntegracao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_integracao", nullable = false, length = 20)
    private StatusIntegracao statusIntegracao;

    @Column(name = "entidade_referencia", nullable = false, length = 50)
    private String entidadeReferencia;

    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    @Column(name = "endpoint_destino", length = 255)
    private String endpointDestino;

    @Lob
    @Column(name = "payload_enviado", columnDefinition = "LONGTEXT")
    private String payloadEnviado;

    @Lob
    @Column(name = "payload_resposta", columnDefinition = "LONGTEXT")
    private String payloadResposta;

    @Column(name = "mensagem", length = 500)
    private String mensagem;

    @Builder.Default
    @Column(name = "tentativa", nullable = false)
    private Integer tentativa = 1;

    @Builder.Default
    @Column(name = "sucesso", nullable = false)
    private Boolean sucesso = false;

    @Builder.Default
    @Column(name = "data_evento", nullable = false)
    private LocalDateTime dataEvento = LocalDateTime.now();
}