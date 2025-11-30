-- Script de inicialização do banco de dados MySQL para PTMD
-- Este arquivo é executado automaticamente quando o container MySQL é criado pela primeira vez

-- Criar banco de dados se não existir
CREATE DATABASE IF NOT EXISTS ptmd_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar o banco de dados
USE ptmd_db;

-- O Spring Boot JPA criará as tabelas automaticamente
-- Os usuários (Admin e Médico) são criados automaticamente pelo DataInitializer.java
-- Não é necessário criar usuários aqui, o Java faz isso na inicialização da aplicação

