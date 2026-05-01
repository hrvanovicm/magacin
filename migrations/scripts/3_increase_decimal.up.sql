CREATE TABLE main.article_has_recipes_new
(
    article_id      INTEGER                  NOT NULL,
    raw_material_id INTEGER                  NOT NULL,
    amount          DECIMAL(15, 6) DEFAULT 0 NOT NULL,

    PRIMARY KEY (article_id, raw_material_id)
);

INSERT INTO main.article_has_recipes_new (article_id, raw_material_id, amount)
SELECT article_id, raw_material_id, amount FROM main.article_has_recipes;

DROP TABLE main.article_has_recipes;

ALTER TABLE main.article_has_recipes_new RENAME TO article_has_recipes;

CREATE TABLE main.articles_new
(
    id                      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    category                VARCHAR(12)                       NOT NULL,
    name                    VARCHAR(64)                       NOT NULL UNIQUE,
    code                    VARCHAR(64) UNIQUE,
    in_stock_amount         DECIMAL(15, 6) DEFAULT 0          NOT NULL,
    in_stock_warning_amount DECIMAL(15, 6) DEFAULT 0          NOT NULL,
    unit_measure_id         INTEGER        DEFAULT NULL,
    tags                    VARCHAR(255)   DEFAULT ''         NOT NULL,

    CHECK (category IN ('COMMERCIAL', 'PRODUCT', 'RAW_MATERIAL')),
    FOREIGN KEY (unit_measure_id) REFERENCES unit_measurements (id) ON DELETE SET DEFAULT
);

INSERT INTO main.articles_new (id, category, name, code, in_stock_amount, in_stock_warning_amount, unit_measure_id, tags)
SELECT id, category, name, code, in_stock_amount, in_stock_warning_amount, unit_measure_id, tags FROM main.articles;

DROP TABLE main.articles;

ALTER TABLE main.articles_new RENAME TO articles;

CREATE TABLE main.report_has_recipes_new
(
    report_id       INTEGER                  NOT NULL,
    article_id      INTEGER                  NOT NULL,
    raw_material_id INTEGER                  NOT NULL,
    amount          DECIMAL(15, 6) DEFAULT 0 NOT NULL,

    PRIMARY KEY (report_id, article_id, raw_material_id),
    FOREIGN KEY (report_id) REFERENCES reports (id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE,
    FOREIGN KEY (raw_material_id) REFERENCES articles (id) ON DELETE CASCADE
);

INSERT INTO main.report_has_recipes_new (report_id, article_id, raw_material_id, amount)
SELECT report_id, article_id, raw_material_id, amount FROM main.report_has_recipes;

DROP TABLE main.report_has_recipes;

ALTER TABLE main.report_has_recipes_new RENAME TO report_has_recipes;

CREATE TABLE main.report_has_articles_new
(
    article_id INTEGER                  NOT NULL,
    report_id  INTEGER                  NOT NULL,
    amount     DECIMAL(15, 6) DEFAULT 0 NOT NULL,

    PRIMARY KEY (report_id, article_id),
    FOREIGN KEY (report_id) REFERENCES reports (id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE
);

INSERT INTO main.report_has_articles_new (article_id, report_id, amount)
SELECT article_id, report_id, amount FROM main.report_has_articles;

DROP TABLE main.report_has_articles;

ALTER TABLE main.report_has_articles_new RENAME TO report_has_articles;
