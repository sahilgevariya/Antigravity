-- V2__seed_inventory_data.sql
-- Seed categories and locations

INSERT INTO categories (name, description) VALUES ('Antibiotics', 'Antibacterial medications');
INSERT INTO categories (name, description) VALUES ('Analgesics', 'Pain relief medications');
INSERT INTO categories (name, description) VALUES ('Cardiovascular', 'Heart and blood pressure medications');
INSERT INTO categories (name, description) VALUES ('Surgical Supplies', 'Surgical instruments and supplies');
INSERT INTO categories (name, description) VALUES ('Diagnostic Equipment', 'Lab and diagnostic equipment');
INSERT INTO categories (name, description) VALUES ('Vaccines', 'Immunization and vaccine products');

INSERT INTO locations (name, type, address) VALUES ('Main Pharmacy', 'PHARMACY', '1st Floor, Main Building');
INSERT INTO locations (name, type, address) VALUES ('Emergency Ward', 'WARD', '2nd Floor, Emergency Block');
INSERT INTO locations (name, type, address) VALUES ('Central Warehouse', 'WAREHOUSE', 'Basement, Store Block');
INSERT INTO locations (name, type, address) VALUES ('ICU Store', 'WARD', '3rd Floor, ICU Block');
