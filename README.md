# Carteira Clara

![CI](https://github.com/arturleandrogon-algo50/carteira-clara/actions/workflows/ci.yml/badge.svg)

Acompanhamento de carteira cripto **em reais**, com explicações em português
simples e uma IA como assistente. O app mostra o que a pessoa tem, o que
mudou e quais caminhos existem. **A decisão é sempre do usuário.**

> **Aviso:** ferramenta informativa e educativa. Não é recomendação de
> investimento, e a IA nunca diz "compre" ou "venda".

## Status

Em construção. Fundação pronta (etapa 1 de 8). Veja o [roadmap](#roadmap).

## Como rodar

Só precisa de Docker:

```bash
docker compose up --build
```

Depois acesse:

- <http://localhost:8080/api/status>
- <http://localhost:8080/actuator/health>

O projeto roda **sem nenhuma chave de API**. Provedores externos (preços e IA)
terão um modo `mock` para quem quiser testar sem configurar nada.

### Rodando sem Docker para a aplicação

Com Java 21 e Maven instalados, suba só o banco e rode a aplicação:

```bash
docker compose up -d db
mvn spring-boot:run
```

Testes:

```bash
mvn verify
```

## Arquitetura

Arquitetura hexagonal: o domínio (regras de negócio) não conhece Spring,
banco nem provedores externos. Tudo que vem de fora entra por interfaces.

```
api  ->  application  ->  domain  <-  infra
(REST)   (casos de uso)    (regras)    (Postgres, preços, IA)
```

Decisões registradas em [`docs/adr`](docs/adr).

## Stack

Java 21, Spring Boot, PostgreSQL, Flyway, Docker, GitHub Actions.
Planejado: Spring AI, Testcontainers, WireMock, Resilience4j, OpenAPI.

## Roadmap

- [x] 1. Fundação: projeto, Postgres no Docker, CI
- [ ] 2. Domínio: ativo, posição, lucro e prejuízo, com testes
- [ ] 3. Persistência e API de posições
- [ ] 4. Preços reais (CoinGecko) com cache e tratamento de limite
- [ ] 5. Histórico de cotações agendado
- [ ] 6. Análise da carteira com IA (saída estruturada, perfil mock)
- [ ] 7. Registro de decisões e ficha de fundamentos por ativo
- [ ] 8. Tela simples, Swagger, GIF de demonstração, Codespaces
