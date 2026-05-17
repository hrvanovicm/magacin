-- SQLite does not support DROP COLUMN in older versions; recreate without company_name
CREATE TABLE main.server_config_tmp AS SELECT name, is_public FROM main.server_config;
DROP TABLE main.server_config;
ALTER TABLE main.server_config_tmp RENAME TO server_config;
