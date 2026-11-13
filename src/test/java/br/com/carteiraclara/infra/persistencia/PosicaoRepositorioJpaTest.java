package br.com.carteiraclara.infra.persistencia;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.carteiraclara.domain.Ativo;
import br.com.carteiraclara.domain.Posicao;

/** Testa a lógica do adaptador com dublês dos repositórios (sem banco). */
@ExtendWith(MockitoExtension.class)
class PosicaoRepositorioJpaTest {

    @Mock
    private PosicaoJpaRepository posicoes;

    @Mock
    private AtivoJpaRepository ativos;

    private PosicaoRepositorioJpa repositorio;

    @BeforeEach
    void preparar() {
        repositorio = new PosicaoRepositorioJpa(posicoes, ativos);
    }

    @Test
    void salvarCriaPosicaoQuandoAindaNaoExiste() {
        var eth = ativoEntidade("ETH", "Ethereum");
        when(ativos.findBySimbolo("ETH")).thenReturn(Optional.of(eth));
        when(posicoes.findByAtivoSimbolo("ETH")).thenReturn(Optional.empty());
        when(posicoes.save(any(PosicaoEntidade.class))).thenAnswer(chamada -> chamada.getArgument(0));

        var salva = repositorio.salvar(Posicao.de(Ativo.ETHEREUM, "2", "10000"));

        assertThat(salva).isEqualTo(Posicao.de(Ativo.ETHEREUM, "2", "10000"));
    }

    @Test
    void salvarAtualizaPosicaoQueJaExiste() {
        var eth = ativoEntidade("ETH", "Ethereum");
        var existente = new PosicaoEntidade(eth, new BigDecimal("1"), new BigDecimal("9000"));
        when(ativos.findBySimbolo("ETH")).thenReturn(Optional.of(eth));
        when(posicoes.findByAtivoSimbolo("ETH")).thenReturn(Optional.of(existente));
        when(posicoes.save(any(PosicaoEntidade.class))).thenAnswer(chamada -> chamada.getArgument(0));

        var salva = repositorio.salvar(Posicao.de(Ativo.ETHEREUM, "3", "9500"));

        assertThat(salva).isEqualTo(Posicao.de(Ativo.ETHEREUM, "3", "9500"));
        verify(posicoes).save(existente);
    }

    @Test
    void salvarRecusaAtivoQueNaoEstaNoCatalogo() {
        when(ativos.findBySimbolo("XYZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repositorio.salvar(Posicao.de(Ativo.cripto("XYZ", "Moeda X"), "1", "1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XYZ");
        verify(posicoes, never()).save(any());
    }

    @Test
    void buscarPorSimboloAceitaLetrasMinusculasEEspacos() {
        var btc = ativoEntidade("BTC", "Bitcoin");
        var entidade = new PosicaoEntidade(btc, new BigDecimal("0.5"), new BigDecimal("300000"));
        when(posicoes.findByAtivoSimbolo("BTC")).thenReturn(Optional.of(entidade));

        var encontrada = repositorio.buscarPorSimbolo(" btc ");

        assertThat(encontrada).contains(Posicao.de(Ativo.BITCOIN, "0.5", "300000"));
    }

    @Test
    void listarTodasConverteCadaLinhaParaODominio() {
        var btc = ativoEntidade("BTC", "Bitcoin");
        var eth = ativoEntidade("ETH", "Ethereum");
        when(posicoes.findAll()).thenReturn(List.of(
                new PosicaoEntidade(btc, new BigDecimal("0.5"), new BigDecimal("300000")),
                new PosicaoEntidade(eth, new BigDecimal("2"), new BigDecimal("10000"))));

        var todas = repositorio.listarTodas();

        assertThat(todas).containsExactly(
                Posicao.de(Ativo.BITCOIN, "0.5", "300000"),
                Posicao.de(Ativo.ETHEREUM, "2", "10000"));
    }

    /** Dublê de uma linha da tabela "ativo" (a entidade real só é preenchida pelo JPA). */
    private static AtivoEntidade ativoEntidade(String simbolo, String nome) {
        var ativo = mock(AtivoEntidade.class);
        lenient().when(ativo.getSimbolo()).thenReturn(simbolo);
        lenient().when(ativo.getNome()).thenReturn(nome);
        return ativo;
    }
}