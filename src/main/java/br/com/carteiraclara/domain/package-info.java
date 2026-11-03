/**
 * Núcleo do sistema: regras de negócio em Java puro (Ativo, Posição, Decisão).
 *
 * <p>Regra de ouro da arquitetura hexagonal: este pacote NÃO depende de Spring,
 * de JPA nem de qualquer provedor externo. Tudo que vem de fora entra por
 * interfaces (portas) declaradas aqui e implementadas em {@code infra}.
 */
package br.com.carteiraclara.domain;
