package br.com.carteiraclara.domain;

import java.util.List;
import java.util.Objects;

/**
 * Algo que a pessoa pode ter na carteira, como Bitcoin ou Ethereum.
 * Imutável e em Java puro. O símbolo é sempre guardado em maiúsculas.
 */
public record Ativo(String simbolo, String nome, TipoAtivo tipo) {

    public static final Ativo BITCOIN = cripto("BTC", "Bitcoin");
    public static final Ativo ETHEREUM = cripto("ETH", "Ethereum");

    /** Ativos que o sistema acompanha hoje (os mesmos da migração V1 do banco). */
    private static final List<Ativo> CATALOGO = List.of(BITCOIN, ETHEREUM);

    public Ativo {
        Objects.requireNonNull(simbolo, "O símbolo não pode ser nulo");
        Objects.requireNonNull(nome, "O nome não pode ser nulo");
        Objects.requireNonNull(tipo, "O tipo não pode ser nulo");
        if (simbolo.isBlank()) {
            throw new IllegalArgumentException("O símbolo não pode ser vazio");
        }
        if (nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser vazio");
        }
        simbolo = simbolo.trim().toUpperCase();
        nome = nome.trim();
    }

    public static Ativo cripto(String simbolo, String nome) {
        return new Ativo(simbolo, nome, TipoAtivo.CRIPTO);
    }

    /** Busca um ativo do catálogo pelo símbolo. Aceita minúsculas e espaços. */
    public static Ativo doCatalogo(String simbolo) {
        Objects.requireNonNull(simbolo, "O símbolo não pode ser nulo");
        var procurado = simbolo.trim().toUpperCase();
        return CATALOGO.stream()
                .filter(ativo -> ativo.simbolo().equals(procurado))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ativo fora do catálogo: " + procurado));
    }
}