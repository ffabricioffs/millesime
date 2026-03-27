-- Habilita a extensão necessária para gerar UUIDs aleatórios no PostgreSQL.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS cliente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    data_nascimento DATE,
    telefone VARCHAR(15),
    newsletter BOOLEAN DEFAULT false,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT true
);


CREATE INDEX IF NOT EXISTS idx_cliente_email ON cliente(email);


CREATE INDEX IF NOT EXISTS idx_cliente_cpf ON cliente(cpf);