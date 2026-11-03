package br.com.carteiraclara.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Quanto a pessoa tem de um ativo e por quanto pagou (preço médio, em reais).
 *
 * <p>Quantidade e preço unitário ficam em {@link BigDecimal} com até 8 casas
 * decimais (criptomoedas são fracionadas, como 0,00012345 BTC). Só os valores
 * totais viram {@link Dinheiro}, com 2 casas. Java puro, sem Spring.
 */
public record Posicao(String simbolo, BigDecimal quantidade, BigDecimal precoMedio) {

    private static final int CASAS_CRIPTO = 8;
    private static final BigDecimal CEM = new BigDecimal("100");

    public Posicao {
        Objects.requireNonNull(simbolo, "O símbolo não pode ser nulo");
        Objects.requireNonNull(quantidade, "A quantidade não pode ser nula");
        Objects.requireNonNull(precoMedio, "O preço médio não pode ser nulo");
        if (simbolo.isBlank()) {
            throw new IllegalArgumentException("O símbolo não pode ser vazio");
        }
        if (quantidade.signum() < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa");
        }
        if (precoMedio.signum() < 0) {
            throw new IllegalArgumentException("O preço médio não pode ser negativo");
        }
        simbolo = simbolo.trim().toUpperCase();
        quantidade = quantidade.setScale(CASAS_CRIPTO, RoundingMode.HALF_EVEN);
        precoMedio = precoMedio.setScale(CASAS_CRIPTO, RoundingMode.HALF_EVEN);
    }

    public static Posicao de(String simbolo, String quantidade, String precoMedio) {
        return new Posicao(simbolo, new BigDecimal(quantidade), new BigDecimal(precoMedio));
    }

    /** Quanto foi gasto no total: quantidade x preço médio. */
    public Dinheiro custoTotal() {
        return new Dinheiro(quantidade.multiply(precoMedio));
    }

    /** Quanto vale hoje, dado o preço atual de 1 unidade. */
    public Dinheiro valorAtual(BigDecimal precoAtual) {
        validarPreco(precoAtual);
        return new Dinheiro(quantidade.multiply(precoAtual));
    }

    /** Lucro (positivo) ou prejuízo (negativo) em reais. */
    public Dinheiro lucroOuPrejuizo(BigDecimal precoAtual) {
        return valorAtual(precoAtual).subtrair(custoTotal());
    }

    /** Variação em %, com 2 casas. Se o custo for zero, devolve 0,00. */
    public BigDecimal variacaoPercentual(BigDecimal precoAtual) {
        var custo = custoTotal().valor();
        if (custo.signum() == 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return lucroOuPrejuizo(precoAtual).valor()
                .multiply(CEM)
                .divide(custo, 2, RoundingMode.HALF_EVEN);
    }

    private static void validarPreco(BigDecimal precoAtual) {
        Objects.requireNonNull(precoAtual, "O preço atual não pode ser nulo");
        if (precoAtual.signum() < 0) {
            throw new IllegalArgumentException("O preço atual não pode ser negativo");
        }
    }
}