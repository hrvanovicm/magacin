CREATE TABLE IF NOT EXISTS main.unit_measurement_conversions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    from_unit_measure_id INTEGER NOT NULL,
    to_unit_measure_id INTEGER NOT NULL,
    factor REAL NOT NULL,
    FOREIGN KEY(from_unit_measure_id) REFERENCES unit_measurements(id) ON DELETE CASCADE,
    FOREIGN KEY(to_unit_measure_id) REFERENCES unit_measurements(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS main.article_unit_measurement_conversions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    article_id INTEGER NOT NULL,
    from_unit_measure_id INTEGER NOT NULL,
    to_unit_measure_id INTEGER NOT NULL,
    factor REAL NOT NULL,
    FOREIGN KEY(article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY(from_unit_measure_id) REFERENCES unit_measurements(id) ON DELETE CASCADE,
    FOREIGN KEY(to_unit_measure_id) REFERENCES unit_measurements(id) ON DELETE CASCADE
);
