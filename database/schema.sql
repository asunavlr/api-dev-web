-- =============================================================================
-- Pet Shop API - Script DDL (PostgreSQL)
-- =============================================================================
-- Cria o banco e as tabelas necessarias. A aplicacao tambem cria/atualiza
-- automaticamente via Hibernate (spring.jpa.hibernate.ddl-auto=update), entao
-- este arquivo serve como referencia / setup manual.
-- =============================================================================

-- CREATE DATABASE petshop;
-- \c petshop

CREATE TABLE IF NOT EXISTS categorias (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(80)  NOT NULL UNIQUE,
    descricao   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS produtos (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    descricao     VARCHAR(500),
    preco         NUMERIC(10,2) NOT NULL CHECK (preco > 0),
    estoque       INTEGER       NOT NULL DEFAULT 0 CHECK (estoque >= 0),
    ativo         BOOLEAN       NOT NULL DEFAULT TRUE,
    categoria_id  BIGINT        NOT NULL REFERENCES categorias(id)
);

CREATE INDEX IF NOT EXISTS idx_produtos_categoria ON produtos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_produtos_nome      ON produtos(LOWER(nome));

CREATE TABLE IF NOT EXISTS clientes (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(120) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    cpf        VARCHAR(14)  NOT NULL UNIQUE,
    telefone   VARCHAR(20),
    endereco   VARCHAR(255),
    criado_em  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pedidos (
    id           BIGSERIAL PRIMARY KEY,
    cliente_id   BIGINT       NOT NULL REFERENCES clientes(id),
    data_pedido  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status       VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE',
    total        NUMERIC(10,2) NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_pedidos_cliente ON pedidos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_pedidos_status  ON pedidos(status);

CREATE TABLE IF NOT EXISTS itens_pedido (
    id              BIGSERIAL PRIMARY KEY,
    pedido_id       BIGINT       NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    produto_id      BIGINT       NOT NULL REFERENCES produtos(id),
    quantidade      INTEGER      NOT NULL CHECK (quantidade > 0),
    preco_unitario  NUMERIC(10,2) NOT NULL,
    subtotal        NUMERIC(10,2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_itens_pedido  ON itens_pedido(pedido_id);
CREATE INDEX IF NOT EXISTS idx_itens_produto ON itens_pedido(produto_id);

-- =============================================================================
-- DADOS DE EXEMPLO (opcional)
-- =============================================================================
INSERT INTO categorias (nome, descricao) VALUES
    ('Racao',      'Alimentacao seca para caes e gatos'),
    ('Brinquedos', 'Brinquedos diversos para pets'),
    ('Higiene',    'Shampoos, escovas e produtos de higiene'),
    ('Acessorios', 'Coleiras, guias, caminhas e similares')
ON CONFLICT (nome) DO NOTHING;

INSERT INTO produtos (nome, descricao, preco, estoque, ativo, categoria_id) VALUES
    ('Racao Premium Caes Adultos 15kg', 'Racao super premium', 189.90, 30, TRUE, 1),
    ('Racao Gatos Castrados 10kg',      'Racao para felinos',  149.50, 20, TRUE, 1),
    ('Mordedor de Borracha',            'Brinquedo resistente', 24.90, 50, TRUE, 2),
    ('Bolinha com Guizo',               'Brinquedo para gatos',  12.00, 100, TRUE, 2),
    ('Shampoo Antialergico 500ml',      'Para peles sensiveis',  39.90, 40, TRUE, 3),
    ('Coleira Antipulgas',              'Protecao por 8 meses',  79.00, 25, TRUE, 4)
ON CONFLICT DO NOTHING;

INSERT INTO clientes (nome, email, cpf, telefone, endereco) VALUES
    ('Maria Silva', 'maria@example.com', '12345678901', '11999990001', 'Rua A, 100 - Sao Paulo/SP'),
    ('Joao Santos', 'joao@example.com',  '98765432100', '11999990002', 'Rua B, 200 - Sao Paulo/SP')
ON CONFLICT DO NOTHING;
-- END
