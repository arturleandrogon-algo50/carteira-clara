package br.com.carteiraclara.application;

import java.math.BigDecimal;
import java.util.Objects;

import br.com.carteiraclara.domain.Dinheiro;

/**
 * Os números que dependem do preço atual: quanto vale hoje,
 * o lucro (ou prejuízo) e a variação em %.
 * Só existe quando temos o preço — por isso vem sempre dentro de um Optional.
 */
public record ValorDeMercado(Dinheiro valorAtual, Dinheiro lucroOuPrejuizo, BigDecimal variacaoPercentual) {

    public ValorDeMercado {
        Objects.requireNonNull(valorAtual, "O valor atual não pode ser nulo");
        Objects.requireNonNull(lucroOuPrejuizo, "O lucro ou prejuízo não pode ser nulo");
        Objects.requireNonNull(variacaoPercentual, "A variação não pode ser nula");
    }
}