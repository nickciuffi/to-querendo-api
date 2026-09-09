-- Praia
CREATE TABLE praias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    url_foto VARCHAR(500),
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(2) NOT NULL
);

-- Categoria
CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL
);

-- Usuario
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    cpf VARCHAR(11) UNIQUE,
    url_foto VARCHAR(500),
    id_praia BIGINT REFERENCES praias (id),
    ts_criacao_conta TIMESTAMP NOT NULL DEFAULT now(),
    conta_ativa BOOLEAN NOT NULL DEFAULT FALSE,
    id_categoria BIGINT REFERENCES categorias (id)
);

-- Localizacao (1-1 com usuario)
CREATE TABLE localizacoes (
    id_usuario BIGINT PRIMARY KEY REFERENCES usuarios (id),
    latitude NUMERIC(10, 7) NOT NULL,
    longitude NUMERIC(10, 7) NOT NULL
);

-- Vendedor (1-1 com usuario)
CREATE TABLE vendedores (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL UNIQUE REFERENCES usuarios (id),
    descricao TEXT,
    online BOOLEAN NOT NULL DEFAULT FALSE
);

-- Avaliacoes (usuario avalia vendedor)
CREATE TABLE avaliacoes (
    id BIGSERIAL PRIMARY KEY,
    id_vendedor BIGINT NOT NULL REFERENCES vendedores (id),
    id_usuario BIGINT NOT NULL REFERENCES usuarios (id),
    nota SMALLINT NOT NULL,
    descricao TEXT,
    ts_avaliacao TIMESTAMP NOT NULL DEFAULT now()
);

-- Pagamentos aceitos (catalogo)
CREATE TABLE pagamentos_aceitos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT
);

-- Pagamentos aceitos por vendedor (N-N)
CREATE TABLE pagamentos_vendedores (
    id_pagamento BIGINT NOT NULL REFERENCES pagamentos_aceitos (id),
    id_vendedor BIGINT NOT NULL REFERENCES vendedores (id),
    PRIMARY KEY (id_pagamento, id_vendedor)
);

-- Produto base (catalogo)
CREATE TABLE produtos_base (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    url_foto VARCHAR(500),
    preco_minimo NUMERIC(10, 2)
);

-- Produto especifico (oferta de um vendedor para um produto base)
CREATE TABLE produtos_especificos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    url_foto VARCHAR(500),
    id_produto_base BIGINT NOT NULL REFERENCES produtos_base (id),
    id_vendedor BIGINT NOT NULL REFERENCES vendedores (id),
    preco NUMERIC(10, 2) NOT NULL,
    produto_ativo BOOLEAN NOT NULL DEFAULT FALSE,
    ts_criacao_produto TIMESTAMP NOT NULL DEFAULT now()
);

-- Intencao de compra (usuario procurando um produto base)
CREATE TABLE intencoes_compra (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL REFERENCES usuarios (id),
    id_produto_base BIGINT NOT NULL REFERENCES produtos_base (id),
    descricao_local TEXT,
    observacoes TEXT,
    url_foto_local VARCHAR(500)
);
