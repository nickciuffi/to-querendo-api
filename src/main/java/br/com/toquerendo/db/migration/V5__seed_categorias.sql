INSERT INTO categorias (id, descricao) VALUES
    (1, 'turista'),
    (2, 'vendedor');

SELECT setval('categorias_id_seq', (SELECT MAX(id) FROM categorias));
