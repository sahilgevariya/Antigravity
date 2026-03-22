-- V2__seed_roles.sql
-- Seed the four application roles

INSERT INTO roles (name, description) VALUES ('ADMIN', 'System administrator with full access');
INSERT INTO roles (name, description) VALUES ('PHARMACIST', 'Pharmacist managing inventory and orders');
INSERT INTO roles (name, description) VALUES ('DOCTOR', 'Doctor requesting supplies and dispensing');
INSERT INTO roles (name, description) VALUES ('SUPPLIER', 'External supplier managing their own data');
