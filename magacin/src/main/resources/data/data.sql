-- Insert data into address table
INSERT INTO address (address, city, country, email, mobile_number, name, phone_number)
VALUES
    ('123 Main St', 'Springfield', 'USA', 'john.doe@email.com', '555-1234', 'John Doe', '555-5678'),
    ('456 Elm St', 'Shelbyville', 'USA', 'jane.smith@email.com', '555-8765', 'Jane Smith', '555-4321'),
    ('789 Oak St', 'Springfield', 'USA', 'mike.jones@email.com', '555-6789', 'Mike Jones', '555-7890'),
    ('101 Pine St', 'Shelbyville', 'USA', 'emily.brown@email.com', '555-2345', 'Emily Brown', '555-8764'),
    ('202 Birch St', 'Capitol City', 'USA', 'sam.wilson@email.com', '555-3456', 'Sam Wilson', '555-5679');

-- Insert data into article_categories table
INSERT INTO article_categories (created_at, name)
VALUES
    (CURRENT_TIMESTAMP, 'Electronics'),
    (CURRENT_TIMESTAMP, 'Furniture'),
    (CURRENT_TIMESTAMP, 'Clothing'),
    (CURRENT_TIMESTAMP, 'Toys'),
    (CURRENT_TIMESTAMP, 'Sports');

-- Insert data into article_has_tag table
INSERT INTO article_has_tag (article_id, created_at, name)
VALUES
    (1, CURRENT_TIMESTAMP, 'Technology'),
    (2, CURRENT_TIMESTAMP, 'Home'),
    (3, CURRENT_TIMESTAMP, 'Fashion'),
    (4, CURRENT_TIMESTAMP, 'Kids'),
    (5, CURRENT_TIMESTAMP, 'Fitness');

-- Insert data into articles table
INSERT INTO articles (active, in_stock_amount, category_id, created_at, code, name, description, unit_measure_id)
VALUES
    ('Y', 100.00, 1, CURRENT_TIMESTAMP, 'ELEC-001', 'Smartphone', 'Latest model smartphone with advanced features', 1),
    ('N', 50.00, 2, CURRENT_TIMESTAMP, 'FURN-001', 'Sofa', 'Comfortable 3-seater sofa', 2),
    ('Y', 200.00, 3, CURRENT_TIMESTAMP, 'CLOTH-001', 'T-shirt', 'Cotton t-shirt with a cool design', 1),
    ('Y', 150.00, 4, CURRENT_TIMESTAMP, 'TOY-001', 'Doll', 'Plush doll for children', 2),
    ('N', 120.00, 5, CURRENT_TIMESTAMP, 'SPRT-001', 'Basketball', 'High-quality basketball for outdoor play', 1);

-- Insert data into company table
INSERT INTO company (is_recipient, is_supplier, created_at, name, description)
VALUES
    (1, 0, CURRENT_TIMESTAMP, 'Tech Corp', 'Leading electronics manufacturer'),
    (0, 1, CURRENT_TIMESTAMP, 'Home Goods Inc', 'Supplier of home furniture'),
    (1, 1, CURRENT_TIMESTAMP, 'Fashion Inc', 'Apparel manufacturer and supplier'),
    (0, 1, CURRENT_TIMESTAMP, 'Toyland', 'Toy manufacturer and distributor'),
    (1, 0, CURRENT_TIMESTAMP, 'Sports Equip', 'Sports equipment supplier');

-- Insert data into company_has_address table
INSERT INTO company_has_address (address_id, company_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);

-- Insert data into contact table
INSERT INTO contact (email, mobile_number, phone_number, title)
VALUES
    ('john.doe@email.com', '555-1234', '555-5678', 'Mr.'),
    ('jane.smith@email.com', '555-8765', '555-4321', 'Ms.'),
    ('mike.jones@email.com', '555-6789', '555-7890', 'Mr.'),
    ('emily.brown@email.com', '555-2345', '555-8764', 'Ms.'),
    ('sam.wilson@email.com', '555-3456', '555-5679', 'Mr.');

-- Insert data into receipt_report table
INSERT INTO receipt_report (report_id, supplier_id, supplier_report_code)
VALUES
    (1, 2, 'SR123'),
    (2, 3, 'SR456'),
    (3, 4, 'SR789'),
    (4, 5, 'SR101'),
    (5, 1, 'SR202');

-- Insert data into report table
INSERT INTO report (date, created_at, signed_by_id, code, description, place_of_publish, type)
VALUES
    ('2025-01-07', CURRENT_TIMESTAMP, 1, 'RPT001', 'Annual report', 'New York', 'RECEIPT'),
    ('2025-01-07', CURRENT_TIMESTAMP, 2, 'RPT002', 'Quarterly report', 'Los Angeles', 'SHIPMENT'),
    ('2025-01-07', CURRENT_TIMESTAMP, 3, 'RPT003', 'Monthly report', 'Chicago', 'RECEIPT'),
    ('2025-01-07', CURRENT_TIMESTAMP, 4, 'RPT004', 'Year-end report', 'Houston', 'SHIPMENT'),
    ('2025-01-07', CURRENT_TIMESTAMP, 5, 'RPT005', 'Financial report', 'San Francisco', 'RECEIPT');

-- Insert data into roles_privileges table
INSERT INTO roles_privileges (privilege_id, role_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);

-- Insert data into shipment_report table
INSERT INTO shipment_report (recipient_id, report_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);

-- Insert data into unit_measurements table
INSERT INTO unit_measurements (active, is_integer, created_at, short_name, name)
VALUES
    ('Y', 1, CURRENT_TIMESTAMP, 'KG', 'Kilogram'),
    ('Y', 0, CURRENT_TIMESTAMP, 'MTR', 'Meter'),
    ('Y', 1, CURRENT_TIMESTAMP, 'L', 'Liter'),
    ('N', 0, CURRENT_TIMESTAMP, 'CM', 'Centimeter'),
    ('Y', 1, CURRENT_TIMESTAMP, 'PCS', 'Piece');

-- Insert data into user table
INSERT INTO user (active, created_at, username, email, password)
VALUES
    ('Y', CURRENT_TIMESTAMP, 'john_doe', 'john.doe@email.com', 'password123'),
    ('N', CURRENT_TIMESTAMP, 'jane_smith', 'jane.smith@email.com', 'password456'),
    ('Y', CURRENT_TIMESTAMP, 'mike_jones', 'mike.jones@email.com', 'password789'),
    ('N', CURRENT_TIMESTAMP, 'emily_brown', 'emily.brown@email.com', 'password101'),
    ('Y', CURRENT_TIMESTAMP, 'sam_wilson', 'sam.wilson@email.com', 'password202');

-- Insert data into user_privilege table
INSERT INTO user_privilege (name)
VALUES
    ('Admin'),
    ('User'),
    ('Manager'),
    ('Viewer'),
    ('Editor');

-- Insert data into user_profile table
INSERT INTO user_profile (first_name, last_name)
VALUES
    ('John', 'Doe'),
    ('Jane', 'Smith'),
    ('Mike', 'Jones'),
    ('Emily', 'Brown'),
    ('Sam', 'Wilson');

-- Insert data into user_role table
INSERT INTO user_role (name)
VALUES
    ('Admin'),
    ('Viewer'),
    ('Editor'),
    ('Manager'),
    ('User');

-- Insert data into user_roles table
INSERT INTO user_roles (role_id, user_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);
