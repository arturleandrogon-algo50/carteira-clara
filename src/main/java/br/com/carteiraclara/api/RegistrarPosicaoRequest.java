package br.com.carteiraclara.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * O JSON que chega no POST /api/posicoes.
 * Exemplo: {"simbolo": "BTC", "quantidade": 0.5, "precoMedio": 300000}
 * As anotações barram campos vazios ou negativos antes de chegar ao caso de uso.
 */
public record RegistrarPosicaoRequest(
        @NotBlank String simbolo,
        @NotNull @PositiveOrZero BigDecimal quantidade,
        @NotNull @PositiveOrZero BigDecimal precoMedio) {
}