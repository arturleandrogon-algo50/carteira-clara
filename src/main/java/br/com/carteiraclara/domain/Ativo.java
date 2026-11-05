package br.com.carteiraclara.domain;

import java.util.Objects;

/**
 * Algo que a pessoa pode ter na carteira, como Bitcoin ou Ethereum.
 * Imutável e em Java puro. O símbolo é sempre guardado em maiúsculas.
 */
public record Ativo(String simbolo, String nome, TipoAtivo tipo) {

    public static final Ativo BITCOIN = cripto("BTC", "Bitcoin");
    public static final Ativo ETHEREUM = cripto("ETH", "Ethereum");

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
}