CREATE TABLE main.in_house_companies
(
    name VARCHAR(128) PRIMARY KEY NOT NULL
);

CREATE TABLE main.article_has_recipes
(
    article_id      INTEGER                  NOT NULL,
    raw_material_id INTEGER                  NOT NULL,
    amount          DECIMAL(10, 2) DEFAULT 0 NOT NULL,

    PRIMARY KEY (article_id, raw_material_id)
);

CREATE TABLE main.unit_measurements
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name       VARCHAR(32)                       NOT NULL UNIQUE,
    is_integer BOOLEAN DEFAULT TRUE              NOT NULL
);

CREATE TABLE main.articles
(
    id                      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    category                VARCHAR(12)                       NOT NULL,
    name                    VARCHAR(64)                       NOT NULL UNIQUE,
    code                    VARCHAR(64) UNIQUE,
    in_stock_amount         DECIMAL(10, 2) DEFAULT 0          NOT NULL,
    in_stock_warning_amount DECIMAL(10, 2) DEFAULT 0          NOT NULL,
    unit_measure_id         INTEGER        DEFAULT NULL,
    tags                    VARCHAR(255)   DEFAULT ''         NOT NULL,

    CHECK (category IN ('COMMERCIAL', 'PRODUCT', 'RAW_MATERIAL')),
    FOREIGN KEY (unit_measure_id) REFERENCES unit_measurements (id) ON DELETE SET DEFAULT
);

CREATE TABLE main.reports
(
    id                 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    type               VARCHAR(7)                        NOT NULL,
    code               VARCHAR(128) UNIQUE,
    signed_at          DATETIME,
    signed_at_location VARCHAR(128),
    signed_by          VARCHAR(128),

    CHECK (type IN ('RECEIPT', 'SHIPMENT'))
);

CREATE TABLE main.receipts
(
    report_id             INTEGER NOT NULL,
    supplier_company_name VARCHAR(128),
    supplier_report_code  VARCHAR(128),

    PRIMARY KEY (report_id),
    FOREIGN KEY (report_id) REFERENCES reports (id) ON DELETE CASCADE
);

