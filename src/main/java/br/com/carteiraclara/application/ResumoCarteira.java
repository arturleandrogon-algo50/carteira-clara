package br.com.carteiraclara.application;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import br.com.carteiraclara.domain.Dinheiro;

/**
 * O resultado da consulta: as linhas da carteira e os totais.
 *
 * <p>O custo total soma todas as posições. O mercado do total soma só as
 * posições que têm preço (e compara com o custo delas), para um ativo sem
 * preço não aparecer como prejuízo falso. Quem ficou de fora está em
 * {@code ativosSemPreco}. Se nenhum ativo tiver preço, o mercado fica vazio.
 */
public record ResumoCarteira(
        List<ItemCarteira> itens,
        Dinheiro custoTotal,
        Optional<ValorDeMercado> mercado,
        List<String> ativosSemPreco) {

    public ResumoCarteira {
        Objects.requireNonNull(custoTotal, "O custo total não pode ser nulo");
        Objects.requireNonNull(mercado, "Use Optional.empty() quando não houver preço");
        itens = List.copyOf(itens);
        ativosSemPreco = List.copyOf(ativosSemPreco);
    }
}