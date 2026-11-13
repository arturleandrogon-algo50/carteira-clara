package br.com.carteiraclara.infra.persistencia;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultas do Spring Data sobre a tabela "ativo". Uso interno da camada infra. */
interface AtivoJpaRepository extends JpaRepository<AtivoEntidade, Long> {

    Optional<AtivoEntidade> findBySimbolo(String simbolo);
}