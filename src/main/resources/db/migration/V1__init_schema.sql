CREATE SEQUENCE IF NOT EXISTS clients_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS clients (
    id           BIGINT         PRIMARY KEY DEFAULT nextval('clients_id_seq'),
    external_id  UUID           DEFAULT gen_random_uuid() UNIQUE NOT NULL,
    name         TEXT           NOT NULL,
    email        VARCHAR        UNIQUE   NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL,
    updated_at   TIMESTAMPTZ    NOT NULL
);

ALTER SEQUENCE clients_id_seq OWNED BY clients.id;

CREATE INDEX idx_clients_external_id_hash ON clients USING HASH (external_id);

CREATE TABLE IF NOT EXISTS products (
    id           BIGINT         PRIMARY KEY,
    title        TEXT           NOT NULL,
    image        TEXT           NOT NULL,
    price        NUMERIC        NOT NULL,
    rate         NUMERIC,
    count        INTEGER,
    created_at   TIMESTAMPTZ    NOT NULL,
    updated_at   TIMESTAMPTZ    NOT NULL
);