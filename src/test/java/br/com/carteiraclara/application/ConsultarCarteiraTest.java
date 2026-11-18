package br.com.carteiraclara.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.com.carteiraclara.domain.Ativo;
import br.com.carteiraclara.domain.Dinheiro;
import br.com.carteiraclara.domain.Posicao;
import br.com.carteiraclara.domain.PosicaoRepositorio;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Testa a consulta da carteira com um dublê da porta (sem banco). */
@ExtendWith(MockitoExtension.class)
class ConsultarCarteiraTest {

    // custo 0,5 x 300.000 = 150.000,00
    private static final Posicao BTC = Posicao.de(Ativo.BITCOIN, "0.5", "300000");
    // custo 2 x 15.000 = 30.000,00
    private static final Posicao ETH = Posicao.de(Ativo.ETHEREUM, "2", "15000");

    @Mock
    private PosicaoRepositorio posicoes;

    private ConsultarCarteira consultar;

    @BeforeEach
    void preparar() {
        consultar = new ConsultarCarteira(posicoes);
    }

    @Test
    void calculaTudoQuandoTodosOsPrecosVieram() {
        when(posicoes.listarTodas()).thenReturn(List.of(BTC, ETH));

        var resumo = consultar.executar(Map.of(
                "BTC", new BigDecimal("350000"),
                "ETH", new BigDecimal("18000")));

        assertThat(resumo.itens()).hasSize(2);
        assertThat(resumo.itens().get(0).mercado()).contains(mercado("175000", "25000", "16.67"));
        assertThat(resumo.itens().get(1).mercado()).contains(mercado("36000", "6000", "20.00"));
        assertThat(resumo.custoTotal()).isEqualTo(Dinheiro.de("180000"));
        assertThat(resumo.mercado()).contains(mercado("211000", "31000", "17.22"));
        assertThat(resumo.ativosSemPreco()).isEmpty();
    }

    @Test
    void ativoSemPrecoFicaSemValoresDeMercadoEForaDoTotal() {
        when(posicoes.listarTodas()).thenReturn(List.of(BTC, ETH));

        var resumo = consultar.executar(Map.of("BTC", new BigDecimal("350000")));

        var itemEth = resumo.itens().get(1);
        assertThat(itemEth.custo()).isEqualTo(Dinheiro.de("30000"));
        assertThat(itemEth.mercado()).isEmpty();
        // o custo total conta tudo; o mercado do total só conta o BTC
        assertThat(resumo.custoTotal()).isEqualTo(Dinheiro.de("180000"));
        assertThat(resumo.mercado()).contains(mercado("175000", "25000", "16.67"));
        assertThat(resumo.ativosSemPreco()).containsExactly("ETH");
    }

    @Test
    void semNenhumPrecoOTotalFicaSemMercado() {
        when(posicoes.listarTodas()).thenReturn(List.of(BTC, ETH));

        var resumo = consultar.executar(Map.of());

        assertThat(resumo.custoTotal()).isEqualTo(Dinheiro.de("180000"));
        assertThat(resumo.mercado()).isEmpty();
        assertThat(resumo.ativosSemPreco()).containsExactly("BTC", "ETH");
    }

    @Test
    void aceitaSimboloEmMinusculasEComEspacosNosPrecos() {
        when(posicoes.listarTodas()).thenReturn(List.of(BTC));

        var resumo = consultar.executar(Map.of(" btc ", new BigDecimal("350000")));

        assertThat(resumo.ativosSemPreco()).isEmpty();
        assertThat(resumo.mercado()).contains(mercado("175000", "25000", "16.67"));
    }

    @Test
    void carteiraVaziaDevolveTudoZeradoESemMercado() {
        when(posicoes.listarTodas()).thenReturn(List.of());

        var resumo = consultar.executar(Map.of());

        assertThat(resumo.itens()).isEmpty();
        assertThat(resumo.custoTotal()).isEqualTo(Dinheiro.zero());
        assertThat(resumo.mercado()).isEmpty();
        assertThat(resumo.ativosSemPreco()).isEmpty();
    }

    private static ValorDeMercado mercado(String valorAtual, String lucro, String variacao) {
        return new ValorDeMercado(Dinheiro.de(valorAtual), Dinheiro.de(lucro), new BigDecimal(variacao));
    }
}