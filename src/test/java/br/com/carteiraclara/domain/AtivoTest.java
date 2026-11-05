package br.com.carteiraclara.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

class AtivoTest {

    @Test
    void ativosIniciaisSaoCripto() {
        assertThat(Ativo.BITCOIN.simbolo()).isEqualTo("BTC");
        assertThat(Ativo.ETHEREUM.simbolo()).isEqualTo("ETH");
        assertThat(Ativo.ETHEREUM.tipo()).isEqualTo(TipoAtivo.CRIPTO);
    }

    @Test
    void normalizaSimboloENome() {
        var ativo = Ativo.cripto("  sol ", "  Solana ");

        assertThat(ativo.simbolo()).isEqualTo("SOL");
        assertThat(ativo.nome()).isEqualTo("Solana");
    }

    @Test
    void doisAtivosComOsMesmosDadosSaoIguais() {
        assertThat(Ativo.cripto("eth", "Ethereum")).isEqualTo(Ativo.ETHEREUM);
    }

    @Test
    void recusaSimboloOuNomeVazio() {
        assertThatThrownBy(() -> Ativo.cripto(" ", "Algo"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("símbolo");
        assertThatThrownBy(() -> Ativo.cripto("XYZ", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nome");
    }

    @Test
    void recusaValoresNulos() {
        assertThatThrownBy(() -> new Ativo(null, "Algo", TipoAtivo.CRIPTO))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Ativo("XYZ", null, TipoAtivo.CRIPTO))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Ativo("XYZ", "Algo", null))
                .isInstanceOf(NullPointerException.class);
    }
}