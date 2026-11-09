-- Posições: quanto a pessoa tem de cada ativo e o preço médio pago (em reais).
-- Uma posição por ativo. Quantidade e preço médio com 8 casas, como na classe Posicao.
CREATE TABLE posicao (
    id           BIGSERIAL PRIMARY KEY,
    ativo_id     BIGINT         NOT NULL UNIQUE REFERENCES ativo (id),
    quantidade   NUMERIC(28, 8) NOT NULL CHECK (quantidade >= 0),
    preco_medio  NUMERIC(28, 8) NOT NULL CHECK (preco_medio >= 0),
    criado_em    TIMESTAMPTZ    NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ   NOT NULL DEFAULT now()
);
