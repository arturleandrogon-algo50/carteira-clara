-- Catálogo de ativos acompanhados pelo sistema.
-- coingecko_id é o identificador usado para buscar preços (etapa 4).
CREATE TABLE ativo (
    id           BIGSERIAL PRIMARY KEY,
    simbolo      VARCHAR(16)  NOT NULL UNIQUE,
    nome         VARCHAR(80)  NOT NULL,
    coingecko_id VARCHAR(80)  NOT NULL UNIQUE,
    criado_em    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

INSERT INTO ativo (simbolo, nome, coingecko_id) VALUES
    ('BTC', 'Bitcoin',  'bitcoin'),
    ('ETH', 'Ethereum', 'ethereum');
