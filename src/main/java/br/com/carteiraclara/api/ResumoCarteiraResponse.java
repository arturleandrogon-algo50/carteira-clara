package br.com.carteiraclara.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import br.com.carteiraclara.application.ItemCarteira;
import br.com.carteiraclara.application.ResumoCarteira;
import br.com.carteiraclara.application.ValorDeMercado;

/**
 * O JSON que sai do GET /api/posicoes.
 * Aqui o Optional do sistema vira null, que é o costume em JSON:
 * sem preço, valorAtual, lucroOuPrejuizo e variacaoPercentual vêm null.
 */
public record ResumoCarteiraResponse(
        List<ItemResponse> itens,
        BigDecimal custoTotal,
        BigDecimal valorAtual,
        BigDecimal lucroOuPrejuizo,
        BigDecimal variacaoPercentual,
        List<String> ativosSemPreco,
        String aviso) {

    /** Uma linha da carteira no JSON. */
    public record ItemResponse(
            String simbolo,
            String nome,
            BigDecimal quantidade,
            BigDecimal precoMedio,
            BigDecimal custo,
            BigDecimal valorAtual,
            BigDecimal lucroOuPrejuizo,
            BigDecimal variacaoPercentual) {

        static ItemResponse de(ItemCarteira item) {
            var posicao = item.posicao();
            var mercado = item.mercado();
            return new ItemResponse(
                    posicao.ativo().simbolo(),
                    posicao.ativo().nome(),
                    posicao.quantidade(),
                    posicao.precoMedio(),
                    item.custo().valor(),
                    valorAtualDe(mercado),
                    lucroDe(mercado),
                    variacaoDe(mercado));
        }
    }

    static ResumoCarteiraResponse de(ResumoCarteira resumo) {
        var mercado = resumo.mercado();
        return new ResumoCarteiraResponse(
                resumo.itens().stream().map(ItemResponse::de).toList(),
                resumo.custoTotal().valor(),
                valorAtualDe(mercado),
                lucroDe(mercado),
                variacaoDe(mercado),
                resumo.ativosSemPreco(),
                StatusController.AVISO);
    }

    private static BigDecimal valorAtualDe(Optional<ValorDeMercado> mercado) {
        return mercado.map(m -> m.valorAtual().valor()).orElse(null);
    }

    private static BigDecimal lucroDe(Optional<ValorDeMercado> mercado) {
        return mercado.map(m -> m.lucroOuPrejuizo().valor()).orElse(null);
    }

    private static BigDecimal variacaoDe(Optional<ValorDeMercado> mercado) {
        return mercado.map(ValorDeMercado::variacaoPercentual).orElse(null);
    }
}