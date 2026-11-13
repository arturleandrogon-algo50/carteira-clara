package br.com.carteiraclara.domain;

import java.util.List;
import java.util.Optional;

/**
 * Porta do domínio: o que precisamos guardar e buscar de posições.
 * A implementação (PostgreSQL com JPA) fica na camada infra.
 * Há no máximo uma posição por ativo, identificada pelo símbolo.
 */
public interface PosicaoRepositorio {

    /** Cria a posição do ativo ou, se já existir, substitui pela nova. */
    Posicao salvar(Posicao posicao);

    Optional<Posicao> buscarPorSimbolo(String simbolo);

    List<Posicao> listarTodas();
}