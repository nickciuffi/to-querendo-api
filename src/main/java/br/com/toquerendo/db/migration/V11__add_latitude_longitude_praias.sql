-- Mesmo tipo/precisão das colunas latitude e longitude da tabela localizacoes.
-- Anuláveis porque as praias já cadastradas ainda não possuem coordenadas.
ALTER TABLE praias
    ADD COLUMN latitude NUMERIC(10, 7),
    ADD COLUMN longitude NUMERIC(10, 7);
