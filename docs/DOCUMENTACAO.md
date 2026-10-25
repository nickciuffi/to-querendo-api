# toQuerendo API — Documentação Completa do Projeto

> **Finalidade deste documento:** servir de fonte única de contexto (para pessoas e para modelos/agentes) sobre o que o projeto faz, como é construído, quais regras de negócio existem e quais convenções seguir. Foi escrito a partir da leitura do código, não de suposições.
>
> **Base da análise:** branch `feature/delete-intencao-compra`, último commit `79bbca8`, mais as alterações ainda não commitadas da funcionalidade de conclusão de intenção de compra (migration V9, endpoint `DELETE /intencao-compra`). Data: 2026-10-24.
>
> **Aviso de validade:** código muda. Em caso de divergência entre este documento e o código, **o código vence** — e este documento deve ser corrigido. A seção [14](#14-inconsistências-e-débitos-técnicos-conhecidos) lista divergências já conhecidas (inclusive entre o README e o código).

---

## Sumário

1. [Visão geral do produto](#1-visão-geral-do-produto)
2. [Stack e versões](#2-stack-e-versões)
3. [Estrutura do repositório e arquitetura](#3-estrutura-do-repositório-e-arquitetura)
4. [Configuração, execução e build](#4-configuração-execução-e-build)
5. [Modelo de dados](#5-modelo-de-dados)
6. [Autenticação e autorização](#6-autenticação-e-autorização)
7. [Regras de negócio por módulo](#7-regras-de-negócio-por-módulo)
8. [Catálogo de endpoints](#8-catálogo-de-endpoints)
9. [Contrato de resposta e tratamento de erros](#9-contrato-de-resposta-e-tratamento-de-erros)
10. [DTOs e validações](#10-dtos-e-validações)
11. [Convenções de código](#11-convenções-de-código)
12. [Estratégia de testes](#12-estratégia-de-testes)
13. [Armadilhas já encontradas (lições aprendidas)](#13-armadilhas-já-encontradas-lições-aprendidas)
14. [Inconsistências e débitos técnicos conhecidos](#14-inconsistências-e-débitos-técnicos-conhecidos)
15. [Funcionalidades previstas mas não expostas](#15-funcionalidades-previstas-mas-não-expostas)
16. [Fluxos de ponta a ponta](#16-fluxos-de-ponta-a-ponta)
17. [Deploy e operação](#17-deploy-e-operação)
18. [Fluxo de trabalho com Git](#18-fluxo-de-trabalho-com-git)
19. [Glossário](#19-glossário)

---

## 1. Visão geral do produto

**toQuerendo** é um projeto de TCC. A API REST conecta **turistas (banhistas)** a **vendedores ambulantes de praia**.

Ideia central: o turista, na praia, registra que **quer** um produto (ex.: água de coco) e onde está (descrição do local, foto). O vendedor da mesma praia que vende aquele tipo de produto consulta quem está querendo comprar e vai até a pessoa. O turista também pode ver **onde estão os vendedores online** de determinado produto, em tempo (quase) real, via localização que cada usuário envia periodicamente.

### Atores e categorias

| Categoria | Id | Descrição | Como se torna |
|---|---|---|---|
| **Turista** | 1 | Banhista. Cria intenções de compra, consulta vendedores e localização. | Todo cadastro público cria um turista. |
| **Vendedor** | 2 | Turista "promovido": cadastra produtos específicos, aparece no mapa, vê banhistas. | O próprio usuário autenticado chama `POST /vendedor` (precisa ter telefone e CPF). |
| **Administrador** | 3 | Gerencia catálogo (produtos base) e praias. | Não há endpoint: contas de admin entram por **seed de migration** (V7). |

### Conceitos de domínio (essenciais)

- **Praia**: local geográfico (nome, cidade, UF). Todo usuário pode estar vinculado a **uma** praia; é a praia que "filtra" quem vê quem.
- **Produto Base**: item do **catálogo global**, gerido pelo admin (ex.: "Água de coco"), com **preço mínimo** e flag de ativo. É o "tipo" de produto.
- **Produto Específico**: **oferta de um vendedor** para um produto base (nome próprio, descrição, foto, preço ≥ preço mínimo do base, ativo/inativo).
- **Intenção de Compra**: registro de que um turista **quer** um *produto base* (não um produto específico), com descrição do local, observações e foto do local. Tem ciclo de vida (ativa → concluída com motivo).
- **Localização**: último par latitude/longitude conhecido de um usuário (1 por usuário, sobrescrito a cada atualização).
- **Vendedor online**: flag que o vendedor liga/desliga; só vendedores online aparecem nas buscas de localização.

> Observação de modelagem: a intenção de compra se liga ao **produto base**, e o casamento com o vendedor é feito pelo fato de o vendedor ter um **produto específico ativo daquele produto base**.

---

## 2. Stack e versões

| Item | Tecnologia / versão | Observação |
|---|---|---|
| Linguagem | **Java 17** (`java.version` no `pom.xml`) | Na máquina de desenvolvimento os testes rodaram com JDK 21; o alvo oficial é 17. |
| Framework | **Spring Boot 3.5.4** | Parent do `pom.xml`. |
| Web | `spring-boot-starter-web` (servlet/MVC, bloqueante) | Sem WebFlux, sem WebSocket/SSE. |
| Persistência | `spring-boot-starter-data-jpa` (Hibernate 6) | `ddl-auto=validate`, `open-in-view=false`. |
| Banco | **PostgreSQL 17** (driver `postgresql`) | Container `postgres:17` no `docker-compose.yml`. |
| Migrations | **Flyway** (`flyway-core` + `flyway-database-postgresql`) | Migrations ficam em `src/main/java/.../db/migration` (incomum — ver §5.5). |
| Segurança | `spring-boot-starter-security` + `@EnableMethodSecurity` | Stateless, filtro JWT próprio. |
| JWT | **jjwt 0.12.6** (`api`, `impl`, `jackson`) | HS256 (chave simétrica). |
| Validação | `spring-boot-starter-validation` (Jakarta Bean Validation) | Usada nos DTOs de entrada. |
| Documentação da API | **springdoc-openapi 2.8.5** (`springdoc-openapi-starter-webmvc-ui`) | Swagger UI. |
| Boilerplate | **Lombok** | `@Data`, `@Builder`, `@AllArgsConstructor`, `@RequiredArgsConstructor`. |
| Observabilidade | `spring-boot-starter-actuator` + `micrometer-registry-cloudwatch2` | Só `health` exposto; export CloudWatch é opcional (desligado por padrão). |
| Testes | JUnit 5, Mockito, AssertJ, `spring-security-test`, MockMvc | Via `spring-boot-starter-test`. |
| Build | **Maven Wrapper** (`mvnw`, `mvnw.cmd`) | Não exige Maven instalado. |
| Empacotamento | **Docker** multi-stage (`eclipse-temurin:17`) + `docker-compose.yml` | App + Postgres. |

Detalhes que costumam confundir:
- `groupId` do `pom.xml` é `br.gov.caixa` (resquício de template); o pacote Java real é **`br.com.toquerendo`**.
- `jackson-databind` está fixado em `2.17.1` no `pom.xml`.
- O `maven-surefire-plugin` usa `argLine` com `-javaagent` apontando para o jar do `mockito-core` 5.17.0 no repositório Maven local (necessário para o Mockito em JDKs recentes). Se o jar não estiver no `.m2`, os testes falham — rode `./mvnw` uma vez com internet para baixá-lo.

---

## 3. Estrutura do repositório e arquitetura

### 3.1 Raiz

```
to-querendo-api/
├── pom.xml
├── mvnw / mvnw.cmd / .mvn/        # Maven Wrapper
├── Dockerfile                     # build multi-stage + runtime JRE 17
├── docker-compose.yml             # postgres + app
├── .env.example                   # modelo de variáveis (o .env real é ignorado pelo git)
├── README.md                      # visão rápida + autenticação (PARCIALMENTE DESATUALIZADO — ver §14)
├── docs/                          # esta documentação
├── Claude outputs/                # artefatos auxiliares (ex.: planilha de endpoints); não faz parte da aplicação
└── src/
    ├── main/java/br/com/toquerendo/…
    ├── main/resources/application.properties
    └── test/java/br/com/toquerendo/…
```

### 3.2 Pacotes (`src/main/java/br/com/toquerendo`)

| Pacote | Responsabilidade |
|---|---|
| `ToQuerendoApplication` | Classe `main`. |
| `config/` | `SecurityConfig` (filter chain, CORS, BCrypt), `OpenApiConfig` (Swagger + esquema Bearer), `CloudWatchMetricsConfig` (bean condicional). |
| `controller/` | Endpoints REST. Finos: validam entrada (`@Valid`), aplicam anotação de role e delegam ao serviço. Também carregam toda a anotação Swagger. |
| `service/` | **Interfaces** de serviço (contratos). |
| `service/implementation/` | Implementações (`*ServiceImpl`) com as **regras de negócio**. |
| `repository/` | Interfaces Spring Data JPA (derived queries + `@Query` JPQL). |
| `entity/` | Entidades JPA (`@Entity`, Lombok `@Data`). |
| `dto/input/` | DTOs de entrada (classes Lombok `@Data`, com Bean Validation e `@Schema`). |
| `dto/output/` | DTOs de saída (`@Data @Builder`, `@JsonInclude(NON_NULL)` na maioria, fábrica estática `fromEntity`/`fromEntities`). |
| `dto/ApiResponse`, `dto/ApiUser` | Envelope padrão de resposta; usuário extraído do JWT. |
| `enums/` | `CategoriaUsuarioEnum` (categorias → roles). |
| `exception/` | Exceções de negócio e `GlobalExceptionHandler` (`@RestControllerAdvice`). |
| `security/` | `JwtService`, `JwtAuthenticationFilter`, `SecurityConstants` (rotas públicas), `security/annotation/{AdminOnly,VendedorOnly,TuristaOnly}`. |
| `utils/` | `SecurityUtils` (usuário logado / checagem de role), `DocumentoUtils` (validação de CPF). |
| `db/migration/` | Scripts SQL do Flyway (dentro de `src/main/java`!). |

### 3.3 Arquitetura em camadas

```
HTTP ──► JwtAuthenticationFilter ──► Controller ──► Service (interface) ──► ServiceImpl ──► Repository ──► PostgreSQL
              (autentica)             (@Valid,        (contrato)            (regras)        (JPA/JPQL)
                                      @XxxOnly)
                                          ▲
                       GlobalExceptionHandler traduz exceções → ApiResponse + status HTTP
```

Regras da arquitetura:
- **Controller → Service → Repository**, nunca pulando camadas. Controllers dependem da **interface** do serviço.
- **Entidades nunca são expostas** pela API: sempre há um DTO de saída.
- O **usuário logado** vem do `SecurityContext`: o `principal`/`name` é o **e-mail** (claim `sub` do JWT). Serviços usam `SecurityUtils.getEmailUsuarioLogado()`.
- Não há MapStruct: o mapeamento é manual, via métodos estáticos nos DTOs de saída.

---

## 4. Configuração, execução e build

### 4.1 Pré-requisitos
JDK 17 e Docker (para o Postgres). Maven não é necessário (usa `mvnw`).

### 4.2 Subir localmente

```bash
docker compose up -d postgres      # só o banco (porta 5432, bind em 127.0.0.1)
./mvnw spring-boot:run             # Windows: mvnw.cmd spring-boot:run
```

- App em `http://localhost:8080`.
- Swagger UI: `http://localhost:8080/swagger-ui/index.html` (também `/swagger-ui.html`); OpenAPI JSON em `/v3/api-docs`.
- O Flyway aplica as migrations automaticamente na subida.

Para subir **tudo** em containers (app + banco): copie `.env.example` para `.env`, preencha (em especial `JWT_SECRET`, obrigatório no compose) e rode `docker compose up -d --build`.

### 4.3 Comandos úteis

```bash
./mvnw test                                   # suíte completa
./mvnw -Dtest=NomeDaClasseTest test           # uma classe
./mvnw -Dtest=A,B,C test                      # várias classes
./mvnw compile                                # compila (JPQL só é validado em runtime!)
./mvnw package -DskipTests                    # gera target/toquerendo-0.0.1-SNAPSHOT.jar
```

### 4.4 Variáveis de ambiente / propriedades

Definidas em `src/main/resources/application.properties` (com defaults de desenvolvimento) e sobrescrevíveis por ambiente:

| Variável | Propriedade | Função | Default |
|---|---|---|---|
| `PORT` | `server.port` | Porta HTTP | `8080` |
| `SPRING_DATASOURCE_URL` | `spring.datasource.url` | JDBC | `jdbc:postgresql://localhost:5432/toQuerendo` |
| `SPRING_DATASOURCE_USERNAME` | idem `.username` | Usuário do banco | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | idem `.password` | Senha do banco | valor de desenvolvimento embutido |
| `JWT_SECRET` | `security.jwt.secret` | Segredo HS256 | valor de desenvolvimento embutido |
| `JWT_EXPIRATION_MS` | `security.jwt.expiration-ms` | Validade do token | `3600000` (1 h) |
| `CORS_ALLOWED_ORIGINS` | `cors.allowed-origins` | Origens CORS (separadas por vírgula; aceita padrões) | `*` |
| `CLOUDWATCH_METRICS_ENABLED` | `management.cloudwatch.metrics.export.enabled` | Liga export de métricas | `false` |
| `JAVA_TOOL_OPTIONS` | — | Tuning da JVM (heap pequeno p/ instâncias de 1 GB) | `-Xmx384m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC` |

Outras propriedades relevantes (fixas no arquivo):
- `spring.jpa.hibernate.ddl-auto=validate` — o Hibernate **não cria/altera** tabelas; só valida. Toda mudança de schema exige migration.
- `spring.jpa.open-in-view=false` — **sem sessão aberta na camada web**; acessar associação `LAZY` fora de transação lança `LazyInitializationException`.
- `spring.jpa.show-sql=true`, `hibernate.format_sql=true`, `logging.level.org.hibernate.SQL=DEBUG`, `logging.level.org.hibernate.orm.jdbc.bind=TRACE` — **log de SQL e parâmetros ligado** (útil em dev; ruidoso/sensível em produção).
- Flyway: `locations=classpath:br/com/toquerendo/db/migration`, `baseline-on-migrate=true`, `out-of-order=true`, `validateMigrationNaming=true`.
- Actuator: `management.endpoints.web.exposure.include=health`.
- Springdoc: `api-docs.path=/v3/api-docs`, `swagger-ui.path=/swagger-ui.html`, ordenação por método/alfabética.

> **Segredos:** nunca registrar valores reais de `JWT_SECRET`/senhas em código, docs ou commits. O `.env` está no `.gitignore`. Os defaults do `application.properties` são só para desenvolvimento e **devem** ser sobrescritos em produção.

---

## 5. Modelo de dados

### 5.1 Diagrama lógico

```
praias 1──* usuarios *──1 categorias
                │
                ├──1:1── localizacoes            (PK = id_usuario)
                ├──1:1── vendedores 1──* produtos_especificos *──1 produtos_base
                │             │                                        │
                │             ├──* avaliacoes (usuario avalia vendedor)│
                │             └──*:* pagamentos_aceitos (via pagamentos_vendedores)
                └──* intencoes_compra *──1 produtos_base
```

### 5.2 Tabelas (estado após a migration V9)

**`praias`** — `id` BIGSERIAL PK · `nome` NN · `url_foto` (500) · `cidade` NN · `estado` CHAR(2) NN

**`categorias`** — `id` · `descricao` NN. Seed: `1 turista`, `2 vendedor`, `3 administrador`.

**`usuarios`** — `id` · `email` NN UNIQUE · `nome` NN · `senha` NN (hash BCrypt) · `telefone` (20) · `cpf` (11, UNIQUE) · `url_foto` (500) · `id_praia` FK→praias **ON DELETE SET NULL** · `ts_criacao_conta` NN default now() · `conta_ativa` NN default false · `id_categoria` FK→categorias

**`localizacoes`** — `id_usuario` PK **e** FK→usuarios · `latitude` NUMERIC(10,7) NN · `longitude` NUMERIC(10,7) NN. (Relação 1:1 de chave compartilhada.)

**`vendedores`** — `id` · `id_usuario` NN UNIQUE FK→usuarios · `descricao` (TEXT) · `online` NN default false

**`produtos_base`** — `id` · `nome` NN · `descricao` · `url_foto` · `preco_minimo` NUMERIC(10,2) · `esta_ativo` NN default false

**`produtos_especificos`** — `id` · `nome` NN · `descricao` · `url_foto` · `id_produto_base` NN FK→produtos_base **ON DELETE CASCADE** · `id_vendedor` NN FK→vendedores · `preco` NUMERIC(10,2) NN · `produto_ativo` NN default false · `ts_criacao_produto` NN default now()

**`intencoes_compra`** — `id` · `id_usuario` NN FK→usuarios · `id_produto_base` NN FK→produtos_base **ON DELETE CASCADE** · `descricao_local` · `observacoes` · `url_foto_local` (500) · `ts_criacao_intencao` NN default now() · `ts_conclusao_intencao` (nulo até concluir) · `esta_ativo` NN default **true** · `motivo_conclusao` VARCHAR(200) (nulo até concluir)

**`avaliacoes`** — `id` · `id_vendedor` · `id_usuario` · `nota` SMALLINT NN · `descricao` · `ts_avaliacao`. *(Entidade e repositório existem; **sem endpoint/serviço**.)*

**`pagamentos_aceitos`** (catálogo) e **`pagamentos_vendedores`** (N:N, PK composta `id_pagamento`+`id_vendedor`). *(Idem: sem endpoint/serviço.)*

### 5.3 Entidades JPA — pontos de atenção

| Entidade | Notas |
|---|---|
| `Usuario` | `praia` e `categoria` são `ManyToOne` **EAGER**. Tem setters auxiliares `setCategoriaId(Integer)` e `setPraiaId(Long)` que criam uma *referência* (objeto só com id). `categoria.id` é `Integer`; `praia.id` é `Long`. |
| `Vendedor` | `OneToOne` EAGER para `Usuario`. Campo `online` (Boolean). |
| `ProdutoEspecifico` | `ManyToOne` **EAGER** para `ProdutoBase` e `Vendedor`. |
| `IntencaoCompra` | `ManyToOne` **LAZY** para `Usuario` e `ProdutoBase`. Por isso as queries usam `join fetch` (ver §13). |
| `Localizacao` | `@Id Long idUsuario` + `@OneToOne(LAZY) @MapsId Usuario usuario`. **Sem `@GeneratedValue`.** Ver armadilha em §13. |
| `Avaliacao`, `PagamentoVendedor` | `ManyToOne` LAZY; sem uso na API. |

Todas usam Lombok `@Data` (equals/hashCode sobre todos os campos — **não use entidades como chave de `Map`/`Set`**; agrupe por `id`).

### 5.4 Histórico de migrations (`src/main/java/br/com/toquerendo/db/migration`)

| Versão | Arquivo | O que faz |
|---|---|---|
| V1 | `V1__criacao_tabelas.sql` | Cria todas as tabelas iniciais. |
| V2 | `V2__add_senha_usuario.sql` | `usuarios.senha`. |
| V3 | `V3__add_ativo_produto_base.sql` | `produtos_base.esta_ativo`. |
| — | *(V4 não existe)* | Lacuna de numeração; tolerada (`out-of-order=true`). |
| V5 | `V5__seed_categorias.sql` | Insere as 3 categorias e acerta a sequence. |
| V6 | `V6__add_campos_intencao_compra.sql` | `ts_criacao_intencao`, `ts_conclusao_intencao`, `esta_ativo`. |
| V7 | `V7__seed_usuarios_admin.sql` | Cria contas de **administrador** da equipe (categoria 3, ativas), todas com um hash BCrypt de senha inicial compartilhada. |
| V8 | `V8__ajustar_on_delete_praia_e_produto_base.sql` | `usuarios.id_praia` → SET NULL; `produtos_especificos` e `intencoes_compra` → CASCADE ao apagar produto base. |
| V9 | `V9__add_motivo_conclusao_intencao_compra.sql` | `intencoes_compra.motivo_conclusao VARCHAR(200)`. |
| V10 | `V10__seed_vendedores_admin.sql` | Cria um registro em `vendedores` (`online=false`, sem descrição) para cada usuário administrador (categoria 3) que ainda não tem um; idempotente via `NOT EXISTS`. A categoria continua administrador. Efeito: admins passam a poder usar os endpoints de vendedor (`buscarVendedorLogado` encontra o registro). Os admins do seed não têm telefone/CPF — a migration ignora a regra de `POST /vendedor`, que exigiria os dois. |

### 5.5 Como criar uma migration (importante)

1. Crie `src/main/java/br/com/toquerendo/db/migration/V<próximo>__descricao_em_snake_case.sql` (próximo = maior versão + 1; hoje, **V11**).
2. **Nunca edite** uma migration já aplicada — crie uma nova.
3. O `pom.xml` copia `**/*.sql` de `src/main/java` para o classpath (bloco `<resources>`); é por isso que as migrations vivem ali e Flyway as encontra em `classpath:br/com/toquerendo/db/migration`.
4. Colunas novas em tabelas existentes: pense em `NOT NULL` + `DEFAULT` (ou nulo) para não quebrar linhas antigas.
5. Atualize a entidade correspondente (o `ddl-auto=validate` derruba a subida se entidade e banco divergirem).

---

## 6. Autenticação e autorização

### 6.1 Fluxo

1. `POST /usuario/cadastrar` cria um turista (público).
2. `POST /auth/login` (`email` + `senha`) → só autentica contas com `conta_ativa = true`; compara a senha com BCrypt; devolve `{token, tipo:"Bearer", expiraEmMs}`.
3. O cliente envia `Authorization: Bearer <token>` nas rotas protegidas.
4. `JwtAuthenticationFilter` valida a assinatura/expiração, lê `sub` (e-mail) e `roles` e popula o `SecurityContext` com `UsernamePasswordAuthenticationToken(email, null, authorities)`.

### 6.2 O token (JWT HS256)

- `sub` = e-mail do usuário.
- `roles` = lista de roles **gravada no token** (derivada da categoria no momento do login/upgrade).
- `iat`, `exp` (validade = `JWT_EXPIRATION_MS`).
- Como as roles ficam no token, **mudar a categoria do usuário só tem efeito num token novo**. Por isso `POST /vendedor` devolve um token novo (campo `token` do `UsuarioOutputDto`).
- Token inválido/expirado/sem `roles` com header `Bearer` presente → o filtro responde **401** com `{"messages":["Token inválido ou expirado"]}` (sem o campo `response`).
- Rotas públicas não passam pelo filtro (`shouldNotFilter`).

### 6.3 Categorias → roles (`CategoriaUsuarioEnum`)

| Categoria | Roles no token |
|---|---|
| TURISTA (1) | `ROLE_TURISTA` |
| VENDEDOR (2) | `ROLE_TURISTA`, `ROLE_VENDEDOR` |
| ADMINISTRADOR (3) | `ROLE_TURISTA`, `ROLE_VENDEDOR`, `ROLE_ADMIN` |

Consequências práticas:
- **`@TuristaOnly` = "qualquer humano logado"** (turista, vendedor e admin passam). Não use para restringir a turistas puros.
- **`@VendedorOnly`** admite vendedor **e admin**. Mas os serviços ainda exigem um registro em `vendedores` (`buscarVendedorLogado`); um admin sem registro de vendedor recebe 400 "Vendedor não encontrado."
- **`@AdminOnly`** só admin.
- "Categoria turista" (regra de negócio, ex.: listar banhistas) ≠ "tem `ROLE_TURISTA`". Filtra-se por `usuario.categoria.id = CategoriaUsuarioEnum.TURISTA.getId()`.

### 6.4 Anotações de segurança (`security/annotation`)

Meta-anotações de método sobre `@PreAuthorize`:
- `@AdminOnly` → `hasRole('ADMIN')`
- `@VendedorOnly` → `hasRole('VENDEDOR')`
- `@TuristaOnly` → `hasRole('TURISTA')`

Violação → `AuthorizationDeniedException` → **403** "Endpoint somente disponível para usuários autorizados." Endpoint **sem** anotação exige apenas estar autenticado.

### 6.5 Rotas públicas (`SecurityConstants.ROTAS_PUBLICAS`)

`/auth/login`, `/swagger-ui/**`, `/v3/api-docs/**`, `/usuario/cadastrar`. Todo o resto (`anyRequest().authenticated()`), **inclusive `/actuator/health`**.

### 6.6 Demais pontos de segurança
- CSRF desabilitado; sessão `STATELESS`.
- Senhas com `BCryptPasswordEncoder`; nunca armazenar/retornar em texto puro.
- CORS: origens vindas de `cors.allowed-origins` (padrões), métodos `GET/POST/PUT/PATCH/DELETE/OPTIONS`, headers `*`, `allowCredentials=true`.
- Requisição sem header `Authorization` em rota protegida: o Spring Security responde negando acesso (sem `AuthenticationEntryPoint` customizado no projeto, o código padrão tende a ser 403, embora o Swagger documente 401 — **não verificado em execução**).

---

## 7. Regras de negócio por módulo

### 7.1 Usuários (`UsuarioServiceImpl`)

**Cadastro** (`POST /usuario/cadastrar`):
- `email` único (senão `EmailJaCadastradoException`, 409); `cpf` único se informado (senão `CpfJaCadastradoException`, 400).
- Senha → BCrypt. `contaAtiva=true`, `tsCriacaoConta=now()`, categoria **sempre TURISTA**.
- `telefone`, `cpf` e `idPraia` são opcionais no cadastro (o README diz que são obrigatórios — está desatualizado).
- O serviço **não** valida os dígitos do CPF nem a existência da praia no cadastro (CPF só passa pela regex de 11 dígitos; praia inexistente cairia em erro de FK → 500).

**Edição** (`PUT /usuario`, qualquer autenticado, atualização **parcial**: campo nulo = não altera):
- `idPraia`: se informado, a praia precisa existir (senão 400 "Praia não encontrada.").
- `cpf`: se já pertence a **outro** usuário → 400; se inválido pelo algoritmo de `DocumentoUtils.isCpfValido` → 400 "CPF inválido."
- `nome`, `telefone`, `urlFoto` opcionais.
- Se o usuário **é vendedor** (registro em `vendedores` **e** role `ROLE_VENDEDOR`): também atualiza `online` e `descricao` do vendedor e os devolve na resposta. **É assim que o vendedor fica online/offline.**

**Consulta** (`GET /usuario/meus-dados`): dados do usuário logado; se for vendedor, inclui `online` e `descricao`.

Regras transversais: operações "do usuário logado" usam `findByEmailAndContaAtivaTrue` (conta inativa = "Usuário não encontrado").

### 7.2 Vendedores (`VendedorServiceImpl`)

**Virar vendedor** (`POST /vendedor`, qualquer autenticado):
- Já existe registro de vendedor para o e-mail → `VendedorJaCadastradoException` (409).
- Exige `telefone` **e** `cpf` preenchidos, `contaAtiva`, e categoria **TURISTA ou ADMINISTRADOR**; senão `CriacaoVendedorException` (400) com mensagem explicativa.
- Cria `Vendedor` com `online=false` e `descricao` opcional (máx. 500).
- Se **não** for admin, muda a categoria para VENDEDOR. Admin permanece admin.
- Retorna `UsuarioOutputDto` com `descricao`, `online` e um **token novo** (com as roles atualizadas).

**Consultar vendedores no mapa** (`GET /vendedor/localizacao?idPraia=…&idProdutoBase=…`, `@TuristaOnly`):
- `idPraia` obrigatório e deve existir (404 `PraiaNaoEncontradaException`).
- `idProdutoBase` **opcional**. Se informado, deve ser produto base **ativo** (404 senão) e a busca traz vendedores **online** da praia com **produto específico ativo** daquele base. Se omitido, traz vendedores online da praia com **algum** produto ativo cujo produto base esteja ativo.
- Para cada vendedor retorna `{idVendedor, nomeVendedor, latitude, longitude, produtos[]}`. **Vendedor sem localização registrada aparece com latitude/longitude `0`** (não é omitido).
- `produtos` = produtos específicos ativos (do base informado, ou todos ativos com base ativo).

### 7.3 Produto Base (catálogo) (`ProdutoBaseServiceImpl`)

- Criação/edição/remoção/listagem completa: **somente admin**. Listagem de ativos: qualquer autenticado.
- Criar: `nome`, `descricao`, `precoMinimo` obrigatórios; `estaAtivo=true` ao criar.
- Atualizar: parcial (campo nulo preserva). `estaAtivo` pode ser alternado (é a forma de "desativar" sem apagar).
- Remover: **delete físico**; o banco apaga em cascata os produtos específicos e as intenções de compra que referenciam o base (V8).
- `GET /produto-base?idPraia=` (parâmetro `Integer`): sem `idPraia` → todos os bases ativos; com `idPraia` → apenas bases ativos que tenham produto específico **ativo** de vendedor daquela praia (**sem** filtrar por vendedor online).
- A saída serializa `precoMinimo` como **String**.

### 7.4 Produto Específico (`ProdutoEspecificoServiceImpl`)

Tudo restrito a vendedor e **sempre sobre os produtos do vendedor logado**.
- **Criar:** produto base deve existir e estar **ativo** (404 senão); `preco >= precoMinimo` do base (400 senão); nasce `produtoAtivo=true`, `tsCriacaoProduto=now()`.
- **Atualizar** (parcial): produto precisa existir (404) e pertencer ao vendedor (403 `ProdutoNaoPertenceAoVendedorException`); se o preço for alterado, continua ≥ preço mínimo do base. Pode ligar/desligar `produtoAtivo`.
- **Listar** (`GET /produto-especifico/meus-produtos`): **todos** os do vendedor, ativos e inativos. A saída inclui `produtoBaseAtivo` para o front sinalizar produtos cujo base foi desativado.
- **Deletar:** delete físico, só do dono (403 caso contrário); devolve o produto removido.
- `tsCriacaoProduto` sai como String `dd/MM/yyyy HH:mm`.

### 7.5 Praia (`PraiaServiceImpl`)

- Leitura (lista e por id): qualquer autenticado. Escrita (criar/atualizar/remover): **admin**.
- `GET /praia/{id}` inclui `qtdVendedoresOnline` (vendedores `online=true` cujo usuário está na praia); a lista **não** inclui essa contagem.
- Criar: `nome`, `cidade`, `estado` (2 letras) obrigatórios. Atualizar: parcial.
- Remover: delete físico; usuários da praia ficam com `id_praia = NULL` (V8).

### 7.6 Localização (`LocalizacaoServiceImpl`)

- `PUT /localizacao` (`@TuristaOnly`, ou seja, qualquer logado): **upsert** da localização do usuário logado. Se não existe, cria (associando o `Usuario`); se existe, sobrescreve lat/lng.
- Validação: `latitude ∈ [-90, 90]`, `longitude ∈ [-180, 180]`, ambos obrigatórios.
- É um recurso por usuário (sem histórico). Vendedores e turistas usam o mesmo endpoint.
- **Modelo de atualização escolhido: polling REST** (cliente envia periodicamente; quem consulta também faz polling). WebSocket/SSE foram avaliados e **descartados por ora** (não há infra de tempo real no projeto; ver §14/§15).
- Método `@Transactional` por causa do `@MapsId` (ver §13).

### 7.7 Intenção de Compra (`IntencaoCompraServiceImpl`)

**Criar** (`POST /intencao-compra`, logado): produto base deve existir e estar **ativo** (404); `descricaoLocal` obrigatório; `observacoes` e `urlFotoLocal` opcionais. Nasce `estaAtivo=true`, `tsCriacaoIntencao=now()`. **Não há trava de duplicidade**: o mesmo usuário pode criar várias intenções ativas para o mesmo produto base.

**Minhas intenções** (`GET /intencao-compra/minhas-intencoes`): só as **ativas** do usuário logado, da mais nova para a mais antiga, com produto base carregado. (A descrição do Swagger diz "ativas ou não" — está incorreta; ver §14.)

**Banhistas para o vendedor** (`GET /intencao-compra/banhistas`, `@VendedorOnly`):
1. Resolve o vendedor logado e seus produtos específicos **ativos**; coleta os `produtoBase` **distintos**.
2. Se não há produto ativo **ou** o vendedor não tem praia → lista vazia (sem consultar intenções).
3. Busca intenções **ativas** de usuários de categoria **TURISTA** (id 1), **na praia do vendedor**, cujo produto base esteja entre os do vendedor.
4. Agrupa por usuário (chave = `usuario.id`, ordem de encontro preservada) → lista de `{idUsuario, nomeUsuario, intencoesCompra[]}`, onde cada banhista traz **apenas** as intenções compatíveis com o vendedor.

**Concluir** (`DELETE /intencao-compra`, corpo `{idProdutoBase, motivo}`, logado):
- Não apaga: **conclui** (soft). Para todas as intenções **ativas** do usuário logado naquele produto base: `estaAtivo=false`, `tsConclusaoIntencao=now()`, `motivoConclusao=motivo.trim()`.
- Sem nenhuma ativa → 404 `IntencaoCompraNaoEncontradaException`.
- Validação no DTO: `idProdutoBase` obrigatório; `motivo` não-branco e ≤ 200 caracteres.
- Resposta sem dados, só a mensagem de sucesso.
- O DTO de saída de intenção **não expõe** `estaAtivo`, `tsConclusaoIntencao` nem `motivoConclusao`.

---

## 8. Catálogo de endpoints

Legenda de acesso: **Público** · **Auth** (qualquer autenticado) · **Turista** (`@TuristaOnly`, na prática qualquer logado) · **Vendedor** (`@VendedorOnly`) · **Admin** (`@AdminOnly`).

| Método | Caminho | Acesso | Entrada | Saída (`response`) | Erros principais |
|---|---|---|---|---|---|
| POST | `/auth/login` | Público | body `LoginRequestDto` | `LoginResponseDto` | 400 validação, 401 credenciais |
| POST | `/usuario/cadastrar` | Público | body `CadastroUsuarioRequestDto` | `UsuarioOutputDto` | 400, 409 e-mail, 400 CPF duplicado |
| PUT | `/usuario` | Auth | body `AtualizarUsuarioRequestDto` | `UsuarioOutputDto` | 400 |
| GET | `/usuario/meus-dados` | Auth | — | `UsuarioOutputDto` | — |
| POST | `/vendedor` | Auth | body `CriarVendedorRequestDto` | `UsuarioOutputDto` (+`token`) | 400 (`CriacaoVendedorException`), 409 já vendedor |
| GET | `/vendedor/localizacao` | Turista | query `idPraia` (obrig.), `idProdutoBase` (opc.) | `List<VendedorLocalizacaoProdutosOutputDto>` | 400 param ausente, 404 praia/produto |
| GET | `/praia` | Auth | — | `List<PraiaOutputDto>` | — |
| GET | `/praia/{id}` | Auth | path | `PraiaOutputDto` (+`qtdVendedoresOnline`) | 404 |
| POST | `/praia` | Admin | body `CriarPraiaInputDto` | `PraiaOutputDto` | 400, 403 |
| PUT | `/praia/{id}` | Admin | body `AtualizarPraiaInputDto` | `PraiaOutputDto` | 400, 403, 404 |
| DELETE | `/praia/{id}` | Admin | path | só mensagem | 403, 404 |
| GET | `/produto-base` | Auth | query `idPraia` (opc., Integer) | `List<ProdutoBaseOutputDto>` | — |
| GET | `/produto-base/todos` | Admin | — | `List<ProdutoBaseOutputDto>` | 403 |
| POST | `/produto-base` | Admin | body `CriarProdutoBaseInputDto` | `ProdutoBaseOutputDto` | 400, 403 |
| PUT | `/produto-base/{id}` | Admin | body `AtualizarProdutoBaseInputDto` | `ProdutoBaseOutputDto` | 403, 404 |
| DELETE | `/produto-base/{id}` | Admin | path | só mensagem | 403, 404 |
| POST | `/produto-especifico` | Vendedor | body `CriarProdutoEspecificoInputDto` | `ProdutoEspecificoOutputDto` | 400 (preço < mínimo), 404 base |
| PUT | `/produto-especifico/{id}` | Vendedor | body `AtualizarProdutoEspecificoInputDto` | `ProdutoEspecificoOutputDto` | 400, 403 (não é dono), 404 |
| GET | `/produto-especifico/meus-produtos` | Vendedor | — | `List<ProdutoEspecificoOutputDto>` | — |
| DELETE | `/produto-especifico/{id}` | Vendedor | path | `ProdutoEspecificoOutputDto` (removido) | 403, 404 |
| PUT | `/localizacao` | Turista | body `AtualizarLocalizacaoInputDto` | `LocalizacaoOutputDto` | 400 faixa/obrigatório |
| POST | `/intencao-compra` | Turista | body `CriarIntencaoCompraInputDto` | `IntencaoCompraOutputDto` | 400, 404 produto base |
| GET | `/intencao-compra/minhas-intencoes` | Turista | — | `List<IntencaoCompraOutputDto>` | — |
| GET | `/intencao-compra/banhistas` | Vendedor | — | `List<BanhistaComIntencoesOutputDto>` | 403 |
| DELETE | `/intencao-compra` | Turista | **body** `ConcluirIntencaoCompraInputDto` | só mensagem | 400 validação, 404 sem intenção ativa |

Mensagens de sucesso são em português (ex.: "Intenção de compra concluída com sucesso!") e vêm em `messages[0]`.

---

## 9. Contrato de resposta e tratamento de erros

### 9.1 Envelope `ApiResponse<T>`

Toda resposta (sucesso ou erro) segue:

```json
{ "response": { /* T, omitido quando ausente */ }, "messages": ["texto 1", "texto 2"] }
```
`@JsonInclude(NON_ABSENT)`: `response` some quando não há dado. Sucesso: `messages[0]` é a mensagem de sucesso.

### 9.2 Mapeamento exceção → HTTP (`GlobalExceptionHandler`)

| Exceção | HTTP | Mensagem |
|---|---|---|
| `MethodArgumentNotValidException` (Bean Validation) | 400 | `messages[0]="Existem erros nos parâmetros enviados!"`, depois um item `campo: mensagem` por erro |
| `MissingServletRequestParameterException` | 400 | "Parâmetro obrigatório ausente: {nome}" |
| `RuntimeApiException` | 400 | mensagem livre — **exceção genérica de regra de negócio violada** |
| `CriacaoVendedorException` | 400 | mensagem livre |
| `CpfJaCadastradoException` | 400 | "Já existe um usuário cadastrado com esse cpf." |
| `CredenciaisInvalidasException` | 401 | "Email ou senha inválidos." |
| `ExpiredJwtException` | 401 | "Token expirado." |
| `UsuarioNaoAutorizadoException` | 403 | "Você só pode alterar a sua própria conta." |
| `ProdutoNaoPertenceAoVendedorException` | 403 | "Você só pode alterar produtos associados à sua própria conta de vendedor." |
| `AuthorizationDeniedException` (`@PreAuthorize`) | 403 | "Endpoint somente disponível para usuários autorizados." |
| `ProdutoNaoEncontradoException` | 404 | "Produto não encontrado." |
| `PraiaNaoEncontradaException` | 404 | "Praia não encontrada para os critérios informados." |
| `IntencaoCompraNaoEncontradaException` | 404 | "Intenção de compra ativa não encontrada para o produto base informado." |
| `NoResourceFoundException` | 404 | "Endpoint não encontrado: {path}" |
| `EmailJaCadastradoException` | 409 | "Já existe um usuário cadastrado com esse email." |
| `VendedorJaCadastradoException` | 409 | "Este usuário já está cadastrado como vendedor." |
| `InvalidDataAccessResourceUsageException`, `DateTimeParseException` | 500 | mensagens genéricas |
| `Exception` (catch-all) | 500 | "Erro interno do servidor." |

Atenção: `RuntimeException` crua (ex.: `new RuntimeException("Usuário não encontrado.")` em vários serviços) cai no catch-all e vira **500** — para erro esperado de negócio use uma exceção mapeada.

### 9.3 Como criar uma nova exceção de negócio
1. Classe em `exception/` estendendo `RuntimeException`, mensagem fixa em português no construtor sem argumentos (padrão `XxxNaoEncontradaException`).
2. Um `@ExceptionHandler` correspondente em `GlobalExceptionHandler`, retornando `ResponseEntity.status(...).body(new ApiResponse<>(e.getMessage()))`.
3. Para um erro 400 simples com texto próprio, basta lançar `RuntimeApiException("mensagem")`.

---

## 10. DTOs e validações

### 10.1 Entrada (`dto/input`)

| DTO | Campos (validação) |
|---|---|
| `LoginRequestDto` | `email` (`@NotBlank`), `senha` (`@NotBlank`) |
| `CadastroUsuarioRequestDto` | `email` (`@NotBlank @Email`), `nome` (`@NotBlank`), `senha` (`@NotBlank`, `@Size(min=6)`), `telefone`, `cpf` (`@Pattern \d{11}`), `idPraia` |
| `AtualizarUsuarioRequestDto` | `nome`, `telefone`, `cpf` (`@Pattern \d{11}`), `urlFoto`, `idPraia`, `descricao`*, `online`* (*só afetam vendedor*) |
| `CriarVendedorRequestDto` | `descricao` (`@Size(max=500)`) |
| `CriarPraiaInputDto` | `nome`, `cidade` (`@NotNull`), `estado` (`@NotNull @Size(2,2)`), `urlFoto` |
| `AtualizarPraiaInputDto` | todos opcionais; `estado` `@Size(2,2)` |
| `CriarProdutoBaseInputDto` | `nome`, `descricao`, `precoMinimo` (`@NotNull`), `urlFoto` |
| `AtualizarProdutoBaseInputDto` | todos opcionais (`nome`, `descricao`, `urlFoto`, `precoMinimo`, `estaAtivo`) |
| `CriarProdutoEspecificoInputDto` | `nome`, `idProdutoBase`, `preco` (`@NotNull`), `descricao`, `urlFoto` |
| `AtualizarProdutoEspecificoInputDto` | todos opcionais (`nome`, `descricao`, `urlFoto`, `preco`, `produtoAtivo`) |
| `AtualizarLocalizacaoInputDto` | `latitude` (`@NotNull`, −90…90), `longitude` (`@NotNull`, −180…180) |
| `CriarIntencaoCompraInputDto` | `idProdutoBase`, `descricaoLocal` (`@NotNull`), `observacoes`, `urlFotoLocal` |
| `ConcluirIntencaoCompraInputDto` | `idProdutoBase` (`@NotNull`), `motivo` (`@NotBlank`, `@Size(max=200)`) |

Padrão de **atualização**: DTOs "Atualizar*" não têm `@NotNull`; o serviço aplica `campo != null ? novo : atual`. Observação: nos controllers de atualização de praia/produto base/produto específico o `@RequestBody` **não** leva `@Valid` (a regra `@Size` de `estado` em `AtualizarPraiaInputDto`, por exemplo, não é aplicada).

### 10.2 Saída (`dto/output`)

| DTO | Campos |
|---|---|
| `LoginResponseDto` | `token`, `tipo`, `expiraEmMs` |
| `UsuarioOutputDto` | `email`, `nome`, `cpf`, `telefone`, `urlFoto` (nulos viram `""`), `praiaAtual` (`PraiaOutputDto`, sempre presente), `categoria` (`CategoriaOutputDto`, sempre presente), e — só quando aplicável — `token`, `descricao`, `online` |
| `PraiaOutputDto` | `id`, `nome`, `urlFoto`, `cidade`, `estado`, `qtdVendedoresOnline` (só em `GET /praia/{id}`) |
| `CategoriaOutputDto` | `id`, `descricao` |
| `ProdutoBaseOutputDto` | `id`, `nome`, `descricao`, `urlFoto`, `precoMinimo` (String), `estaAtivo` |
| `ProdutoEspecificoOutputDto` | `id`, `nome`, `descricao`, `urlFoto`, `idProdutoBase`, `nomeProdutoBase`, `idVendedor`, `preco`, `produtoAtivo`, `produtoBaseAtivo`, `tsCriacaoProduto` (String `dd/MM/yyyy HH:mm`) |
| `LocalizacaoOutputDto` | `idUsuario`, `latitude`, `longitude` |
| `VendedorLocalizacaoProdutosOutputDto` | `idVendedor`, `nomeVendedor`, `latitude`, `longitude`, `produtos[]` (`ProdutoEspecificoOutputDto`) |
| `IntencaoCompraOutputDto` | `id`, `idUsuario`, `idProdutoBase`, `nomeProdutoBase`, `descricaoLocal`, `observacoes` e `urlFotoLocal` (nulos → `""`), `tsCriacaoIntencao` (`LocalDateTime`) |
| `BanhistaComIntencoesOutputDto` | `idUsuario`, `nomeUsuario`, `intencoesCompra[]` |
| `VendedorOutputDto` | existe mas **não é usado** por nenhum endpoint atual |

---

## 11. Convenções de código

**Idioma:** código de domínio, mensagens, Swagger, commits e documentação em **português (pt-BR)**. Identificadores técnicos de framework em inglês. Sem acentos em nomes de classes/métodos; acentos permitidos em strings.

**Nomes:**
- Controllers `XxxController`; serviços `XxxService` (interface) e `XxxServiceImpl`; repositórios `XxxRepository`; DTOs `CriarXxxInputDto`, `AtualizarXxxInputDto`, `XxxOutputDto`, `XxxRequestDto/ResponseDto` (login/usuário).
- Métodos de serviço em verbo + objeto: `criarXxx`, `atualizarXxx`, `consultarXxx`, `deletarXxx`, `concluirXxx`.
- Tabelas/colunas em `snake_case` plural (`intencoes_compra`, `esta_ativo`, `ts_criacao_…`); booleanos `esta_ativo`/`produto_ativo`/`conta_ativa`; timestamps com prefixo `ts_`.
- Rotas em kebab-case no singular (`/produto-especifico`, `/intencao-compra`).

**Camadas:**
- Todo serviço novo: **interface em `service/` + `*ServiceImpl` em `service/implementation/`**, `@Service @AllArgsConstructor`, dependências `private final`.
- Controller: `@RestController @RequestMapping("/recurso") @AllArgsConstructor @Tag(...)`; cada método com `@Operation` e `@ApiResponses` (200/400/401/403/404 conforme aplicável), anotação de role quando restrito, `@RequestBody @Valid` quando há corpo; retorna `ResponseEntity.ok().body(new ApiResponse<>(dado, "Mensagem de sucesso!"))`.
- **Validação de entrada obrigatória vai no DTO com Bean Validation** (`@NotNull`, `@NotBlank`, `@Size`, …) + `@Valid` no controller — **não** em `if` dentro do serviço. Serviço valida apenas regra de negócio que depende de estado/banco.
- Atualização parcial: `campo != null ? novo : atual`.
- DTO de saída com `@Data @Builder` e método estático `fromEntity(...)`; se combinar duas entidades, `fromEntities(a, b)`.
- Usuário logado: `SecurityUtils.getEmailUsuarioLogado()`; helper privado `buscarXxxLogado()` por serviço (duplicado de propósito entre serviços — convenção atual).
- **Consultas:** derived queries para casos simples; `@Query` **JPQL com entidades e caminhos de propriedade** (`pe.vendedor.usuario.praia.id`) para o resto. **Não usar `nativeQuery`** (já causou bug — §13). Deduplicar com `select distinct`, não com `group by` de entidade.
- Associações `LAZY` lidas depois da query: `join fetch` na própria query (ou serviço `@Transactional`).
- Listas "vazias" são resposta 200 com `[]`; não lançar 404 para "nenhum resultado".
- Escrita que envolve entidade de chave compartilhada (`Localizacao`) ou múltiplas leituras+escrita: `@Transactional` no método do serviço.
- Comentários: só para o *porquê* não óbvio; sem docstrings longas.

**Swagger:** endpoints públicos usam `@SecurityRequirements` (sem argumento) para tirar o cadeado; o esquema global é `bearerAuth`.

---

## 12. Estratégia de testes

**Camadas testadas (padrão de 3 arquivos por feature)** em `src/test/java/br/com/toquerendo`:

| Arquivo | Técnica | O que valida |
|---|---|---|
| `controller/XxxControllerTest` | `@ExtendWith(MockitoExtension)` + `MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).setValidator(new LocalValidatorFactoryBean())`; serviço mockado | Status HTTP, JSON do envelope, Bean Validation (400), mapeamento de exceção (404/403/…). |
| `controller/XxxControllerSecurityTest` | `@WebMvcTest(controllers=…)` + `@AutoConfigureMockMvc(addFilters=false)` + `@Import` de `@TestConfiguration @EnableMethodSecurity` + `@MockBean` do serviço e do `JwtService` + `@WithMockUser(roles=…)` | Que `@PreAuthorize` libera/nega por role (200 vs 403). |
| `service/implementation/XxxServiceImplTest` | Mockito puro, `@InjectMocks`; `SecurityContextHolder` semeado em `@BeforeEach` com `UsernamePasswordAuthenticationToken(email, null, roles)` e limpo em `@AfterEach`; AssertJ; `ArgumentCaptor` | Regras de negócio e interações com repositórios. |

Regras e armadilhas:
- **Importante:** `@WithMockUser(roles = "VENDEDOR")` dá **somente** `ROLE_VENDEDOR`. Um vendedor real tem também `ROLE_TURISTA`. Para simular vendedor em endpoint `@TuristaOnly`, use `@WithMockUser(roles = {"TURISTA","VENDEDOR"})`.
- Em testes de segurança, o corpo da requisição precisa ser **válido** — a validação (`@Valid`) roda antes do `@PreAuthorize` e devolveria 400 em vez de 403/200.
- Convenção de nome: `metodo_cenario_resultadoEsperado` em português.
- Controllers recebem a **interface** do serviço; os testes de controller mockam `XxxServiceImpl` (compatível com a injeção por construtor).
- **Lacuna:** não há testes de repositório (`@DataJpaTest`), nem H2/Testcontainers, nem testes de integração. Portanto **JPQL, `join fetch`, `@MapsId` e cascades só são exercitados em execução real** — testes verdes não provam que a query funciona. Antes de entregar mudança em query/entidade, suba a aplicação contra o Postgres.
- Rodar: `./mvnw test` (a última execução completa registrada: 130 testes, 0 falhas).

---

## 13. Armadilhas já encontradas (lições aprendidas)

Cada item abaixo gerou um bug real neste projeto.

1. **`@MapsId` + `Localizacao`** — Setar `idUsuario` na mão numa `Localizacao` nova faz o Spring Data achar que ela já existe (id não-nulo → `merge`) e o UPDATE afeta 0 linhas → `ObjectOptimisticLockingFailureException`. **Correto:** deixar o id nulo e setar a associação (`nova.setUsuario(usuario)`); o id vem do `@MapsId` no `persist`.
2. **`detached entity passed to persist: Usuario`** — Sem transação envolvendo o método, o `Usuario` buscado numa chamada de repositório fica *detached* quando usado no `save` seguinte. **Correto:** `@Transactional` no método de serviço (já aplicado em `atualizarLocalizacao`).
3. **Native query com sintaxe de JPQL** — `@Query(nativeQuery=true)` com `select V from vendedores V … group by (V)` devolve uma coluna composta e o Hibernate falha com "Unable to find column position by name: id". **Correto:** JPQL puro sobre entidades (`select distinct pe.vendedor from ProdutoEspecifico pe where …`).
4. **JPQL escrito como SQL sem `nativeQuery`** — usar nomes de tabela/coluna (`produtos_base`, `id_praia`) em `@Query` não funciona; use nomes de **entidade** e de **propriedade**.
5. **`open-in-view=false` + associações LAZY** — acessar `intencao.getUsuario()`/`getProdutoBase()` depois do retorno do repositório estoura `LazyInitializationException`. **Correto:** `join fetch` na query (e `@Transactional(readOnly = true)` como segurança).
6. **Erros de JPQL só aparecem em runtime** — `./mvnw compile` e testes mockados passam mesmo com query inválida. Suba a app.
7. **Roles no token** — mudar de categoria exige token novo (daí o token no retorno de `POST /vendedor`).
8. **Parâmetro `@RequestParam` ausente** — antes de existir o handler dedicado virava 500; hoje vira 400.
9. **Chave de `Map` por entidade Lombok `@Data`** — `equals/hashCode` de todos os campos; agrupe por `id`.
10. **`DELETE` com corpo** — alguns clientes/proxies descartam o corpo; o projeto optou por usar corpo mesmo assim em `DELETE /intencao-compra` (decisão do dono do produto). Confirme com o front/cliente HTTP utilizado.

---

## 14. Inconsistências e débitos técnicos conhecidos

Itens abaixo são fatos verificados no código; **não** são necessariamente bugs a corrigir sem alinhamento.

**Documentação vs. código**
- `README.md` está parcialmente desatualizado: cita `POST /usuario` como cadastro público (o real é `POST /usuario/cadastrar`); diz que telefone/CPF são obrigatórios no cadastro (são opcionais); cita só as categorias turista/vendedor (existe administrador); diz "Spring Data JPA, Validation, Security" sem mencionar Actuator/CloudWatch.
- Swagger de `GET /intencao-compra/minhas-intencoes` diz "ativas ou não", mas a query retorna só **ativas**.
- Swagger de `POST /usuario/cadastrar` documenta **409** para "e-mail ou CPF já cadastrado", mas CPF duplicado retorna **400**.
- Swagger do `DELETE /produto-especifico/{id}` documenta só 200/401 (existem também 403/404).
- `ProdutoBaseController` descreve a remoção como "remove (ou inativa)"; na prática é **delete físico**.
- Vários endpoints documentam **401** para falta de token, mas o comportamento real sem header não foi verificado (ver §6.6).

**Segurança / operação**
- Defaults de desenvolvimento para `JWT_SECRET` e senha do banco estão em `application.properties` (versionado). Em produção precisam ser sobrescritos.
- `V7` insere contas admin com uma **senha inicial compartilhada** (hash no repositório) — trocar após o primeiro acesso / antes de produção.
- `CORS_ALLOWED_ORIGINS=*` com `allowCredentials=true` (liberado de propósito "por enquanto", segundo o `.env.example`).
- Log de SQL e **parâmetros bindados** ligado por padrão (`TRACE`) — pode vazar dados em produção.
- `/actuator/health` **não** é rota pública (exige token), o que pode atrapalhar health checks de load balancer/Docker.
- Sem rate limiting, sem refresh token, sem revogação de token, sem logout.
- `JwtService` usa `secret.getBytes()` sem charset explícito.

**Regras de negócio / dados**
- Sem trava de duplicidade de intenção de compra ativa (mesmo usuário + produto base).
- Cadastro de usuário não valida dígitos do CPF nem existência de `idPraia` (só a edição valida).
- Deletes físicos: produto específico, produto base (com cascade para produtos específicos e intenções), praia.
- Produto específico atualizado não reverifica se o produto base continua ativo.
- Um vendedor "online" sem localização aparece no mapa em lat/lng `0,0`.
- Timestamps usam `LocalDateTime.now()` (fuso do servidor), sem tratamento de timezone.
- `GET /produto-base?idPraia=` não filtra por vendedor online; `idPraia` é `Integer` enquanto o resto usa `Long`.
- Exceções `RuntimeException` cruas ("Usuário não encontrado.") viram 500.

**Código morto / resquícios de template**
- `SimulacaoService`/`SimulacaoServiceImpl`, `TesteDto`, `NenhumaSimulacaoEncontradaException`, `TelemetriaNaoEncontradaException`, `EventSenderNaoInicializado`, `EnviarEventSenderException`, `ConversaoJsonException` (e seus handlers) não pertencem ao domínio toQuerendo.
- `VendedorOutputDto` sem uso. Entidades/repositórios `Avaliacao`, `PagamentoAceito`, `PagamentoVendedor` sem serviço/endpoint.
- `groupId` `br.gov.caixa` e `description` "Demo project for Spring Boot" no `pom.xml`.
- Migrations dentro de `src/main/java`.

**Testes**
- Sem testes de repositório/integração (ver §12); sem testes para `AuthController`, `VendedorController`, `ProdutoBaseController`, `JwtService`/filtro.

---

## 15. Funcionalidades previstas mas não expostas

- **Avaliações** de vendedores (tabela, entidade e repositório prontos).
- **Formas de pagamento aceitas** por vendedor (catálogo + N:N prontos).
- **Tempo real** para localização (WebSocket/SSE) — discutido e adiado; a decisão vigente é polling REST. Se retomado, exige nova dependência (`spring-boot-starter-websocket`/SSE com `SseEmitter`), tratamento de autenticação no handshake e estratégia de broadcast.
- Upload/armazenamento de imagens (existe branch `feat/salvarImagens`; hoje só se guardam URLs).
- Distância comprador↔vendedor (haversine), notificações push, histórico de localização — não existem.

---

## 16. Fluxos de ponta a ponta

**A) Turista quer comprar**
1. `POST /usuario/cadastrar` → `POST /auth/login` → (opcional) `PUT /usuario` com `idPraia`, CPF, telefone.
2. `GET /praia`, `GET /produto-base?idPraia=` para descobrir o que existe na praia.
3. `PUT /localizacao` periodicamente (envia onde está).
4. `POST /intencao-compra` `{idProdutoBase, descricaoLocal, observacoes?, urlFotoLocal?}`.
5. `GET /vendedor/localizacao?idPraia=&idProdutoBase=` (polling) para ver vendedores online e seus produtos.
6. `GET /intencao-compra/minhas-intencoes` para acompanhar.
7. `DELETE /intencao-compra` `{idProdutoBase, motivo}` ao desistir/comprar → intenção concluída.

**B) Vendedor atende**
1. Turista cadastrado completa telefone+CPF (`PUT /usuario`) → `POST /vendedor` → **troca o token** pelo retornado.
2. Admin precisa ter cadastrado produtos base (`POST /produto-base`).
3. `POST /produto-especifico` para cada oferta (preço ≥ mínimo do base).
4. `PUT /usuario` com `online=true` (e `idPraia`) para aparecer no mapa; `PUT /localizacao` periodicamente.
5. `GET /intencao-compra/banhistas` → lista de banhistas da sua praia com intenções ativas compatíveis com seus produtos ativos → vai até eles.

**C) Admin**
Gerencia `/praia` e `/produto-base` (incluindo `GET /produto-base/todos` e ativar/desativar via `PUT`). Admin também pode virar vendedor (`POST /vendedor`) sem perder a categoria.

---

## 17. Deploy e operação

- **Imagem:** `Dockerfile` multi-stage — estágio `eclipse-temurin:17-jdk` roda `./mvnw package -DskipTests`; estágio final `eclipse-temurin:17-jre` executa `java -jar app.jar` (porta 8080).
- **Compose:** serviços `postgres` (17, volume `postgres_data`, porta só em `127.0.0.1`) e `app` (build local, `depends_on` postgres, rede `toquerendo-net`, `restart: unless-stopped`). `JWT_SECRET` é obrigatório no compose.
- **Alvo de infraestrutura:** o `.env.example` indica instância pequena (EC2 `t3.micro`, 1 GB) com heap limitado via `JAVA_TOOL_OPTIONS`; o histórico do git também registra tentativa de deploy no Railway (branch `railway/fix-deploy-…`).
- **Observabilidade:** Actuator expõe apenas `health`. Métricas Micrometer podem ser exportadas ao **AWS CloudWatch** (namespace `ToQuerendo/Api`, passo de 1 min) ligando `CLOUDWATCH_METRICS_ENABLED=true`; o bean `CloudWatchAsyncClient` só existe quando a propriedade está ativa e usa a cadeia padrão de credenciais da AWS.
- **Banco em produção:** o Flyway roda na subida da aplicação; `ddl-auto=validate` impede alterações de schema fora das migrations.
- **Checklist antes de produção:** trocar `JWT_SECRET` e senhas; trocar senha dos admins do seed; restringir CORS; desligar `show-sql`/`TRACE` de bind; liberar/rotear health check; revisar volume e backup do Postgres.

---

## 18. Fluxo de trabalho com Git

- Repositório: `github.com/nickciuffi/to-querendo-api`. Branch padrão de integração: **`start`** (`origin/HEAD`); existem também `develop` e `master`.
- Trabalho por **branches de feature** (`feat/…`, `fix/…`, `feature/…`) e **Pull Requests** mesclados em `start` (histórico com muitos "Merge pull request #N").
- Commits em português, geralmente com prefixo convencional (`feat:`, `fix:`).
- Equipe pequena (3 admins semeados em V7 + colaboradores no git).
- Não commitar `.env`, segredos ou `target/`.

---

## 19. Glossário

| Termo | Significado |
|---|---|
| Banhista | Usuário de categoria turista que está na praia e pode ter intenções de compra. |
| Intenção de compra | Declaração de que o turista quer um *produto base* num local; ativa até ser concluída. |
| Conclusão | Encerramento lógico da intenção (`esta_ativo=false`, data e motivo). |
| Produto base | Item do catálogo global (admin). |
| Produto específico | Oferta de um vendedor para um produto base. |
| Online | Flag do vendedor indicando que está trabalhando agora. |
| Upsert | "Cria se não existe, atualiza se existe" — usado em `PUT /localizacao`. |
| `@MapsId` | Mapeamento JPA em que a PK da entidade filha é a FK/PK da pai (usado em `Localizacao`). |
| ROLE_* | Autoridade Spring Security; vem do claim `roles` do JWT. |
