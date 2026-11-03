# Progresso

Última atualização: 2026-11-03

## Etapa 1: Fundação

### Validado

- [x] `mvn verify`: BUILD SUCCESS, 2 testes passando (`StatusControllerTest`).
      O `pom.xml` (starters do Spring Boot 4.0.6) estava correto, sem ajustes.
- [x] Docker Desktop (WSL2) instalado: Docker 29.9.2.
- [x] `docker compose up --build`: banco (Postgres 17) saudável e aplicação no ar.
- [x] Flyway: migração `V1__criar_tabela_ativo.sql` validada e aplicada (schema na versão 1).
- [x] <http://localhost:8080/api/status> responde `{"situacao":"no ar", ...}`.
- [x] <http://localhost:8080/actuator/health> responde `{"status":"UP"}`.

### Ainda não validado

- [ ] CI no GitHub Actions (`.github/workflows/ci.yml`): só roda depois do push;
      o workflow ainda não foi executado.
- [x] Badge do README apontando para `arturleandrogon-algo50/carteira-clara`
      (só vai mostrar o status depois que o repositório existir no GitHub com esse
      nome e o CI rodar).
- [ ] Rodar com Java 21 (a máquina local usa JDK 25; o container e o CI definem
      a versão real).
- [ ] Maven no `Path` do Windows (adicionar `C:\Users\User\tools\apache-maven-3.9.16\bin`
      pela interface; a gravação automática foi bloqueada).

## Notas do ambiente (só nesta máquina, não vão para o repositório)

- Rede/antivírus intercepta HTTPS e o Java não confiava no certificado. Solução:
  variável de usuário `MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=WINDOWS-ROOT`.
- Maven 3.9.16 instalado manualmente em `C:\Users\User\tools\apache-maven-3.9.16`
  (não existe pacote do Maven no winget).
- Se o mesmo erro de certificado aparecer em outras ferramentas, a causa é a mesma.

## Próximo passo

Etapa 2: domínio (`Ativo`, `Posicao`, `Dinheiro` e cálculo de lucro/prejuízo),
em Java puro, com testes unitários.
