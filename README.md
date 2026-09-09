# toQuerendo API

API do projeto **toQuerendo** (TCC), desenvolvida em **Java 17** com **Spring Boot 3.5**. Conecta turistas a vendedores locais de praia, permitindo consultar produtos e registrar intenções de compra.

## Stack

- Java 17 + Spring Boot 3.5 (Web, Data JPA, Validation, Security)
- PostgreSQL 17
- Flyway (versionamento de schema)
- JWT (autenticação stateless, assinatura HS256)
- Springdoc OpenAPI (Swagger UI)
- Lombok

## Pré-requisitos

- JDK 17
- Docker (para subir o PostgreSQL via `docker-compose`)

Não é necessário ter o Maven instalado — o projeto usa o Maven Wrapper (`mvnw` / `mvnw.cmd`).

## Como rodar

### 1. Subir o banco de dados

```bash
docker compose up -d
```

Isso sobe um PostgreSQL 17 na porta `5432` com o banco `toQuerendo` (usuário `postgres`, senha `toQuerendo321@`, já configurados em [application.properties](src/main/resources/application.properties)).

### 2. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

No Windows (PowerShell/cmd):

```bash
mvnw.cmd spring-boot:run
```

Na subida, o Flyway aplica automaticamente as migrations em [src/main/java/br/com/toquerendo/db/migration](src/main/java/br/com/toquerendo/db/migration), criando as tabelas e populando dados básicos (ex.: categorias `turista` e `vendedor`).

A aplicação sobe em `http://localhost:8080`. Documentação interativa (Swagger UI): `http://localhost:8080/swagger-ui/index.html`.

## Autenticação

A API usa **JWT com assinatura simétrica (HS256)**: o próprio backend gera e valida os tokens, sem depender de um serviço externo de emissão. Rotas protegidas exigem o header:

```
Authorization: Bearer <token>
```

Rotas públicas (não exigem token): `POST /usuario` (cadastro) e `POST /auth/login`.

O segredo e o tempo de expiração do token são configuráveis via variáveis de ambiente (com valores padrão para desenvolvimento):

| Variável | Descrição | Padrão |
|---|---|---|
| `JWT_SECRET` | Segredo usado para assinar/validar o token | valor de desenvolvimento embutido |
| `JWT_EXPIRATION_MS` | Tempo de expiração do token em milissegundos | `3600000` (1 hora) |

**Importante:** em produção, `JWT_SECRET` deve ser sobrescrito por um valor forte e mantido fora do controle de versão.

### 1. Cadastrar um usuário

```
POST /usuario
Content-Type: application/json

{
  "email": "usuario@exemplo.com",
  "nome": "Nome do Usuário",
  "senha": "senha123",
  "telefone": "11999999999",
  "cpf": "12345678900"
}
```

Campos obrigatórios: `email`, `nome`, `senha` (mínimo 6 caracteres), `telefone`, `cpf` (11 dígitos). A senha é armazenada com hash BCrypt — nunca em texto puro. Todo usuário cadastrado por essa rota recebe a categoria padrão `turista`.

### 2. Gerar o token (login)

```
POST /auth/login
Content-Type: application/json

{
  "email": "usuario@exemplo.com",
  "senha": "senha123"
}
```

Resposta:

```json
{
  "response": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tipo": "Bearer",
    "expiraEmMs": 3600000
  },
  "messages": ["Login realizado com sucesso!"]
}
```

### 3. Usar o token

Envie o token retornado no header `Authorization` das requisições às rotas protegidas:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
