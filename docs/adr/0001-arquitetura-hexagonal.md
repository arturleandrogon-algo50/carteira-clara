# ADR 0001: Arquitetura hexagonal (portas e adaptadores)

**Status:** aceita
**Data:** 2026-10-26

## Contexto

O sistema depende de coisas que mudam e falham: APIs de preço com limite de
requisições, provedores de IA diferentes (nuvem ou local) e o banco de dados.
Queremos que o projeto cresça (novos ativos, novos provedores, eventualmente
usuários e login) sem reescrever as regras de negócio.

## Decisão

Separar o código em quatro camadas:

- `domain`: regras de negócio em Java puro, sem Spring nem JPA.
- `application`: casos de uso que orquestram o domínio.
- `infra`: adaptadores que implementam as portas do domínio (Postgres,
  CoinGecko, provedor de IA, mocks).
- `api`: controllers REST e DTOs.

A dependência sempre aponta para dentro: `api` e `infra` conhecem o domínio,
nunca o contrário. Provedores externos entram por interfaces, e cada um tem
uma implementação `mock` para que o projeto rode sem chave de API.

## Consequências

- Trocar de provedor de preço ou de IA não altera o domínio.
- Testes do domínio são rápidos e não precisam de banco nem de rede.
- Há mais arquivos (interfaces e adaptadores) do que em um CRUD direto. Aceitamos
  esse custo em troca de testabilidade e de espaço para crescer.
