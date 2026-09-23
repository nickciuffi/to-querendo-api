-- Ao deletar uma praia, os usuários vinculados a ela ficam sem praia (id_praia = NULL)
ALTER TABLE usuarios
    DROP CONSTRAINT usuarios_id_praia_fkey,
    ADD CONSTRAINT usuarios_id_praia_fkey
        FOREIGN KEY (id_praia) REFERENCES praias (id) ON DELETE SET NULL;

-- Ao deletar um produto base, os produtos específicos que o referenciam são apagados junto
ALTER TABLE produtos_especificos
    DROP CONSTRAINT produtos_especificos_id_produto_base_fkey,
    ADD CONSTRAINT produtos_especificos_id_produto_base_fkey
        FOREIGN KEY (id_produto_base) REFERENCES produtos_base (id) ON DELETE CASCADE;

-- Ao deletar um produto base, as intenções de compra registradas pra ele também são apagadas
ALTER TABLE intencoes_compra
    DROP CONSTRAINT intencoes_compra_id_produto_base_fkey,
    ADD CONSTRAINT intencoes_compra_id_produto_base_fkey
        FOREIGN KEY (id_produto_base) REFERENCES produtos_base (id) ON DELETE CASCADE;
