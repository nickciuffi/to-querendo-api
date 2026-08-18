## Simulação de Crédito - Caixa
API para geração de simulações de empréstimos para a população brasileira.

## Funcionalidades
- Realiza simulações de empréstimo com amortização SAC e PRICE.
- Consulta produtos de crédito em um SQL Server remoto antes de calcular as parcelas.
- Armazena simulações e parcelas em um MySQL local, garantindo segurança e confiabilidade.
- Envia as simulações para o Microsoft EventHub
- Permite consultar simulações com paginação e filtros por produto e data.
- Coleta telemetria em todas as chamadas de endpoints e armazena no banco local.
- Disponibiliza um endpoint para consultar todas as informações de telemetria.

## Tecnologias
- Java 17
- Spring Boot 3
- Maven (Gerenciamento de dependencias)
- Mysql (Banco de dados local)
- SQL Server (Banco de dados remoto)
- H2 (Banco de dados para testes de integração)
- Hibernate/JPA (ORM)
- Swagger (Documentação da API) - Link para a documentação, com a aplicação rodando: http://localhost:8080/swagger-ui/index.html
- Microsoft Eventhub
- Lombok
- Docker Compose (Execução da aplicação e do banco de dados local)

## Como rodar o projeto
- Tenha o docker-compose e o docker instalado na sua máquina
- Execute o comando "docker-compose up" na pasta raiz da aplicação

## Dica extra
- É possível obter a collection do postman, com a aplicação rodando, no link: http://localhost:8080/collection
