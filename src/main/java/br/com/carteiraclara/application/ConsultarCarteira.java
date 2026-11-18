package br.com.carteiraclara.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.carteiraclara.domain.Dinheiro;
import br.com.carteiraclara.domain.PosicaoRepositorio;

/**
 * Caso de uso: consultar a carteira com os preços atuais.
 *
 * <p>Recebe os preços (símbolo para preço de 1 unidade, em reais). Ativo sem
 * preço continua na lista com o custo, mas sem valores de mercado, e fica
 * fora do mercado do total (veja {@link ResumoCarteira}).
 */
@Service
public class ConsultarCarteira {

    private static final BigDecimal CEM = new BigDecimal("100");

    private final PosicaoRepositorio posicoes;

    public ConsultarCarteira(PosicaoRepositorio posicoes) {
        this.posicoes = posicoes;
    }

    public ResumoCarteira executar(Map<String, BigDecimal> precosAtuais) {
        var precos = normalizar(precosAtuais);

        var itens = new ArrayList<ItemCarteira>();
        var ativosSemPreco = new ArrayList<String>();
        var custoTotal = Dinheiro.zero();
        var custoComPreco = Dinheiro.zero();
        var valorComPreco = Dinheiro.zero();
        var algumComPreco = false;

        for (var posicao : posicoes.listarTodas()) {
            var simbolo = posicao.ativo().simbolo();
            var custo = posicao.custoTotal();
            custoTotal = custoTotal.somar(custo);

            var preco = precos.get(simbolo);
            if (preco == null) {
                ativosSemPreco.add(simbolo);
                itens.add(new ItemCarteira(posicao, custo, Optional.empty()));
                continue;
            }

            var mercado = new ValorDeMercado(
                    posicao.valorAtual(preco),
                    posicao.lucroOuPrejuizo(preco),
                    posicao.variacaoPercentual(preco));
            itens.add(new ItemCarteira(posicao, custo, Optional.of(mercado)));

            custoComPreco = custoComPreco.somar(custo);
            valorComPreco = valorComPreco.somar(mercado.valorAtual());
            algumComPreco = true;
        }

        Optional<ValorDeMercado> mercadoTotal = algumComPreco
                ? Optional.of(totalDeMercado(custoComPreco, valorComPreco))
                : Optional.empty();

        return new ResumoCarteira(itens, custoTotal, mercadoTotal, ativosSemPreco);
    }

    /** Mesma regra da Posicao: variação com 2 casas e 0,00 se o custo for zero. */
    private static ValorDeMercado totalDeMercado(Dinheiro custo, Dinheiro valorAtual) {
        var lucro = valorAtual.subtrair(custo);
        var variacao = custo.valor().signum() == 0
                ? BigDecimal.ZERO.setScale(2)
                : lucro.valor().multiply(CEM).divide(custo.valor(), 2, RoundingMode.HALF_EVEN);
        return new ValorDeMercado(valorAtual, lucro, variacao);
    }

    /** Deixa os símbolos como no catálogo: sem espaços e em maiúsculas. */
    private static Map<String, BigDecimal> normalizar(Map<String, BigDecimal> precosAtuais) {
        Objects.requireNonNull(precosAtuais, "Os preços atuais não podem ser nulos");
        var precos = new HashMap<String, BigDecimal>();
        precosAtuais.forEach((simbolo, preco) -> precos.put(simbolo.trim().toUpperCase(), preco));
        return precos;
    }
}