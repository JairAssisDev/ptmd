# 🖥 PTMD Frontend

> Interface web **React + TypeScript** para o Sistema de Diagnóstico Médico **PTMD-YOLO**, com Material-UI e Vite.

---

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Guia de Instalação](#-guia-de-instalação)
  - [Instalação Local](#opção-1--instalação-local)
  - [Instalação com Docker](#opção-2--instalação-com-docker)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Funcionalidades](#-funcionalidades)
- [Rotas da Aplicação](#-rotas-da-aplicação)
- [Credenciais de Teste](#-credenciais-de-teste)
- [Desenvolvimento](#-desenvolvimento)
- [Build para Produção](#-build-para-produção)
- [Troubleshooting](#-troubleshooting)

---

## 🔍 Visão Geral

O PTMD Frontend é a interface web do sistema de diagnóstico médico assistido por IA. Ele oferece:

- **Autenticação** com login/cadastro e gerenciamento de sessão JWT
- **Dashboard Administrativo** com estatísticas e backup de dados
- **Dashboard Médico** para criar consultas, fazer upload de imagens e confirmar diagnósticos
- **Interface moderna e responsiva** com Material-UI
- **Upload drag & drop** de imagens otológicas

---

## 🛠 Tecnologias

| Tecnologia | Versão | Descrição |
|---|---|---|
| **React** | 18.2 | Biblioteca UI principal |
| **TypeScript** | 5.2+ | Tipagem estática |
| **Material-UI (MUI)** | 5.15 | Componentes de interface |
| **React Router** | 6.20 | Roteamento SPA |
| **Axios** | 1.6+ | Cliente HTTP |
| **Vite** | 5.0+ | Build tool e dev server |
| **React Dropzone** | 14.2 | Upload de arquivos com drag & drop |
| **date-fns** | 2.30 | Formatação de datas |
| **Nginx** | latest | Servidor web (produção/Docker) |

---

## ✅ Pré-requisitos

### Para instalação local

- [Node.js 18+](https://nodejs.org/) instalado e configurado no `PATH`
- [npm](https://www.npmjs.com/) (incluído com Node.js) ou [Yarn](https://yarnpkg.com/)
- Backend Java (PTMD-BACK) rodando em `http://localhost:8080`

### Para instalação com Docker

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando

### Verificando os pré-requisitos

```bash
# Verificar Node.js
node --version
# Esperado: v18.x.x ou superior

# Verificar npm
npm --version
# Esperado: 9.x.x ou superior

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
cd ptmd/PTMD-FRONT
```

#### Passo 2: Instalar dependências

```bash
npm install
```

> 💡 **Nota:** Isso instalará todas as dependências listadas no `package.json`, incluindo React, MUI, Axios, etc.

#### Passo 3: Configurar variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```bash
# Opção 1: Copiar o arquivo de exemplo (se existir)
cp .env.example .env

# Opção 2: Criar manualmente
```

Conteúdo do `.env`:

```env
VITE_API_URL=http://localhost:8080/api
```

> ⚠️ **Nota:** No modo de desenvolvimento local, o Vite já possui um **proxy** configurado que redireciona requisições `/api` para `http://localhost:8080`. Portanto, a variável `.env` pode ser desnecessária para desenvolvimento local.

#### Passo 4: Iniciar o servidor de desenvolvimento

```bash
npm run dev
```

✅ **A aplicação estará disponível em:** `http://localhost:3000`

O Vite suporta **Hot Module Replacement (HMR)**, então alterações no código serão refletidas automaticamente no navegador.

---

### Opção 2 — Instalação com Docker

#### Passo 1: Clonar o repositório

```bash
git clone https://github.com/JairAssisDev/ptmd.git
cd ptmd/PTMD-FRONT
```

#### Passo 2: Subir o container

```bash
docker compose up --build -d
```

Isso irá criar e iniciar:

| Container | Porta | Descrição |
|---|---|---|
| `ptmd-frontend-app` | `3000` → `80` | Frontend React via Nginx |

#### Passo 3: Verificar se o container está rodando

```bash
docker compose ps
```

#### Passo 4: Acompanhar os logs

```bash
docker logs -f ptmd-frontend-app
```

✅ **A aplicação estará disponível em:** `http://localhost:3000`

#### Configuração do Docker

No modo Docker standalone, o frontend espera que o backend esteja acessível em `http://127.0.0.1:8080/api`. A variável de ambiente no `docker-compose.yml`:

```yaml
environment:
  - VITE_API_URL=http://127.0.0.1:8080/api
```

#### Parar o container

```bash
docker compose down
```

---

## 🌍 Variáveis de Ambiente

| Variável | Descrição | Padrão |
|---|---|---|
| `VITE_API_URL` | URL base da API backend | `http://localhost:8080/api` |

### Proxy do Vite (Desenvolvimento)

Em modo de desenvolvimento, o Vite está configurado para fazer proxy de requisições `/api`:

```typescript
// vite.config.ts
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
}
```

Isso significa que no desenvolvimento local, requisições como `GET /api/medico/consultations` são automaticamente redirecionadas para `http://localhost:8080/api/medico/consultations`.

---

## 📁 Estrutura do Projeto

```
PTMD-FRONT/
├── src/
│   ├── components/          # Componentes reutilizáveis
│   ├── contexts/            # Context API (AuthContext)
│   ├── pages/               # Páginas da aplicação
│   │   ├── Login.tsx        # Tela de login
│   │   ├── Register.tsx     # Cadastro de médico
│   │   ├── AdminDashboard.tsx   # Dashboard admin
│   │   └── MedicoDashboard.tsx  # Dashboard médico
│   ├── services/            # Serviços de API (Axios)
│   │   └── api.ts           # Instância Axios configurada
│   ├── types/               # Tipos TypeScript
│   ├── utils/               # Utilitários
│   ├── App.tsx              # Componente principal + rotas
│   └── main.tsx             # Entry point
├── public/                  # Arquivos estáticos
├── index.html               # HTML template
├── package.json             # Dependências npm
├── tsconfig.json            # Configuração TypeScript
├── vite.config.ts           # Configuração Vite + proxy
├── .eslintrc.cjs            # Configuração ESLint
├── Dockerfile               # Build (sistema completo)
├── Dockerfile.standalone    # Build (standalone)
├── docker-compose.yml       # Orquestração standalone
├── nginx.conf               # Config Nginx (sistema completo)
└── nginx-standalone.conf    # Config Nginx (standalone)
```

---

## 🎯 Funcionalidades

### 🔐 Autenticação

- ✅ Login de administrador e médico
- ✅ Cadastro público de médicos
- ✅ Gerenciamento de sessão com JWT
- ✅ Proteção de rotas por role (ADMIN / MEDICO)
- ✅ Token armazenado em `localStorage`
- ✅ Redirecionamento automático com base na role

### 🔧 Dashboard Administrativo

- ✅ Estatísticas do sistema (imagens, consultas, pacientes)
- ✅ Download de backup de imagens (ZIP com dataset + CSV)
- ✅ Alteração de senha do administrador

### 🩺 Dashboard Médico

- ✅ Criar nova consulta com dados do paciente
- ✅ Upload de múltiplas imagens (até 10, drag & drop)
- ✅ Visualizar lista de consultas (com filtros: nome, CPF)
- ✅ Modal de detalhes da consulta com todas as imagens
- ✅ Confirmar diagnóstico individualmente por imagem
- ✅ Selecionar diagnóstico final via dropdown
- ✅ Visualizar diagnóstico da IA com probabilidades

---

## 🗺 Rotas da Aplicação

| Rota | Componente | Acesso | Descrição |
|---|---|---|---|
| `/login` | Login | Público | Tela de login |
| `/register` | Register | Público | Cadastro de médico |
| `/admin` | AdminDashboard | `ADMIN` | Dashboard administrativo |
| `/medico` | MedicoDashboard | `MEDICO` | Dashboard do médico |

### Proteção de rotas

- Rotas `/admin` requerem role `ADMIN`
- Rotas `/medico` requerem role `MEDICO`
- Usuários não autenticados são redirecionados para `/login`
- Após login, usuários são redirecionados para seu dashboard correspondente

---

## 🔑 Credenciais de Teste

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

> 💡 Esses usuários são criados automaticamente pelo backend na primeira execução. Você também pode cadastrar novos médicos pela tela de registro.

---

## 💻 Desenvolvimento

### Scripts disponíveis

```bash
# Servidor de desenvolvimento com HMR
npm run dev

# Build para produção
npm run build

# Preview do build de produção
npm run preview

# Lint do código
npm run lint
```

### Adicionando uma nova página

1. Crie o componente em `src/pages/NovaPagina.tsx`
2. Adicione a rota em `src/App.tsx`
3. Se necessário, crie o serviço em `src/services/`

### Adicionando um novo serviço de API

1. Crie o arquivo em `src/services/novoServico.ts`
2. Use a instância `api` de `api.ts` para requisições
3. O token JWT é adicionado automaticamente via interceptor

### Estrutura de um serviço

```typescript
import api from './api';

export const meuServico = {
  listar: () => api.get('/rota'),
  criar: (dados: MeuTipo) => api.post('/rota', dados),
  buscar: (id: number) => api.get(`/rota/${id}`),
};
```

---

## 📦 Build para Produção

### Build local

```bash
npm run build
```

Os arquivos compilados estarão em `dist/`. Para testar:

```bash
npm run preview
```

### Build Docker

O Dockerfile utiliza multi-stage build:

1. **Stage 1 (build):** Node.js compila o projeto com `npm run build`
2. **Stage 2 (produção):** Nginx serve os arquivos estáticos de `dist/`

```bash
docker build -t ptmd-frontend .
docker run -p 3000:80 ptmd-frontend
```

---

## 🐛 Troubleshooting

### Erro: "Cannot connect to API"

```
Network Error / ERR_CONNECTION_REFUSED
```

**Soluções:**
1. Verifique se o backend (PTMD-BACK) está rodando em `http://localhost:8080`
2. Confirme a URL no arquivo `.env`: `VITE_API_URL=http://localhost:8080/api`
3. Verifique o proxy no `vite.config.ts`
4. No Docker, verifique se o backend está acessível na rede

### Erro: "401 Unauthorized"

**Soluções:**
1. Faça login novamente (o token pode ter expirado — validade: 24h)
2. Verifique se o token está sendo enviado nos headers
3. Limpe o `localStorage` e faça login novamente:
   ```javascript
   // No console do navegador
   localStorage.clear();
   ```

### Erro: "npm install" falha

**Soluções:**
1. Limpe o cache do npm:
   ```bash
   npm cache clean --force
   ```
2. Delete `node_modules` e `package-lock.json`:
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```
   No Windows (PowerShell):
   ```powershell
   Remove-Item -Recurse -Force node_modules, package-lock.json
   npm install
   ```

### Erro: Porta 3000 já em uso

**Soluções:**
1. Verificar o que está usando a porta:
   ```bash
   # Windows
   netstat -ano | findstr :3000

   # Linux
   lsof -i :3000
   ```
2. O Vite oferecerá automaticamente a próxima porta disponível (3001, 3002, etc.)

### Imagens não carregam

**Soluções:**
1. Verifique se o backend está servindo as imagens em `/api/files/by-name/{filename}`
2. Confirme que a pasta `uploads/` no backend contém os arquivos
3. Verifique o CORS no backend

### Docker: Container não inicia

```bash
# Ver logs detalhados
docker logs ptmd-frontend-app

# Recriar do zero
docker compose down
docker compose up --build -d
```

---

## 🎨 Interface

O frontend utiliza **Material-UI** com tema customizado, oferecendo:

- 🎨 Design moderno e responsivo
- 📤 Drag & drop para upload de imagens
- 📊 Tabelas interativas com dados das consultas
- 🪟 Diálogos modais para detalhes e confirmação
- ⚡ Feedback visual (alerts, loading states, spinners)
- 🔄 Atualização automática de dados após ações

---

## 📄 Licença

Este projeto faz parte do sistema **PTMD-YOLO** — Sistema de Diagnóstico Médico com IA.

**Repositório:** [https://github.com/JairAssisDev/ptmd](https://github.com/JairAssisDev/ptmd)
