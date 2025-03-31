CREATE SEQUENCE IF NOT EXISTS tokens_id_seq;

CREATE TABLE IF NOT EXISTS tokens
(
    id bigint NOT NULL DEFAULT nextval('tokens_id_seq'),
    access_token_expires_at timestamp without time zone,
    access_token_issued_at timestamp without time zone,
    access_token_value character varying(2000),
    refresh_token_issued_at timestamp without time zone,
    refresh_token_value character varying(255),
    CONSTRAINT tokens_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS token_scopes
(
    token_id bigint NOT NULL,
    access_token_scopes character varying(255),
    CONSTRAINT token_fk FOREIGN KEY (token_id)
        REFERENCES tokens (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS users
(
    id bigint NOT NULL,
    created_at timestamp without time zone,
    name character varying(255),
    token_id bigint,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT token_fk_users FOREIGN KEY (token_id)
        REFERENCES tokens (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);