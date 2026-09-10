-- V1__init_schema.sql
-- Initial database migration for Jurisprudence Hub Backend

-- Enable UUID extension if supported
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Schema baseline marker
-- Additional module tables (auth, course, etc.) will be added in subsequent migrations
