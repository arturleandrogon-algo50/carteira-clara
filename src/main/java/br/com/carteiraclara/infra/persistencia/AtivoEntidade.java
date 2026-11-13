package br.com.carteiraclara.infra.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Linha da tabela "ativo" (catálogo criado pela migração V1).
 * Existe só na camada infra: o domínio usa o record Ativo, sem JPA.
 */
@Entity
@Table(name = "ativo")
public class AtivoEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String simbolo;

    @Column(nullable = false, length = 80)
    private String nome;

    /** Exigido pelo JPA. */
    protected AtivoEntidade() {
    }

    public Long getId() {
        return id;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public String getNome() {
        return nome;
    }
}