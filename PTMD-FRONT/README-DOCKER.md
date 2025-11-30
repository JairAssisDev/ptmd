# 🐳 Docker - PTMD Frontend

Este guia explica como executar apenas o Frontend React usando Docker Compose.

## 🚀 Inicialização Rápida

### 1. Construir e iniciar

```bash
cd PTMD-FRONT
docker-compose up --build
```

### 2. Iniciar em background

```bash
docker-compose up -d --build
```

### 3. Ver logs

```bash
docker-compose logs -f frontend
```

### 4. Parar o serviço

```bash
docker-compose down
```

## 📡 Acessos

- **Frontend:** http://localhost:3000

## 🔧 Configuração

### Variáveis de Ambiente

A URL da API pode ser alterada no `docker-compose.yml`:

```yaml
environment:
  - VITE_API_URL=http://host.docker.internal:8080/api
```

**Nota:** `VITE_API_URL` aponta para `host.docker.internal:8080` para acessar o backend Java que pode estar rodando fora do Docker.

### Para desenvolvimento local

Se o backend estiver rodando localmente (não Docker), use:

```yaml
environment:
  - VITE_API_URL=http://localhost:8080/api
```

## 🔍 Comandos Úteis

### Ver status

```bash
docker-compose ps
```

### Entrar no container

```bash
docker-compose exec frontend sh
```

### Reconstruir

```bash
docker-compose up -d --build frontend
```

### Ver logs em tempo real

```bash
docker-compose logs -f
```

## 📝 Notas

- O frontend usa Nginx para servir os arquivos estáticos
- As requisições `/api` são redirecionadas para o backend via proxy (configurado no nginx.conf)
- O build é feito durante a construção da imagem Docker

