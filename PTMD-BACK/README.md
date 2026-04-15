# 🏥 PTMD Backend API

> API Principal (Backend) para o sistema de diagnóstico médico **PTMD-YOLO**, desenvolvida em **Java Spring Boot 3**.

---

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Guia de Instalação](#-guia-de-instalação)
  - [Instalação Local](#opção-1--instalação-local)
  - [Instalação com Docker](#opção-2--instalação-com-docker)
- [Configuração](#-configuração)
- [Executando a Aplicação](#-executando-a-aplicação)
- [Documentação Swagger/OpenAPI](#-documentação-swaggeropenapi)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Endpoints da API](#-endpoints-da-api)
- [Fluxo de Consulta](#-fluxo-de-consulta)
- [Segurança](#-segurança)
- [Credenciais Padrão](#-credenciais-padrão)
- [Troubleshooting](#-troubleshooting)

---

## 🔍 Visão Geral

O PTMD Backend é a API REST central do sistema de diagnóstico médico assistido por IA. Ele gerencia:

- **Autenticação e autorização** via JWT (RBAC: ADMIN / MEDICO)
- **Cadastro de pacientes** e criação de consultas médicas
- **Upload e processamento de imagens** de exames otológicos
- **Integração com microsserviço de IA** (Python) para diagnóstico automático
- **Confirmação de diagnósticos** individualmente por imagem
- **Backup de dados** com exportação em ZIP (imagens + CSV de metadados)

---

## 🛠 Tecnologias

| Tecnologia | Versão | Descrição |
|---|---|---|
| **Java** | 17+ | Linguagem principal |
| **Spring Boot** | 3.2.0 | Framework web |
| **Spring Security** | — | Autenticação JWT |
| **MySQL** | 8.0+ | Banco de dados relacional |
| **JPA / Hibernate** | — | ORM para persistência |
| **Lombok** | — | Redução de boilerplate |
| **WebClient** | — | Integração reativa com microsserviço Python |
| **SpringDoc OpenAPI** | 2.3.0 | Documentação Swagger |
| **Maven** | 3.6+ | Gerenciador de build/dependências |
| **Docker** | — | Containerização |

---

## ✅ Pré-requisitos

### Para instalação local

- [Java JDK 17+](https://adoptium.net/) instalado e configurado no `PATH`
- [Maven 3.6+](https://maven.apache.org/download.cgi) instalado e configurado no `PATH`
- [MySQL 8.0+](https://dev.mysql.com/downloads/installer/) instalado e rodando
- Microsserviço Python (PTMD-BACK-IA) rodando em `http://localhost:8081`

### Para instalação com Docker

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando

### Verificando os pré-requisitos

```bash
# Verificar Java
java -version
# Esperado: openjdk version "17.x.x" ou superior

# Verificar Maven
mvn -version
# Esperado: Apache Maven 3.6.x ou superior

# Verificar MySQL
mysql --version
# Esperado: mysql Ver 8.x.x

# Verificar Docker (se usar Docker)
docker --version
docker compose version
```

---

## 🚀 Guia de Instalação

### Opção 1 — Instalação Local

#### Passo 1: Clonar o repositório

```bash
git clone https://github.com/JairAssisDev/ptmd.git
cd ptmd/PTMD-BACK
```

#### Passo 2: Configurar o banco de dados MySQL

Conecte-se ao MySQL e crie o banco:

```sql
CREATE DATABASE IF NOT EXISTS ptmd_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

> 💡 **Nota:** O Spring Boot JPA criará as tabelas automaticamente na primeira execução. Usuários padrão (admin e médico mock) também são criados pela aplicação.

#### Passo 3: Configurar o `application.properties`

Edite o arquivo `src/main/resources/application.properties` com as credenciais do seu MySQL:

```properties
# Conexão com o banco
spring.datasource.url=jdbc:mysql://localhost:3306/ptmd_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=sua_senha_aqui

# URL do microsserviço de IA
python.api.url=http://localhost:8081

# Diretório de uploads (será criado automaticamente)
file.upload-dir=uploads
```

#### Passo 4: Instalar dependências e compilar

```bash
mvn clean install -DskipTests
```

#### Passo 5: Executar a aplicação

```bash
mvn spring-boot:run
```

✅ **A API estará disponível em:** `http://localhost:8080`

---

### Opção 2 — Instalação com Docker

#### Passo 1: Clonar o repositório

```bash
git clone https://github.com/JairAssisDev/ptmd.git
cd ptmd/PTMD-BACK
```

#### Passo 2: Subir os containers (Backend + MySQL)

```bash
docker compose up --build -d
```

Isso irá criar e iniciar:

| Container | Porta | Descrição |
|---|---|---|
| `ptmd-mysql` | `3306` | Banco de dados MySQL 8.0 |
| `ptmd-backend` | `8080` | API Spring Boot |

#### Passo 3: Verificar se os containers estão rodando

```bash
docker compose ps
```

#### Passo 4: Acompanhar os logs

```bash
# Logs do backend
docker logs -f ptmd-backend

# Logs do MySQL
docker logs -f ptmd-mysql
```

✅ **A API estará disponível em:** `http://localhost:8080`

#### Credenciais do MySQL no Docker

| Variável | Valor |
|---|---|
| `MYSQL_ROOT_PASSWORD` | `root_password` |
| `MYSQL_DATABASE` | `ptmd_db` |
| `MYSQL_USER` | `ptmd_user` |
| `MYSQL_PASSWORD` | `ptmd_password` |

#### Parar os containers

```bash
docker compose down

# Para remover também os volumes (dados do banco):
docker compose down -v
```

---

## ⚙ Configuração

### Variáveis de configuração importantes

| Propriedade | Descrição | Padrão (Local) | Padrão (Docker) |
|---|---|---|---|
| `spring.datasource.url` | URL do banco MySQL | `localhost:3306/ptmd_db` | `mysql:3306/ptmd_db` |
| `python.api.url` | URL do microsserviço de IA | `http://localhost:8081` | `http://python-api:8081` |
| `file.upload-dir` | Diretório de uploads | `uploads` | `/app/uploads` |
| `jwt.expiration` | Tempo de expiração do token | `86400000` (24h) | `86400000` (24h) |

### Limites do sistema

- **Tamanho máximo por arquivo:** 10 MB
- **Tamanho máximo por requisição:** 10 MB
- **Máximo de imagens por consulta:** 10
- **Timeout de requisição à IA:** 60 segundos

---

## ▶ Executando a Aplicação

### Modo Desenvolvimento (Local)

```bash
mvn spring-boot:run
```

### Modo Produção (Docker)

```bash
docker compose up --build -d
```

### Diretório de Uploads

O sistema criará automaticamente a pasta `uploads/` na raiz do projeto (local) ou `/app/uploads` (Docker) para armazenar as imagens enviadas.

---

## 📚 Documentação Swagger/OpenAPI

Após iniciar a aplicação, acesse a documentação interativa:

| Recurso | URL |
|---|---|
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |

### Como usar o Swagger

1. Acesse `http://localhost:8080/swagger-ui.html`
2. Para testar endpoints protegidos:
   - Faça login em `POST /api/auth/login`
   - Copie o token JWT retornado
   - Clique no botão **"Authorize"** no topo da página
   - Cole o token no formato: `Bearer {seu_token}`
   - Agora você pode testar todos os endpoints protegidos

### Funcionalidades do Swagger

- ✅ Documentação completa de todos os endpoints
- ✅ Teste interativo diretamente no navegador
- ✅ Autenticação JWT integrada
- ✅ Exemplos de requisições e respostas
- ✅ Validação de schemas
- ✅ Organização por tags

---

## 📁 Estrutura do Projeto

```
PTMD-BACK/
├── src/main/java/com/ptmd/
│   ├── config/          # Configurações (DataInitializer)
│   ├── controller/      # Controladores REST
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # Entidades JPA
│   ├── exception/       # Handlers de exceção globais
│   ├── repository/      # Repositórios JPA
│   ├── security/        # Configuração de segurança e JWT
│   └── service/         # Lógica de negócio
├── src/main/resources/
│   ├── application.properties          # Config desenvolvimento
│   └── application-docker.properties   # Config Docker/produção
├── Dockerfile           # Build multi-stage do container
├── docker-compose.yml   # Orquestração Backend + MySQL
├── init.sql             # Script de inicialização do banco
├── pom.xml              # Dependências Maven
└── uploads/             # Imagens armazenadas (gerado em runtime)
```

---

## 🔗 Endpoints da API

### 🔓 Autenticação (público)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/auth/register` | Cadastro público de médico |
| `POST` | `/api/auth/login` | Login e obtenção de token JWT |

### 🩺 Médico (requer `MEDICO` ou `ADMIN`)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/medico/consultations` | Criar nova consulta (com upload de imagens) |
| `GET` | `/api/medico/consultations` | Listar consultas do médico (filtros: nome, CPF) |
| `GET` | `/api/medico/consultations/{id}` | Obter detalhes de uma consulta |
| `PUT` | `/api/medico/consultations/{id}/confirm` | Confirmar diagnóstico da consulta (legado) |
| `PUT` | `/api/medico/consultations/images/{imageId}/confirm` | Confirmar diagnóstico por imagem |

### 🔧 Administração (requer `ADMIN`)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/admin/dashboard` | Estatísticas do sistema |
| `POST` | `/api/admin/change-password` | Alterar senha do administrador |
| `GET` | `/api/admin/backup` | Download do backup (ZIP com imagens + CSV) |

### 📁 Arquivos (público)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/files/by-name/{filename}` | Servir imagem por nome do arquivo |

---

## 🔄 Fluxo de Consulta

```
1. Médico cria consulta
   ├── Envia dados do paciente + imagens
   ├── API busca/cria paciente por CPF
   └── Para cada imagem:
       ├── Salva no disco (uploads/)
       ├── Envia para microsserviço Python (POST /predict)
       ├── Recebe diagnóstico da IA (Normal / Anormal + classe)
       └── Salva resultado na entidade Image

2. Médico confirma diagnóstico
   ├── Visualiza cada imagem com diagnóstico da IA
   ├── Seleciona diagnóstico final
   └── API marca imagem como confirmada
```

### Diagnósticos possíveis

| Valor | Descrição |
|---|---|
| `Normal` | Ouvido normal |
| `aom` | Otite Média Aguda |
| `csom` | Otite Média Crônica |
| `earwax` | Cerúmen |
| `ExternalEarInfections` | Infecções do Ouvido Externo |
| `tympanoskleros` | Timpanoesclerose |

---

## 🔐 Segurança

- **Autenticação:** JWT (HS512) com expiração de 24 horas
- **Autorização:** RBAC (Role-Based Access Control) com roles `ADMIN` e `MEDICO`
- **Senhas:** Hashadas com BCrypt
- **CSRF:** Desabilitado (API stateless)
- **CORS:** Configurado para permitir requests do frontend

### Endpoints públicos (sem autenticação)

- `/api/auth/**` — Cadastro e login
- `/api/files/**` — Servir imagens
- `/swagger-ui/**` — Documentação
- `/v3/api-docs/**` — OpenAPI spec

---

## 🔑 Credenciais Padrão

### Administrador

| Campo | Valor |
|---|---|
| **Email** | `admin` |
| **Senha** | `admin` |

### Médico (Mock)

| Campo | Valor |
|---|---|
| **Email** | `medico@example.com` |
| **Senha** | `password` |
| **CPF** | `123.456.789-00` |
| **CRM** | `CRM/SP 123456` |

> ⚠️ **Importante:** Estes usuários são criados automaticamente pelo `DataInitializer` na primeira execução. Altere as senhas padrão em ambiente de produção.

---

## 🐛 Troubleshooting

### Erro: Não conecta ao MySQL

```
Communications link failure
```

**Soluções:**
1. Verifique se o MySQL está rodando: `mysql -u root -p`
2. Confirme as credenciais em `application.properties`
3. Verifique se o banco `ptmd_db` existe
4. Confira se a porta `3306` está livre: `netstat -an | findstr 3306`

### Erro: Não conecta ao microsserviço Python

```
Connection refused: localhost:8081
```

**Soluções:**
1. Verifique se o PTMD-BACK-IA está rodando na porta `8081`
2. Teste manualmente: `curl http://localhost:8081/predict`
3. Se estiver usando Docker, verifique a URL: `http://python-api:8081`

### Erro: Upload de imagem falha

```
Maximum upload size exceeded
```

**Soluções:**
1. Verifique se a imagem tem menos de 10 MB
2. Formatos aceitos: JPG, JPEG, PNG
3. Verifique se a pasta `uploads/` tem permissão de escrita

### Docker: Container não inicia

```bash
# Ver logs detalhados
docker logs ptmd-backend

# Recriar do zero
docker compose down -v
docker compose up --build -d
```

---

## 📄 Licença

Este projeto faz parte do sistema **PTMD-YOLO** — Sistema de Diagnóstico Médico com IA.

**Repositório:** [https://github.com/JairAssisDev/ptmd](https://github.com/JairAssisDev/ptmd)
