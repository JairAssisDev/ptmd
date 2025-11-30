# PTMD Frontend

Frontend React + TypeScript para o Sistema de Diagnóstico Médico PTMD-YOLO.

## 🚀 Tecnologias

- **React 18** - Biblioteca UI
- **TypeScript** - Tipagem estática
- **Material-UI (MUI)** - Componentes de interface
- **React Router** - Roteamento
- **Axios** - Cliente HTTP
- **Vite** - Build tool
- **React Dropzone** - Upload de arquivos

## 📋 Pré-requisitos

- Node.js 18+ e npm/yarn
- Backend Java rodando em `http://localhost:8080`

## 🛠️ Instalação e Execução

### Desenvolvimento Local

```bash
# Instalar dependências
npm install

# Criar arquivo .env
cp .env.example .env

# Iniciar servidor de desenvolvimento
npm run dev
```

A aplicação estará disponível em `http://localhost:3000`

### Build para Produção

```bash
npm run build
```

### Docker

```bash
# Build e iniciar
docker-compose up --build frontend

# Ou iniciar tudo junto
docker-compose up --build
```

## 📁 Estrutura do Projeto

```
PTMD-FRONT/
├── src/
│   ├── components/      # Componentes reutilizáveis
│   ├── contexts/        # Context API (Auth)
│   ├── pages/           # Páginas da aplicação
│   ├── services/        # Serviços de API
│   ├── App.tsx          # Componente principal
│   └── main.tsx         # Entry point
├── public/              # Arquivos estáticos
├── Dockerfile           # Docker para produção
└── nginx.conf           # Configuração Nginx
```

## 🎯 Funcionalidades

### Autenticação
- ✅ Login de administrador e médico
- ✅ Registro público de médicos
- ✅ Gerenciamento de sessão com JWT
- ✅ Proteção de rotas por role

### Dashboard Administrativo
- ✅ Estatísticas do sistema (imagens, consultas, pacientes)
- ✅ Download de backup de imagens (ZIP)
- ✅ Alteração de senha

### Dashboard Médico
- ✅ Criar nova consulta com upload de imagem
- ✅ Visualizar histórico de consultas
- ✅ Confirmar diagnóstico da IA
- ✅ Editar diagnóstico final

## 🔐 Credenciais Mock

Consulte o arquivo `CREDENCIAIS_MOCK.md` na raiz do projeto para ver as credenciais de teste.

## 🌐 Rotas

- `/login` - Página de login
- `/register` - Cadastro de médico
- `/admin` - Dashboard administrativo (requer role ADMIN)
- `/medico` - Dashboard médico (requer role MEDICO)

## 📝 Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
VITE_API_URL=http://localhost:8080/api
```

## 🐳 Docker

O frontend está configurado para rodar em Docker com Nginx:

- **Porta:** 3000 (mapeada para 80 no container)
- **Proxy:** Requisições `/api` são redirecionadas para o backend Java

## 🎨 Interface

A interface utiliza Material-UI com tema customizado, oferecendo:
- Design moderno e responsivo
- Drag & drop para upload de imagens
- Tabelas interativas
- Diálogos modais
- Feedback visual (alerts, loading states)

## 🔧 Desenvolvimento

### Adicionar Nova Página

1. Criar componente em `src/pages/`
2. Adicionar rota em `src/App.tsx`
3. Criar serviço em `src/services/` se necessário

### Adicionar Novo Serviço

1. Criar arquivo em `src/services/`
2. Usar `api` do `api.ts` para requisições
3. O token JWT é adicionado automaticamente

## 📦 Build

```bash
npm run build
```

Os arquivos compilados estarão em `dist/`

## 🐛 Troubleshooting

### Erro: "Cannot connect to API"
- Verifique se o backend está rodando
- Confirme a URL no `.env`
- Verifique CORS no backend

### Erro: "401 Unauthorized"
- Faça login novamente
- Verifique se o token está sendo enviado
- Token pode ter expirado (24h)

## 📄 Licença

Este projeto faz parte do sistema PTMD-YOLO.

