-- Cria o registro de vendedor para cada usuário administrador (categoria 3) que ainda não possui um.
-- A categoria do usuário permanece administrador; o vendedor nasce offline e sem descrição.
INSERT INTO vendedores (id_usuario, online)
SELECT u.id, FALSE
FROM usuarios u
WHERE u.id_categoria = 3
  AND NOT EXISTS (
      SELECT 1
      FROM vendedores v
      WHERE v.id_usuario = u.id
  );
