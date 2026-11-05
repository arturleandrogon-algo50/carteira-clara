package br.com.carteiraclara.domain;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

class PosicaoTest {

    private final Posicao meioEth = Posicao.de(Ativo.ETHEREUM, "0.5", "10000");

    @Test
    void calculaCustoTotal() {
        assertThat(meioEth.custoTotal()).isEqualTo(Dinheiro.de("5000.00"));
    }

    @Test
    void calculaValorAtual() {
        assertThat(meioEth.valorAtual(new BigDecimal("12000"))).isEqualTo(Dinheiro.de("6000.00"));
    }

    @Test
    void calculaLucro() {
        var lucro = meioEth.lucroOuPrejuizo(new BigDecimal("12000"));

        assertThat(lucro).isEqualTo(Dinheiro.de("1000.00"));
        assertThat(lucro.eNegativo()).isFalse();
    }

    @Test
    void calculaPrejuizo() {
        var prejuizo = meioEth.lucroOuPrejuizo(new BigDecimal("8000"));

        assertThat(prejuizo).isEqualTo(Dinheiro.de("-1000.00"));
        assertThat(prejuizo.eNegativo()).isTrue();
    }

    @Test
    void calculaVariacaoPercentual() {
        assertThat(meioEth.variacaoPercentual(new BigDecimal("12000"))).hasToString("20.00");
        assertThat(meioEth.variacaoPercentual(new BigDecimal("8000"))).hasToString("-20.00");
        assertThat(meioEth.variacaoPercentual(new BigDecimal("10000"))).hasToString("0.00");
    }

    @Test
    void aceitaQuantidadeFracionadaDeCripto() {
        var poeira = Posicao.de(Ativo.BITCOIN, "0.00012345", "500000");

        assertThat(poeira.quantidade()).hasToString("0.00012345");
        assertThat(poeira.custoTotal()).isEqualTo(Dinheiro.de("61.72")); // 61,725 -> HALF_EVEN (2 é par)
    }

    @Test
    void quantidadeZeroNaoTemCustoNemVariacao() {
        var vazia = Posicao.de(Ativo.ETHEREUM, "0", "10000");

        assertThat(vazia.custoTotal()).isEqualTo(Dinheiro.zero());
        assertThat(vazia.lucroOuPrejuizo(new BigDecimal("12000"))).isEqualTo(Dinheiro.zero());
        assertThat(vazia.variacaoPercentual(new BigDecimal("12000"))).hasToString("0.00");
    }

    @Test
    void precoAtualZeroDaPrejuizoTotal() {
        assertThat(meioEth.variacaoPercentual(BigDecimal.ZERO)).hasToString("-100.00");
    }

    @Test
    void recusaQuantidadeNegativa() {
        assertThatThrownBy(() -> Posicao.de(Ativo.ETHEREUM, "-1", "10000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantidade");
    }

    @Test
    void recusaPrecoMedioNegativo() {
        assertThatThrownBy(() -> Posicao.de(Ativo.ETHEREUM, "1", "-5"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("preço médio");
    }

    @Test
    void recusaPrecoAtualNegativo() {
        assertThatThrownBy(() -> meioEth.valorAtual(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("preço atual");
    }

    @Test
    void recusaAtivoNulo() {
        assertThatThrownBy(() -> new Posicao(null, BigDecimal.ONE, BigDecimal.ONE))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ativo");
    }
}