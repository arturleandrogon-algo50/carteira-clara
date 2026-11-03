package br.com.carteiraclara.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Valor em reais. Sempre com 2 casas decimais e imutável.
 *
 * <p>Usa {@link BigDecimal} porque {@code double} erra centavos
 * (0.1 + 0.2 dá 0.30000000000000004). Java puro: sem Spring nem JPA.
 *
 * <p>Pensada para valores totais em reais. Preços unitários e quantidades de
 * criptomoedas precisam de mais casas decimais e terão tipos próprios.
 */
public record Dinheiro(BigDecimal valor) implements Comparable<Dinheiro> {

    private static final int CASAS_DECIMAIS = 2;

    private static final Dinheiro ZERO = new Dinheiro(BigDecimal.ZERO);

    public Dinheiro {
        Objects.requireNonNull(valor, "O valor em dinheiro não pode ser nulo");
        valor = valor.setScale(CASAS_DECIMAIS, RoundingMode.HALF_EVEN);
    }

    public static Dinheiro zero() {
        return ZERO;
    }

    /** Cria a partir de texto com ponto decimal, por exemplo {@code de("10.50")}. */
    public static Dinheiro de(String texto) {
        Objects.requireNonNull(texto, "O texto do valor não pode ser nulo");
        return new Dinheiro(new BigDecimal(texto));
    }

    public Dinheiro somar(Dinheiro outro) {
        return new Dinheiro(valor.add(outro.valor));
    }

    public Dinheiro subtrair(Dinheiro outro) {
        return new Dinheiro(valor.subtract(outro.valor));
    }

    /** Multiplica por um fator, por exemplo uma quantidade. O resultado é arredondado para 2 casas. */
    public Dinheiro multiplicar(BigDecimal fator) {
        Objects.requireNonNull(fator, "O fator não pode ser nulo");
        return new Dinheiro(valor.multiply(fator));
    }

    public boolean eNegativo() {
        return valor.signum() < 0;
    }

    @Override
    public int compareTo(Dinheiro outro) {
        return valor.compareTo(outro.valor);
    }
}