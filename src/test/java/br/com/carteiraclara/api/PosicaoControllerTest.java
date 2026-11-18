package br.com.carteiraclara.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.carteiraclara.application.ConsultarCarteira;
import br.com.carteiraclara.application.ItemCarteira;
import br.com.carteiraclara.application.RegistrarPosicao;
import br.com.carteiraclara.application.ResumoCarteira;
import br.com.carteiraclara.application.ValorDeMercado;
import br.com.carteiraclara.domain.Ativo;
import br.com.carteiraclara.domain.Dinheiro;
import br.com.carteiraclara.domain.Posicao;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Testa o controller chamando os métodos direto, com dublês dos casos de uso (sem servidor). */
@ExtendWith(MockitoExtension.class)
class PosicaoControllerTest {

    private static final Posicao BTC = Posicao.de(Ativo.BITCOIN, "0.5", "300000");
    private static final Posicao ETH = Posicao.de(Ativo.ETHEREUM, "2", "15000");

    @Mock
    private RegistrarPosicao registrarPosicao;

    @Mock
    private ConsultarCarteira consultarCarteira;

    private PosicaoController controller;

    @BeforeEach
    void preparar() {
        controller = new PosicaoController(registrarPosicao, consultarCarteira);
    }

    @Test
    void registrarDevolveAPosicaoSalva() {
        when(registrarPosicao.executar("BTC", new BigDecimal("0.5"), new BigDecimal("300000")))
                .thenReturn(BTC);

        var resposta = controller.registrar(
                new RegistrarPosicaoRequest("BTC", new BigDecimal("0.5"), new BigDecimal("300000")));

        assertThat(resposta.simbolo()).isEqualTo("BTC");
        assertThat(resposta.nome()).isEqualTo("Bitcoin");
        assertThat(resposta.quantidade()).isEqualByComparingTo("0.5");
        assertThat(resposta.custo()).isEqualByComparingTo("150000");
    }

    @Test
    void consultarConverteOsPrecosDaUrlEMontaOResumo() {
        var mercadoBtc = new ValorDeMercado(Dinheiro.de("175000"), Dinheiro.de("25000"), new BigDecimal("16.67"));
        var resumo = new ResumoCarteira(
                List.of(new ItemCarteira(BTC, Dinheiro.de("150000"), Optional.of(mercadoBtc)),
                        new ItemCarteira(ETH, Dinheiro.de("30000"), Optional.empty())),
                Dinheiro.de("180000"),
                Optional.of(mercadoBtc),
                List.of("ETH"));
        when(consultarCarteira.executar(Map.of("BTC", new BigDecimal("350000")))).thenReturn(resumo);

        var resposta = controller.consultar(Map.of("BTC", "350000"));

        var itemBtc = resposta.itens().get(0);
        assertThat(itemBtc.valorAtual()).isEqualByComparingTo("175000");
        assertThat(itemBtc.variacaoPercentual()).isEqualByComparingTo("16.67");
        var itemEth = resposta.itens().get(1);
        assertThat(itemEth.custo()).isEqualByComparingTo("30000");
        assertThat(itemEth.valorAtual()).isNull();
        assertThat(itemEth.lucroOuPrejuizo()).isNull();
        assertThat(itemEth.variacaoPercentual()).isNull();
        assertThat(resposta.custoTotal()).isEqualByComparingTo("180000");
        assertThat(resposta.lucroOuPrejuizo()).isEqualByComparingTo("25000");
        assertThat(resposta.ativosSemPreco()).containsExactly("ETH");
    }

    @Test
    void semNenhumPrecoOsTotaisDeMercadoVemNulosEOAvisoContinua() {
        var resumo = new ResumoCarteira(
                List.of(new ItemCarteira(BTC, Dinheiro.de("150000"), Optional.empty())),
                Dinheiro.de("150000"),
                Optional.empty(),
                List.of("BTC"));
        when(consultarCarteira.executar(Map.of())).thenReturn(resumo);

        var resposta = controller.consultar(Map.of());

        assertThat(resposta.custoTotal()).isEqualByComparingTo("150000");
        assertThat(resposta.valorAtual()).isNull();
        assertThat(resposta.lucroOuPrejuizo()).isNull();
        assertThat(resposta.variacaoPercentual()).isNull();
        assertThat(resposta.ativosSemPreco()).containsExactly("BTC");
        assertThat(resposta.aviso()).contains("Não é recomendação de investimento");
    }

    @Test
    void precoQueNaoENumeroDaErroClaroSemChamarOCasoDeUso() {
        assertThatThrownBy(() -> controller.consultar(Map.of("BTC", "abc")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("BTC");
        verify(consultarCarteira, never()).executar(any());
    }

    @Test
    void tratadorDeErrosRepassaAMensagemDoDominio() {
        var resposta = new TratadorDeErros()
                .argumentoInvalido(new IllegalArgumentException("Ativo fora do catálogo: XYZ"));

        assertThat(resposta.erro()).isEqualTo("Ativo fora do catálogo: XYZ");
    }
}