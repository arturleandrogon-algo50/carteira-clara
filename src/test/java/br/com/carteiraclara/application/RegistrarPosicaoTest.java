package br.com.carteiraclara.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.carteiraclara.domain.Ativo;
import br.com.carteiraclara.domain.Posicao;
import br.com.carteiraclara.domain.PosicaoRepositorio;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Testa o caso de uso com um dublê da porta (sem banco). */
@ExtendWith(MockitoExtension.class)
class RegistrarPosicaoTest {

    @Mock
    private PosicaoRepositorio posicoes;

    private RegistrarPosicao registrar;

    @BeforeEach
    void preparar() {
        registrar = new RegistrarPosicao(posicoes);
    }

    @Test
    void registraUsandoOAtivoDoCatalogo() {
        when(posicoes.salvar(any(Posicao.class))).thenAnswer(chamada -> chamada.getArgument(0));

        var registrada = registrar.executar("btc", new BigDecimal("0.5"), new BigDecimal("300000"));

        assertThat(registrada).isEqualTo(Posicao.de(Ativo.BITCOIN, "0.5", "300000"));
        verify(posicoes).salvar(Posicao.de(Ativo.BITCOIN, "0.5", "300000"));
    }

    @Test
    void recusaAtivoForaDoCatalogo() {
        assertThatThrownBy(() -> registrar.executar("XYZ", BigDecimal.ONE, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XYZ");
        verify(posicoes, never()).salvar(any());
    }

    @Test
    void recusaQuantidadeNegativa() {
        assertThatThrownBy(() -> registrar.executar("ETH", new BigDecimal("-1"), BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
        verify(posicoes, never()).salvar(any());
    }
}