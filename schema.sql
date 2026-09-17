-- =============================================
-- SGD — Sistema Gestor de Deudores
-- Script de creación de base de datos (Supabase)
-- Ejecutar en el SQL Editor de Supabase.
-- =============================================

DROP TABLE IF EXISTS user_approvals CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

-- =============================================
-- TABLA: clientes (deudores)
-- =============================================
CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    city VARCHAR(100),
    registration_date TIMESTAMP,
    debt NUMERIC(12, 2) NOT NULL DEFAULT 0,
    payment NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    discount BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE INDEX idx_clientes_status ON clientes(status);
CREATE INDEX idx_clientes_city ON clientes(city);

-- =============================================
-- TABLA: user_approvals (usuarios mobile + flujo de aprobación)
-- =============================================
CREATE TABLE user_approvals (
    id BIGSERIAL PRIMARY KEY,
    firebase_uid TEXT UNIQUE NOT NULL,
    email TEXT NOT NULL,
    nombre TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING',
    role TEXT NOT NULL DEFAULT 'USER',
    approved_by TEXT,
    approved_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_approvals_status ON user_approvals(status);

-- =============================================
-- DATOS DE PRUEBA
-- =============================================
INSERT INTO clientes (first_name, last_name, city, registration_date, debt, payment, total_amount, discount, status) VALUES
    ('Roberto', 'Alvarez', 'San Salvador', NOW() - INTERVAL '30 days', 2500.00, 0, 2500.00, FALSE, 'ACTIVE'),
    ('Carmen', 'Pineda', 'Santa Ana', NOW() - INTERVAL '25 days', 1200.50, 0, 1200.50, FALSE, 'ACTIVE'),
    ('Francisco', 'Mejia', 'San Miguel', NOW() - INTERVAL '20 days', 1800.00, 1800.00, 0, TRUE, 'CANCELLED'),
    ('Gloria', 'Hernandez', 'San Salvador', NOW() - INTERVAL '15 days', 4200.75, 0, 4200.75, FALSE, 'ACTIVE');

INSERT INTO user_approvals (firebase_uid, email, nombre, status, role, created_at) VALUES
    ('fb-admin-001', 'admin@sgd.com', 'Administrador SGD', 'APPROVED', 'ADMIN', NOW() - INTERVAL '30 days');
