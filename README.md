# 🏥 PTMD — Sistema de Diagnóstico Médico com Inteligência Artificial

> **Projeto de Tecnologia Médica para Diagnóstico** — Sistema completo para diagnóstico de condições otológicas assistido por IA, utilizando modelos YOLO para classificação de imagens.

[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=openjdk)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61DAFB?style=flat-square&logo=react)](https://react.dev/)
[![Python](https://img.shields.io/badge/Python-3.11+-3776AB?style=flat-square&logo=python)](https://python.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker)](https://docker.com/)

---

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Arquitetura do Sistema](#-arquitetura-do-sistema)
- [Projetos do Repositório](#-projetos-do-repositório)
- [Guia Rápido de Instalação](#-guia-rápido-de-instalação)
- [Regras de Negócio](#-regras-de-negócio)
- [Estrutura de Dados](#-estrutura-de-dados)
- [Fluxos Principais](#-fluxos-principais)
- [Endpoints da API](#-endpoints-da-api)
- [Segurança](#-segurança)
- [Credenciais Padrão](#-credenciais-padrão)
- [Infraestrutura e Portas](#-infraestrutura-e-portas)
- [Contribuição](#-contribuição)

---

## 🔍 Visão Geral

O **PTMD** (Projeto de Tecnologia Médica para Diagnóstico) é um sistema completo de diagnóstico médico assistido por Inteligência Artificial, focado na análise de imagens otológicas (ouvido).

### O que o sistema faz

O sistema permite que **médicos**:
- 📋 Cadastrem pacientes e realizem consultas
- 📤 Enviem imagens de exames otológicos para análise por IA
- 🤖 Recebam diagnósticos preliminares automáticos da IA (Normal/Anormal + classificação)
- ✅ Confirmem ou ajustem diagnósticos individualmente por imagem
- 📊 Gerenciem histórico completo de consultas e pacientes

E que **administradores** possam:
- 📈 Visualizar estatísticas gerais do sistema
- 💾 Gerar backups organizados (ZIP com imagens confirmadas + CSV de metadados)
- 🔑 Gerenciar credenciais de acesso

### Diagnósticos suportados

| Classe | Descrição |
|---|---|
| `Normal` | Ouvido saudável |
| `aom` | Otite Média Aguda |
| `csom` | Otite Média Crônica Supurativa |
| `earwax` | Cerúmen (cera de ouvido) |
| `ExternalEarInfections` | Infecções do Ouvido Externo |
| `tympanoskleros` | Timpanoesclerose |

---

## 🏗 Arquitetura do Sistema

O PTMD segue uma arquitetura de **microsserviços**, composta por 3 projetos independentes que se comunicam via HTTP REST:

```
┌─────────────────────────────────────────────────────────────────┐
│                        USUÁRIO (Navegador)                      │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
                  ┌─────────────────────────┐
                  │      PTMD-FRONT         │
                  │    React + TypeScript    │
                  │     Porta: 3000         │
                  │                         │
                  │  • Interface do médico  │
                  │  • Interface do admin   │
                  │  • Upload de imagens    │
                  └────────────┬────────────┘
                               │ HTTP (Axios)
                               ▼
                  ┌─────────────────────────┐
                  │      PTMD-BACK          │
                  │   Java Spring Boot 3    │
                  │     Porta: 8080         │
                  │                         │
                  │  • API REST principal   │
                  │  • Autenticação JWT     │
                  │  • Lógica de negócio    │
                  │  • Persistência (JPA)   │
                  └──────┬──────────┬───────┘
                         │          │
              HTTP (WebClient)   JDBC
                         │          │
                         ▼          ▼
          ┌──────────────────┐  ┌──────────────┐
          │   PTMD-BACK-IA   │  │    MySQL      │
          │  Python FastAPI   │  │   8.0+       │
          │   Porta: 8081    │  │  Porta: 3306  │
          │                  │  │              │
          │ • Modelos YOLO   │  │ • Users      │
          │ • Classificação  │  │ • Patients   │
          │   binária        │  │ • Consults   │
          │ • Classificação  │  │ • Images     │
          │   multiclasse    │  │              │
          └──────────────────┘  └──────────────┘
```

---

## 📂 Projetos do Repositório

Este repositório é um **monorepo** contendo 3 projetos independentes:

| Projeto | Diretório | Tecnologia | Descrição | README |
|---|---|---|---|---|
| **Backend API** | [`PTMD-BACK/`](./PTMD-BACK) | Java 17 + Spring Boot 3.2 | API REST principal, autenticação, lógica de negócio | [📖 README](./PTMD-BACK/README.md) |
| **Microsserviço de IA** | [`PTMD-BACK-IA/`](./PTMD-BACK-IA) | Python 3.11 + FastAPI | Análise de imagens com modelos YOLO | [📖 README](./PTMD-BACK-IA/README.md) |
| **Frontend Web** | [`PTMD-FRONT/`](./PTMD-FRONT) | React 18 + TypeScript + Vite | Interface web responsiva com Material-UI | [📖 README](./PTMD-FRONT/README.md) |

> 📖 Cada projeto possui seu próprio **README.md** com guia de instalação detalhado, passo a passo.

---

## 🚀 Guia Rápido de Instalação

### Pré-requisitos gerais

| Ferramenta | Versão | Necessário para |
|---|---|---|
| **Git** | qualquer | Clonar o repositório |
| **Java JDK** | 17+ | PTMD-BACK |
| **Maven** | 3.6+ | PTMD-BACK |
| **MySQL** | 8.0+ | PTMD-BACK |
| **Python** | 3.11+ | PTMD-BACK-IA |
| **Node.js** | 18+ | PTMD-FRONT |
| **Docker** *(alternativa)* | latest | Todos (via containers) |

### Clonar o repositório

```bash
git clone https://github.com/JairAssisDev/ptmd.git
cd ptmd
```

### Ordem de inicialização recomendada

Os serviços devem ser iniciados na seguinte ordem para garantir que as dependências estejam disponíveis:

```
1️⃣  MySQL (banco de dados)
2️⃣  PTMD-BACK-IA (microsserviço de IA — porta 8081)
3️⃣  PTMD-BACK (backend Java — porta 8080)
4️⃣  PTMD-FRONT (frontend React — porta 3000)
```

### Opção A — Instalação local (passo a passo)

#### 1. Banco de Dados MySQL

```sql
CREATE DATABASE IF NOT EXISTS ptmd_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

#### 2. Microsserviço de IA (Python)

```bash
cd PTMD-BACK-IA
python -m venv venv
venv\Scripts\activate          # Windows
# source venv/bin/activate     # Linux/Mac
pip install -r requirements.txt
python main.py
# Rodando em http://localhost:8081
```

#### 3. Backend API (Java)

```bash
cd PTMD-BACK
# Configurar application.properties com credenciais do MySQL
mvn spring-boot:run
# Rodando em http://localhost:8080
```

#### 4. Frontend (React)

```bash
cd PTMD-FRONT
npm install
npm run dev
# Rodando em http://localhost:3000
```

### Opção B — Instalação com Docker (cada projeto)

```bash
# 1. Backend + MySQL
cd PTMD-BACK
docker compose up --build -d

# 2. Microsserviço de IA
cd PTMD-BACK-IA
docker compose up --build -d

# 3. Frontend
cd PTMD-FRONT
docker compose up --build -d
```

> 📖 Para instruções detalhadas de cada projeto, consulte os READMEs individuais linkados na seção [Projetos do Repositório](#-projetos-do-repositório).

---

## 📏 Regras de Negócio

### 1. Autenticação e autorização

| Regra | Descrição |
|---|---|
| **RN-AUTH-01** | Todos os endpoints (exceto `/api/auth/**` e `/api/files/**`) requerem autenticação via token JWT |
| **RN-AUTH-02** | Token JWT é válido por **24 horas** (86.400.000 ms) e utiliza algoritmo HS512 |
| **RN-AUTH-03** | O token deve ser enviado no header: `Authorization: Bearer {token}` |
| **RN-AUTH-04** | Senhas são armazenadas com hash **BCrypt** — nunca em texto plano |
| **RN-AUTH-05** | Email de cadastro deve ser **único** no sistema |
| **RN-AUTH-06** | Existem duas roles: **ADMIN** (acesso total) e **MEDICO** (acesso restrito às próprias consultas) |
| **RN-AUTH-07** | Cadastro de médico é **público** (não requer autenticação) |

### 2. Gestão de pacientes

| Regra | Descrição |
|---|---|
| **RN-PAC-01** | CPF do paciente é **obrigatório** e deve ser **único** no sistema |
| **RN-PAC-02** | Ao criar uma consulta, o sistema busca paciente existente por CPF |
| **RN-PAC-03** | Se o paciente **já existe**, seus dados (nome, data de nascimento) são atualizados se fornecidos |
| **RN-PAC-04** | Se o paciente **não existe**, um novo registro é criado automaticamente |
| **RN-PAC-05** | Não é permitida a duplicação de pacientes pelo mesmo CPF |
| **RN-PAC-06** | Nome do paciente é **obrigatório** |
| **RN-PAC-07** | Sexo do paciente é **obrigatório** (MASCULINO, FEMININO ou OUTRO) |

### 3. Gestão de consultas

| Regra | Descrição |
|---|---|
| **RN-CON-01** | O médico só pode criar consultas **para si próprio** (associadas ao seu usuário) |
| **RN-CON-02** | O médico só pode visualizar suas **próprias** consultas |
| **RN-CON-03** | O administrador (ADMIN) pode visualizar **todas** as consultas |
| **RN-CON-04** | Pelo menos **1 imagem** é obrigatória ao criar uma consulta |
| **RN-CON-05** | O máximo de imagens por consulta é **10** |
| **RN-CON-06** | Consultas podem ser filtradas por **nome** e **CPF** do paciente |
| **RN-CON-07** | O diagnóstico da consulta é baseado na **primeira imagem** processada pela IA |

### 4. Gestão de imagens e diagnósticos

| Regra | Descrição |
|---|---|
| **RN-IMG-01** | Cada imagem é processada **individualmente** pela IA |
| **RN-IMG-02** | Formatos de imagem aceitos: **JPG, JPEG, PNG** |
| **RN-IMG-03** | Tamanho máximo por imagem: **10 MB** |
| **RN-IMG-04** | Nomes de arquivo são gerados com **UUID** para evitar conflitos |
| **RN-IMG-05** | Imagens são armazenadas em `uploads/` (local) ou `/app/uploads` (Docker) |
| **RN-IMG-06** | O diagnóstico da IA é sempre gerado automaticamente quando a imagem é enviada |
| **RN-IMG-07** | O médico pode **aceitar** o diagnóstico da IA ou escolher um **diagnóstico diferente** |
| **RN-IMG-08** | A confirmação de diagnóstico é feita **por imagem**, não por consulta |
| **RN-IMG-09** | O diagnóstico final deve ser um valor válido do enum `Diagnosis` |
| **RN-IMG-10** | Valores do diagnóstico são **case-insensitive** na conversão |

### 5. Processamento de IA

| Regra | Descrição |
|---|---|
| **RN-IA-01** | O processamento ocorre em **duas etapas**: classificação binária → classificação multiclasse |
| **RN-IA-02** | A classificação binária determina se a imagem é **Normal** ou **Anormal** |
| **RN-IA-03** | Se **Anormal**, uma segunda classificação identifica a condição específica (5 classes possíveis) |
| **RN-IA-04** | O timeout de requisição à IA é de **60 segundos** |
| **RN-IA-05** | Erros no microsserviço de IA são tratados com mensagens descritivas e fallback |
| **RN-IA-06** | O backend Java trata a inconsistência de chave `"class"`/`"Class"` via DTO |

### 6. Backup e exportação de dados

| Regra | Descrição |
|---|---|
| **RN-BKP-01** | Apenas **administradores** podem gerar backups |
| **RN-BKP-02** | O backup inclui apenas imagens com **diagnóstico confirmado** pelo médico |
| **RN-BKP-03** | Imagens no backup são renomeadas: `{imageId}_{patientId}_{finalDiagnosis}.{ext}` |
| **RN-BKP-04** | O backup é um arquivo **ZIP** contendo uma pasta `dataset/` (imagens) e `database.csv` (metadados) |
| **RN-BKP-05** | O CSV contém: Image ID, Patient ID, Model Prediction, Doctor Final Diagnosis |
| **RN-BKP-06** | Apenas imagens cujos arquivos **existem em disco** são incluídas no backup |

### 7. Validações de cadastro

| Campo | Regras |
|---|---|
| **Nome (médico)** | Obrigatório |
| **Email (médico)** | Obrigatório, formato válido, único no sistema |
| **Senha (médico)** | Obrigatória, mínimo 6 caracteres |
| **CPF (médico)** | Opcional, formato válido |
| **CRM (médico)** | Opcional |
| **Data de nascimento** | Opcional, formato válido |
| **Nome (paciente)** | Obrigatório |
| **CPF (paciente)** | Obrigatório, único |
| **Sexo (paciente)** | Obrigatório (MASCULINO, FEMININO, OUTRO) |

---

## 🗂 Estrutura de Dados

### Modelo Entidade-Relacionamento

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│     User     │       │  Consultation    │       │    Image     │
│──────────────│       │──────────────────│       │──────────────│
│ id (PK)      │       │ id (PK)          │       │ id (PK)      │
│ nome         │ 1───N │ medico_id (FK)   │ 1───N │ consult_id   │
│ email (UK)   │       │ patient_id (FK)  │       │ filePath     │
│ password     │       │ aiDiagnosis      │       │ fileName     │
│ cpf          │       │ confidence       │       │ fileSize     │
│ crm          │       │ multClass        │       │ contentType  │
│ dataNasc     │       │ multClassConf    │       │ aiDiagnosis  │
│ role         │       │ finalDiagnosis*  │       │ confidence   │
│ createdAt    │       │ confirmed*       │       │ multClass    │
└──────────────┘       │ createdAt        │       │ multClassConf│
                       └────────┬─────────┘       │ finalDiag    │
                                │                  │ confirmed    │
                       ┌────────┴─────────┐       │ createdAt    │
                       │    Patient       │       └──────────────┘
                       │──────────────────│
                       │ id (PK)          │
                       │ nome             │
                       │ cpf (UK)         │
                       │ sexo             │
                       │ dataNascimento   │
                       │ createdAt        │
                       └──────────────────┘

  * Campos legados — confirmação real é feita por Image
  PK = Primary Key | FK = Foreign Key | UK = Unique Key
```

### Relacionamentos

| Relação | Tipo | Descrição |
|---|---|---|
| User → Consultation | 1:N | Um médico pode ter muitas consultas |
| Patient → Consultation | 1:N | Um paciente pode ter muitas consultas |
| Consultation → Image | 1:N | Uma consulta pode ter até 10 imagens |

### Enums

| Enum | Valores |
|---|---|
| **User.Role** | `ADMIN`, `MEDICO` |
| **Patient.Sexo** | `MASCULINO`, `FEMININO`, `OUTRO` |
| **Diagnosis** | `Normal`, `aom`, `csom`, `earwax`, `ExternalEarInfections`, `tympanoskleros` |

---

## 🔄 Fluxos Principais

### Fluxo 1 — Cadastro e Login

```
                  Médico
                    │
          ┌─────────┴─────────┐
          ▼                   ▼
    POST /register       POST /login
    (dados pessoais)     (email + senha)
          │                   │
          ▼                   ▼
    Validação de          Verifica
    dados + email         credenciais
    único                     │
          │                   ▼
          ▼              Gera Token JWT
    Cria User            (válido 24h)
    (role: MEDICO)            │
          │                   ▼
          ▼              Retorna token
    Retorna sucesso      ao frontend
                              │
                              ▼
                         Armazena em
                         localStorage
```

### Fluxo 2 — Criação de Consulta com Diagnóstico IA

```
Médico preenche formulário (paciente + imagens)
    │
    ▼
POST /api/medico/consultations
    │
    ├── 1. Busca paciente por CPF
    │       ├── Existe? → Atualiza dados
    │       └── Não existe? → Cria novo
    │
    ├── 2. Cria registro de Consultation
    │
    └── 3. Para cada imagem (1 até 10):
            │
            ├── Salva arquivo em uploads/ (nome UUID)
            │
            ├── POST http://localhost:8081/predict
            │       │
            │       ├── Modelo Binário (ptmdNA.pt)
            │       │       └── Normal ou Anormal?
            │       │
            │       └── Se Anormal:
            │               └── Modelo Multiclasse (ptmdClsA.pt)
            │                       └── aom/csom/earwax/etc.
            │
            └── Salva resultado da IA na entidade Image
                    │
                    ▼
            Retorna consulta com diagnósticos
```

### Fluxo 3 — Confirmação de Diagnóstico (por imagem)

```
Médico abre detalhes da consulta
    │
    ▼
Visualiza cada imagem + diagnóstico da IA
    │
    ▼
Para cada imagem:
    ├── Aceita diagnóstico da IA
    │       └── PUT /images/{imageId}/confirm { diagnosis: "aom" }
    │
    └── Ou seleciona diagnóstico diferente
            └── PUT /images/{imageId}/confirm { diagnosis: "csom" }
                    │
                    ▼
            Image.finalDiagnosis = valor escolhido
            Image.confirmed = true
```

### Fluxo 4 — Geração de Backup (Administrador)

```
Admin clica em "Download Database"
    │
    ▼
GET /api/admin/backup
    │
    ├── Busca todas as imagens com confirmed = true
    │
    ├── Para cada imagem confirmada:
    │       ├── Verifica se arquivo existe em disco
    │       ├── Renomeia: {imageId}_{patientId}_{finalDiagnosis}.ext
    │       └── Adiciona à pasta dataset/ no ZIP
    │
    ├── Gera CSV com metadados
    │       └── Image ID, Patient ID, Model Prediction, Doctor Diagnosis
    │
    └── Retorna arquivo ZIP para download
```

---

## 🔗 Endpoints da API

### 🔓 Autenticação (público)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/auth/register` | Cadastro público de médico |
| `POST` | `/api/auth/login` | Login e obtenção de token JWT |

### 🩺 Consultas Médicas (requer `MEDICO` ou `ADMIN`)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/medico/consultations` | Criar nova consulta com imagens |
| `GET` | `/api/medico/consultations` | Listar consultas (filtros: nome, CPF) |
| `GET` | `/api/medico/consultations/{id}` | Detalhes de uma consulta |
| `PUT` | `/api/medico/consultations/{id}/confirm` | Confirmar diagnóstico (legado) |
| `PUT` | `/api/medico/consultations/images/{imageId}/confirm` | Confirmar diagnóstico por imagem |

### 🔧 Administração (requer `ADMIN`)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/admin/dashboard` | Estatísticas (imagens, consultas, pacientes) |
| `POST` | `/api/admin/change-password` | Alterar senha do admin |
| `GET` | `/api/admin/backup` | Download backup (ZIP) |

### 📁 Arquivos (público)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/files/by-name/{filename}` | Servir imagem por nome |

### 🤖 IA — Microsserviço Python

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/predict` | Análise de imagem com IA (YOLO) |

### 📚 Documentação interativa

| Recurso | URL |
|---|---|
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI spec** | `http://localhost:8080/v3/api-docs` |

---

## 🔐 Segurança

| Aspecto | Implementação |
|---|---|
| **Autenticação** | JWT (HS512) com expiração de 24 horas |
| **Autorização** | RBAC com roles `ADMIN` e `MEDICO` |
| **Senhas** | Hash BCrypt |
| **CSRF** | Desabilitado (API stateless) |
| **CORS** | Configurado para aceitar requisições do frontend |
| **Uploads** | Nomes UUID, validação de tipo MIME, limite de 10 MB |

### Endpoints públicos (sem autenticação)

- `/api/auth/**` — Cadastro e login
- `/api/files/**` — Servir imagens
- `/swagger-ui/**` — Documentação Swagger
- `/v3/api-docs/**` — OpenAPI spec

### Endpoints protegidos

- `/api/medico/**` — Requer role `MEDICO` ou `ADMIN`
- `/api/admin/**` — Requer role `ADMIN`

---

## 🔑 Credenciais Padrão

> ⚠️ Criadas automaticamente pelo `DataInitializer` na primeira execução do backend. **Altere em produção.**

### Administrador

| Campo | Valor |
|---|---|
| **Email** | `admin` |
| **Senha** | `admin` |
| **Role** | `ADMIN` |

### Médico (Mock de teste)

| Campo | Valor |
|---|---|
| **Email** | `medico@example.com` |
| **Senha** | `password` |
| **Nome** | `Dr. Mock Teste` |
| **CPF** | `123.456.789-00` |
| **CRM** | `CRM/SP 123456` |
| **Role** | `MEDICO` |

---

## 🌐 Infraestrutura e Portas

### Mapa de serviços

| Serviço | Porta | Tecnologia | Container Docker |
|---|---|---|---|
| **Frontend** | `3000` | React + Vite / Nginx | `ptmd-frontend-app` |
| **Backend API** | `8080` | Java Spring Boot | `ptmd-backend` |
| **Microsserviço IA** | `8081` | Python FastAPI | `ptmd-python-api-standalone` |
| **Banco de dados** | `3306` | MySQL 8.0 | `ptmd-mysql` |

### Comunicação entre serviços

```
Frontend (3000) ──HTTP──▶ Backend (8080) ──HTTP──▶ IA (8081)
                                 │
                                 └──JDBC──▶ MySQL (3306)
```

### Limites do sistema

| Parâmetro | Valor |
|---|---|
| Tamanho máximo por arquivo | 10 MB |
| Tamanho máximo por requisição | 10 MB |
| Máximo de imagens por consulta | 10 |
| Timeout de requisição à IA | 60 segundos |
| Validade do token JWT | 24 horas |

---

## 🤝 Contribuição

### Como contribuir

1. Faça um **fork** do repositório
2. Crie uma **branch** para sua funcionalidade:
   ```bash
   git checkout -b feature/minha-funcionalidade
   ```
3. Faça **commit** das alterações:
   ```bash
   git commit -m "feat: adiciona minha funcionalidade"
   ```
4. Faça **push** para a branch:
   ```bash
   git push origin feature/minha-funcionalidade
   ```
5. Abra um **Pull Request** para revisão

### Estrutura de diretórios do repositório

```
ptmd/
├── PTMD-BACK/           # Backend Java Spring Boot (API principal)
│   ├── src/             #   Código-fonte Java
│   ├── Dockerfile       #   Container Docker
│   ├── docker-compose.yml   Backend + MySQL
│   └── README.md        #   📖 Guia de instalação
│
├── PTMD-BACK-IA/        # Microsserviço Python (IA)
│   ├── controller/      #   Endpoints FastAPI
│   ├── service/         #   Lógica de IA (YOLO)
│   ├── models/          #   Modelos .pt treinados
│   ├── Dockerfile       #   Container Docker
│   ├── docker-compose.yml
│   └── README.md        #   📖 Guia de instalação
│
├── PTMD-FRONT/          # Frontend React + TypeScript
│   ├── src/             #   Código-fonte React/TS
│   ├── Dockerfile       #   Container Docker
│   ├── docker-compose.yml
│   └── README.md        #   📖 Guia de instalação
│
└── README.md            # 📖 Este arquivo (visão geral)
```

---

## 📄 Licença

**PTMD-YOLO** — Sistema de Diagnóstico Médico com Inteligência Artificial

**Autor:** [JairAssisDev](https://github.com/JairAssisDev)

**Repositório:** [https://github.com/JairAssisDev/ptmd](https://github.com/JairAssisDev/ptmd)
