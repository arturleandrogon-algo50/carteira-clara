package br.com.carteiraclara.domain;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

class DinheiroTest {

    @Test
    void somaValoresSimples() {
        var resultado = Dinheiro.de("10.00").somar(Dinheiro.de("5.50"));

        assertThat(resultado).isEqualTo(Dinheiro.de("15.50"));
    }

    @Test
    void somaSemErroDeCentavos() {
        // Com double, 0.1 + 0.2 daria 0.30000000000000004
        var resultado = Dinheiro.de("0.10").somar(Dinheiro.de("0.20"));

        assertThat(resultado).isEqualTo(Dinheiro.de("0.30"));
    }

    @Test
    void subtracaoPodeDarResultadoNegativo() {
        var resultado = Dinheiro.de("5.00").subtrair(Dinheiro.de("8.00"));

        assertThat(resultado).isEqualTo(Dinheiro.de("-3.00"));
        assertThat(resultado.eNegativo()).isTrue();
    }

    @Test
    void valorPositivoENulosNaoSaoNegativos() {
        assertThat(Dinheiro.de("1.00").eNegativo()).isFalse();
        assertThat(Dinheiro.zero().eNegativo()).isFalse();
    }

    @Test
    void sempreTemDuasCasasDecimais() {
        assertThat(Dinheiro.de("10").valor()).hasToString("10.00");
        assertThat(Dinheiro.de("10.5").valor()).hasToString("10.50");
    }

    @Test
    void arredondaParaDuasCasasComHalfEven() {
        assertThat(Dinheiro.de("10.005").valor()).hasToString("10.00");
        assertThat(Dinheiro.de("10.015").valor()).hasToString("10.02");
    }

    @Test
    void multiplicaPorQuantidadeEArredonda() {
        // 0,5 ETH a R$ 15.000,00
        var resultado = Dinheiro.de("15000.00").multiplicar(new BigDecimal("0.5"));

        assertThat(resultado).isEqualTo(Dinheiro.de("7500.00"));
    }

    @Test
    void naoAceitaValorNulo() {
        assertThatThrownBy(() -> new Dinheiro(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("não pode ser nulo");
    }

    @Test
    void operacoesNaoAlteramOObjetoOriginal() {
        var original = Dinheiro.de("10.00");

        original.somar(Dinheiro.de("5.00"));

        assertThat(original).isEqualTo(Dinheiro.de("10.00"));
    }

    @Test
    void comparaValores() {
        assertThat(Dinheiro.de("5.00")).isLessThan(Dinheiro.de("8.00"));
        assertThat(Dinheiro.de("8.00")).isGreaterThan(Dinheiro.de("5.00"));
    }
}