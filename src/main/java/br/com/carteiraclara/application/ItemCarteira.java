package br.com.carteiraclara.application;

import java.util.Objects;
import java.util.Optional;

import br.com.carteiraclara.domain.Dinheiro;
import br.com.carteiraclara.domain.Posicao;

/**
 * Uma linha da carteira: a posição, quanto custou e,
 * se tivermos o preço atual, os valores de mercado.
 */
public record ItemCarteira(Posicao posicao, Dinheiro custo, Optional<ValorDeMercado> mercado) {

    public ItemCarteira {
        Objects.requireNonNull(posicao, "A posição não pode ser nula");
        Objects.requireNonNull(custo, "O custo não pode ser nulo");
        Objects.requireNonNull(mercado, "Use Optional.empty() quando não houver preço");
    }
}