package br.com.carteiraclara.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import br.com.carteiraclara.domain.Ativo;
import br.com.carteiraclara.domain.Posicao;
import br.com.carteiraclara.domain.PosicaoRepositorio;

/**
 * Caso de uso: registrar a posição de um ativo na carteira.
 * Uma posição por ativo: registrar de novo o mesmo ativo substitui a anterior.
 */
@Service
public class RegistrarPosicao {

    private final PosicaoRepositorio posicoes;

    public RegistrarPosicao(PosicaoRepositorio posicoes) {
        this.posicoes = posicoes;
    }

    public Posicao executar(String simbolo, BigDecimal quantidade, BigDecimal precoMedio) {
        var ativo = Ativo.doCatalogo(simbolo);
        var posicao = new Posicao(ativo, quantidade, precoMedio);
        return posicoes.salvar(posicao);
    }
}