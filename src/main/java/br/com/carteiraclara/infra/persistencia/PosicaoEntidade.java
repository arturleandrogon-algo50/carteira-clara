package br.com.carteiraclara.infra.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Linha da tabela "posicao" (criada pela migração V2).
 * Existe só na camada infra: o domínio usa o record Posicao, sem JPA.
 */
@Entity
@Table(name = "posicao")
public class PosicaoEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ativo_id", nullable = false)
    private AtivoEntidade ativo;

    @Column(nullable = false, precision = 28, scale = 8)
    private BigDecimal quantidade;

    @Column(name = "preco_medio", nullable = false, precision = 28, scale = 8)
    private BigDecimal precoMedio;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    /** Exigido pelo JPA. */
    protected PosicaoEntidade() {
    }

    public PosicaoEntidade(AtivoEntidade ativo, BigDecimal quantidade, BigDecimal precoMedio) {
        this.ativo = ativo;
        this.quantidade = quantidade;
        this.precoMedio = precoMedio;
        var agora = Instant.now();
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    /** Troca quantidade e preço médio e registra a hora da mudança. */
    public void atualizar(BigDecimal novaQuantidade, BigDecimal novoPrecoMedio) {
        this.quantidade = novaQuantidade;
        this.precoMedio = novoPrecoMedio;
        this.atualizadoEm = Instant.now();
    }

    public AtivoEntidade getAtivo() {
        return ativo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoMedio() {
        return precoMedio;
    }
}