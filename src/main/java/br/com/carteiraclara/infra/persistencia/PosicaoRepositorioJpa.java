package br.com.carteiraclara.infra.persistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.carteiraclara.domain.Ativo;
import br.com.carteiraclara.domain.Posicao;
import br.com.carteiraclara.domain.PosicaoRepositorio;

/**
 * Adaptador: implementa a porta PosicaoRepositorio do domínio usando JPA.
 * Traduz entre o record Posicao (domínio) e a PosicaoEntidade (banco).
 */
@Repository
public class PosicaoRepositorioJpa implements PosicaoRepositorio {

    private final PosicaoJpaRepository posicoes;
    private final AtivoJpaRepository ativos;

    PosicaoRepositorioJpa(PosicaoJpaRepository posicoes, AtivoJpaRepository ativos) {
        this.posicoes = posicoes;
        this.ativos = ativos;
    }

    @Override
    @Transactional
    public Posicao salvar(Posicao posicao) {
        var simbolo = posicao.ativo().simbolo();
        var ativo = ativos.findBySimbolo(simbolo)
                .orElseThrow(() -> new IllegalArgumentException("Ativo não cadastrado: " + simbolo));

        var entidade = posicoes.findByAtivoSimbolo(simbolo)
                .map(existente -> {
                    existente.atualizar(posicao.quantidade(), posicao.precoMedio());
                    return existente;
                })
                .orElseGet(() -> new PosicaoEntidade(ativo, posicao.quantidade(), posicao.precoMedio()));

        return paraDominio(posicoes.save(entidade));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Posicao> buscarPorSimbolo(String simbolo) {
        return posicoes.findByAtivoSimbolo(simbolo.trim().toUpperCase())
                .map(PosicaoRepositorioJpa::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Posicao> listarTodas() {
        return posicoes.findAll().stream()
                .map(PosicaoRepositorioJpa::paraDominio)
                .toList();
    }

    /** Converte a linha do banco no record do domínio. Por enquanto todo ativo é cripto. */
    private static Posicao paraDominio(PosicaoEntidade entidade) {
        var ativo = Ativo.cripto(entidade.getAtivo().getSimbolo(), entidade.getAtivo().getNome());
        return new Posicao(ativo, entidade.getQuantidade(), entidade.getPrecoMedio());
    }
}