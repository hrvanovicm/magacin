CREATE TABLE main.notes
(
    subject_type VARCHAR(64) NOT NULL,
    subject_id   INTEGER     NOT NULL,
    content      TEXT        NOT NULL DEFAULT '',
    updated_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (subject_type, subject_id)
);
