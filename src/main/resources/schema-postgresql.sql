CREATE TABLE IF NOT EXISTS cliente(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    data_nascimento DATE,
    telefone VARCHAR(20),
    newsletter BOOLEAN DEFAULT false,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS produto(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    tipo VARCHAR(50),
    uva VARCHAR(100),
    pais VARCHAR(50),
    regiao VARCHAR(100),
    preco DECIMAL(10, 2) NOT NULL CHECK (preco > 0),
    estoque INT DEFAULT 0 CHECK (estoque >= 0),
    imagem VARCHAR(255),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS pedido(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL REFERENCES cliente(id),
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDENTE',
    total DECIMAL(10, 2) NOT NULL CHECK (total > 0)
);

CREATE TABLE IF NOT EXISTS item_pedido(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pedido_id UUID NOT NULL REFERENCES pedido(id),
    produto_id UUID NOT NULL REFERENCES produto(id),
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco_unitario DECIMAL(10, 2) NOT NULL CHECK (preco_unitario > 0)
);

CREATE TABLE IF NOT EXISTS cliente_role(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL REFERENCES cliente(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_CLIENTE',
    UNIQUE(cliente_id, role)
);

CREATE TABLE IF NOT EXISTS password_reset_token(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL REFERENCES cliente(id),
    token VARCHAR(255) NOT NULL UNIQUE,
    expiration TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT false
);

CREATE INDEX IF NOT EXISTS idx_pedido_cliente_id ON pedido(cliente_id);
CREATE INDEX IF NOT EXISTS idx_pedido_status ON pedido(status);
CREATE INDEX IF NOT EXISTS idx_item_pedido_pedido_id ON item_pedido(pedido_id);
CREATE INDEX IF NOT EXISTS idx_item_pedido_produto_id ON item_pedido(produto_id);
CREATE INDEX IF NOT EXISTS idx_cliente_role_cliente_id ON cliente_role(cliente_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_token_cliente_id ON password_reset_token(cliente_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_token_token ON password_reset_token(token);

CREATE TABLE IF NOT EXISTS contato(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    assunto VARCHAR(200) NOT NULL,
    mensagem TEXT NOT NULL,
    data_contato TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE contato ADD COLUMN IF NOT EXISTS telefone VARCHAR(20);
ALTER TABLE contato ADD COLUMN IF NOT EXISTS newsletter BOOLEAN DEFAULT false;
