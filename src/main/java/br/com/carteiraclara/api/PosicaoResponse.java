package br.com.carteiraclara.api;

import java.math.BigDecimal;

import br.com.carteiraclara.domain.Posicao;

/** O JSON que sai do POST /api/posicoes: a posição como ficou salva. */
public record PosicaoResponse(
        String simbolo,
        String nome,
        BigDecimal quantidade,
        BigDecimal precoMedio,
        BigDecimal custo) {

    static PosicaoResponse de(Posicao posicao) {
        return new PosicaoResponse(
                posicao.ativo().simbolo(),
                posicao.ativo().nome(),
                posicao.quantidade(),
                posicao.precoMedio(),
                posicao.custoTotal().valor());
    }
}