CREATE TABLE main.report_has_recipes
(
    report_id       INTEGER                  not null,
    article_id      INTEGER                  not null,
    raw_material_id INTEGER                  not null,
    amount          DECIMAL(10, 2) default 0 not null,

    PRIMARY KEY (report_id, article_id, raw_material_id),
    FOREIGN KEY (report_id) REFERENCES reports (id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE,
    FOREIGN KEY (raw_material_id) REFERENCES articles (id) ON DELETE CASCADE
);

CREATE TABLE main.report_has_articles
(
    article_id INTEGER                  NOT NULL,
    report_id  INTEGER                  NOT NULL,
    amount     DECIMAL(10, 2) DEFAULT 0 NOT NULL,

    PRIMARY KEY (report_id, article_id),
    FOREIGN KEY (report_id) REFERENCES reports (id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE
);

CREATE TABLE main.shipments
(
    report_id            INTEGER NOT NULL,
    receipt_company_name VARCHAR(128),

    PRIMARY KEY (report_id),
    FOREIGN KEY (report_id) REFERENCES reports (id) ON DELETE CASCADE
);


INSERT INTO main.unit_measurements (id, name, is_integer) VALUES
(1, 'kg', FALSE),
(2, 'grams', FALSE),
(3, 'liters', FALSE),
(4, 'mL', FALSE),
(5, 'unit', TRUE),
(6, 'box', TRUE),
(7, 'meter', FALSE),
(8, 'm²', FALSE),
(9, 'roll', TRUE),
(10, 'pack', TRUE);

INSERT INTO main.articles (id, category, name, code, in_stock_amount, in_stock_warning_amount, unit_measure_id, tags) VALUES
-- RAW_MATERIAL (1-10)
(1, 'RAW_MATERIAL', 'Flour - Wheat', 'RM-FLOUR-WH', 450.50, 50.00, 1, 'baking, staple'),
(2, 'RAW_MATERIAL', 'Sugar - Granulated', 'RM-SUGAR-GR', 200.75, 25.00, 1, 'sweetener, baking'),
(3, 'RAW_MATERIAL', 'Milk - Whole', 'RM-MILK-WH', 150.00, 20.00, 3, 'dairy, liquid'),
(4, 'RAW_MATERIAL', 'Eggs - Large', 'RM-EGGS-LG', 600.00, 50.00, 5, 'dairy, protein'),
(5, 'RAW_MATERIAL', 'Vanilla Extract', 'RM-VANILLA', 5.50, 1.00, 4, 'flavoring'),
(6, 'RAW_MATERIAL', 'Butter - Unsalted', 'RM-BUTTER-UN', 120.00, 10.00, 1, 'fat, dairy'),
(7, 'RAW_MATERIAL', 'Cocoa Powder', 'RM-COCOA', 80.25, 5.00, 2, 'chocolate, baking'),
(8, 'RAW_MATERIAL', 'Salt - Fine', 'RM-SALT', 30.00, 2.00, 2, 'seasoning'),
(9, 'RAW_MATERIAL', 'Yeast - Dry Active', 'RM-YEAST', 15.00, 1.50, 2, 'leavening'),
(10, 'RAW_MATERIAL', 'Plastic Film Roll', 'RM-FILM', 50.00, 5.00, 9, 'packaging'),
-- PRODUCT (11-20)
(11, 'PRODUCT', 'Chocolate Chip Cookie', 'PD-CCC', 1200.00, 100.00, 5, 'dessert, sweet'),
(12, 'PRODUCT', 'Sourdough Loaf', 'PD-SLOAF', 500.00, 50.00, 5, 'bread, artisan'),
(13, 'PRODUCT', 'Wedding Cake - Small', 'PD-WCS', 15.00, 2.00, 5, 'custom, cake'),
(14, 'PRODUCT', 'Milk Chocolate Bar', 'PD-MCB', 850.00, 75.00, 5, 'confectionery'),
(15, 'PRODUCT', 'Apple Pie - 10 inch', 'PD-APIE', 80.00, 10.00, 5, 'fruit, dessert'),
(16, 'PRODUCT', 'Gourmet Brownie Pack', 'PD-GBP', 250.00, 25.00, 10, 'sweet, chocolate'),
(17, 'PRODUCT', 'Mini Croissant', 'PD-MCRO', 1500.00, 150.00, 5, 'pastry, breakfast'),
(18, 'PRODUCT', 'Assorted Tarts Box', 'PD-ATB', 150.00, 15.00, 6, 'dessert, variety'),
(19, 'PRODUCT', 'Whole Grain Bread', 'PD-WGB', 400.00, 40.00, 5, 'healthy, staple'),
(20, 'PRODUCT', 'Cupcake - Red Velvet', 'PD-CRV', 900.00, 90.00, 5, 'cake, sweet'),
-- COMMERCIAL (21-30)
(21, 'COMMERCIAL', 'Paper Towels - Large', 'CM-PTL', 75.00, 10.00, 6, 'cleaning, supplies'),
(22, 'COMMERCIAL', 'Sanitizing Wipes', 'CM-SW', 30.00, 5.00, 10, 'cleaning, hygiene'),
(23, 'COMMERCIAL', 'Delivery Boxes - Large', 'CM-DBL', 500.00, 50.00, 5, 'packaging, logistics'),
(24, 'COMMERCIAL', 'Office Paper Ream', 'CM-OPR', 20.00, 3.00, 10, 'admin, stationery'),
(25, 'COMMERCIAL', 'Printer Ink Cartridge', 'CM-PIC', 8.00, 1.00, 5, 'admin, supplies'),
(26, 'COMMERCIAL', 'Gloves - Nitril Medium', 'CM-GNM', 100.00, 15.00, 10, 'safety, hygiene'),
(27, 'COMMERCIAL', 'Aprons - Disposable', 'CM-AD', 200.00, 20.00, 10, 'safety, apparel'),
(28, 'COMMERCIAL', 'Cleaning Solution All-Purpose', 'CM-CSA', 10.00, 2.00, 3, 'cleaning, liquid'),
(29, 'COMMERCIAL', 'Trash Bags - Industrial', 'CM-TBI', 40.00, 5.00, 6, 'cleaning, waste'),
(30, 'COMMERCIAL', 'Thermal Printer Paper', 'CM-TPP', 90.00, 10.00, 9, 'admin, receipt');

INSERT INTO main.reports (id, type, code, signed_at, signed_at_location, signed_by) VALUES
-- RECEIPTS (1-10)
(1, 'RECEIPT', 'RPT-REC-2025-0001', '2025-10-01 09:30:00', 'Main Warehouse', 'John Doe'),
(2, 'RECEIPT', 'RPT-REC-2025-0002', '2025-10-01 14:00:00', 'Receiving Dock A', 'Jane Smith'),
(3, 'RECEIPT', 'RPT-REC-2025-0003', '2025-10-02 11:15:00', 'Main Warehouse', 'John Doe'),
(4, 'RECEIPT', 'RPT-REC-2025-0004', '2025-10-03 08:45:00', 'Receiving Dock B', 'Peter Jones'),
(5, 'RECEIPT', 'RPT-REC-2025-0005', '2025-10-04 10:00:00', 'Main Warehouse', 'Jane Smith'),
(6, 'RECEIPT', 'RPT-REC-2025-0006', '2025-10-05 16:20:00', 'Receiving Dock A', 'Peter Jones'),
(7, 'RECEIPT', 'RPT-REC-2025-0007', '2025-10-06 09:00:00', 'Main Warehouse', 'John Doe'),
(8, 'RECEIPT', 'RPT-REC-2025-0008', '2025-10-07 13:10:00', 'Receiving Dock B', 'Jane Smith'),
(9, 'RECEIPT', 'RPT-REC-2025-0009', '2025-10-08 10:30:00', 'Main Warehouse', 'Peter Jones'),
(10, 'RECEIPT', 'RPT-REC-2025-0010', '2025-10-09 15:45:00', 'Receiving Dock A', 'John Doe'),
(11, 'SHIPMENT', 'RPT-SHP-2025-0001', '2025-10-01 12:00:00', 'Shipping Bay 1', 'Alice Brown'),
(12, 'SHIPMENT', 'RPT-SHP-2025-0002', '2025-10-01 16:30:00', 'Shipping Bay 2', 'Bob Green'),
(13, 'SHIPMENT', 'RPT-SHP-2025-0003', '2025-10-02 15:00:00', 'Shipping Bay 1', 'Alice Brown'),
(14, 'SHIPMENT', 'RPT-SHP-2025-0004', '2025-10-03 11:00:00', 'Shipping Bay 3', 'Charlie White'),
(15, 'SHIPMENT', 'RPT-SHP-2025-0005', '2025-10-04 13:45:00', 'Shipping Bay 2', 'Bob Green'),
(16, 'SHIPMENT', 'RPT-SHP-2025-0006', '2025-10-05 10:30:00', 'Shipping Bay 1', 'Charlie White'),
(17, 'SHIPMENT', 'RPT-SHP-2025-0007', '2025-10-06 14:15:00', 'Shipping Bay 3', 'Alice Brown'),
(18, 'SHIPMENT', 'RPT-SHP-2025-0008', '2025-10-07 16:00:00', 'Shipping Bay 2', 'Bob Green'),
(19, 'SHIPMENT', 'RPT-SHP-2025-0009', '2025-10-08 12:45:00', 'Shipping Bay 1', 'Charlie White'),
(20, 'SHIPMENT', 'RPT-SHP-2025-0010', '2025-10-09 09:15:00', 'Shipping Bay 3', 'Alice Brown');

INSERT INTO main.report_has_articles (report_id, article_id, amount) VALUES
-- Receipts for RAW_MATERIAL/COMMERCIAL
(1, 1, 100.00),   -- Flour
(1, 4, 120.00),   -- Eggs
(2, 3, 50.00),    -- Milk
(2, 6, 25.00),    -- Butter
(3, 21, 10.00),   -- Paper Towels
(4, 2, 50.00),    -- Sugar
(5, 7, 10.00),    -- Cocoa Powder
(6, 26, 20.00),   -- Gloves
(7, 1, 50.00),    -- Flour
(8, 28, 5.00),    -- Cleaning Solution
(9, 30, 25.00),   -- Printer Paper
(10, 5, 2.00),    -- Vanilla Extract
-- Shipments for PRODUCT/COMMERCIAL
(11, 11, 200.00), -- Chocolate Chip Cookie
(12, 12, 50.00),  -- Sourdough Loaf
(13, 14, 100.00), -- Milk Chocolate Bar
(14, 16, 20.00),  -- Gourmet Brownie Pack
(15, 17, 300.00), -- Mini Croissant
(16, 20, 150.00), -- Cupcake - Red Velvet
(17, 23, 10.00),  -- Delivery Boxes
(18, 11, 150.00), -- Chocolate Chip Cookie
(19, 15, 10.00),  -- Apple Pie
(20, 19, 80.00);  -- Whole Grain Bread

INSERT INTO main.article_has_recipes (article_id, raw_material_id, amount) VALUES
(11, 1, 0.05),  -- 0.05 kg of Flour per cookie
(11, 2, 0.03);  -- 0.03 kg of Sugar per cookie

INSERT INTO main.receipts (report_id, supplier_company_name, supplier_report_code) VALUES
(1, 'Agri-Supply Co.', 'SC001-2025'),
(2, 'Dairy & Co.', 'DC045-2025'),
(3, 'OfficePro Inc.', 'OP2025-10-02'),
(4, 'Sweetener Source', 'SS987-2025'),
(5, 'Specialty Foods Ltd.', 'SF500-2025'),
(6, 'SafetyGear Corp.', 'SG123-2025'),
(7, 'Agri-Supply Co.', 'SC002-2025'),
(8, 'Janitorial Solutions', 'JS-OCT-001'),
(9, 'Tech-Prints Inc.', 'TP-2025-20'),
(10, 'Flavoring Central', 'FC-EXT-101');

INSERT INTO main.shipments (report_id, receipt_company_name) VALUES
(11, 'Local Bakery Chain A'),
(12, 'Cafe Grand B'),
(13, 'Supermarket Deli C'),
(14, 'University Cafeteria D'),
(15, 'Local Bakery Chain A'),
(16, 'Corporate Event E'),
(17, 'Internal Use - Production'),
(18, 'Cafe Grand B'),
(19, 'Restaurant Depot F'),
(20, 'Supermarket Deli C');