CREATE TABLE main.accounts
(
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    username VARCHAR(32) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL,
    server_address VARCHAR(255) NULL,
    role VARCHAR(9) NULL DEFAULT NULL CHECK (role IN ('ADMIN', 'MODERATOR', 'GUEST'))
);

CREATE TABLE main.companies
(
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name VARCHAR(128) NOT NULL UNIQUE,
    in_house_production BOOLEAN NOT NULL
);

CREATE TABLE main.servers
(
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name VARCHAR(32) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL UNIQUE,
    last_used_username VARCHAR(32) NULL
);

CREATE TABLE main.activity_logs
(
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    subject_id INTEGER NOT NULL,
    subject_type VARCHAR(64) NOT NULL,
    description VARCHAR(255) NOT NULL,
    actor_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE main.server_config
(
    name VARCHAR(32) NOT NULL,
    is_public BOOLEAN DEFAULT FALSE
);

INSERT INTO main.server_config(name, is_public) VALUES ('Lokalni server', false);