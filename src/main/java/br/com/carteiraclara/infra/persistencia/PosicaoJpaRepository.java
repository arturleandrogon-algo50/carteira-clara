package br.com.carteiraclara.infra.persistencia;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/** Consultas do Spring Data sobre a tabela "posicao". Uso interno da camada infra. */
interface PosicaoJpaRepository extends JpaRepository<PosicaoEntidade, Long> {

    /** Busca a posição pelo símbolo do ativo, por exemplo "ETH". */
    Optional<PosicaoEntidade> findByAtivoSimbolo(String simbolo);
}
