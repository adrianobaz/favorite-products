CREATE SEQUENCE IF NOT EXISTS client_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS client_product(
    id BIGINT PRIMARY KEY DEFAULT nextval('client_product_id_seq'),
    client_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_clients
            FOREIGN KEY (client_id)
            REFERENCES clients(id),
    CONSTRAINT fk_products
                FOREIGN KEY (product_id)
                REFERENCES products(id)
);

ALTER SEQUENCE client_product_id_seq OWNED BY client_product.id;