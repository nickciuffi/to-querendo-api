ALTER TABLE intencoes_compra
    ADD COLUMN ts_criacao_intencao TIMESTAMP NOT NULL DEFAULT now(),
    ADD COLUMN ts_conclusao_intencao TIMESTAMP,
    ADD COLUMN esta_ativo BOOLEAN NOT NULL DEFAULT TRUE;
