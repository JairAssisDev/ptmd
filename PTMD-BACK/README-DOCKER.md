# 🐳 Docker - PTMD Backend

Este guia explica como executar apenas o Backend Java com MySQL usando Docker Compose.

## 🚀 Inicialização Rápida

### 1. Construir e iniciar

```bash
cd PTMD-BACK
docker-compose up --build
```

### 2. Iniciar em background

```bash
docker-compose up -d --build
```

### 3. Ver logs

```bash
docker-compose logs -f java-backend
```

### 4. Parar os serviços

```bash
docker-compose down
```

## 📡 Acessos

- **Backend API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **MySQL:** localhost:3306

## 🔧 Configuração

### Variáveis de Ambiente

As configurações podem ser alteradas no `docker-compose.yml`:

```yaml
environment:
  - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ptmd_db
  - SPRING_DATASOURCE_USERNAME=ptmd_user
  - SPRING_DATASOURCE_PASSWORD=ptmd_password
  - APP_AI_SERVICE_URL=http://host.docker.internal:8081
```

**Nota:** `APP_AI_SERVICE_URL` aponta para `host.docker.internal:8081` para acessar a API Python que pode estar rodando fora do Docker.

## 📝 Credenciais

- **MySQL Root:** root / root_password
- **MySQL User:** ptmd_user / ptmd_password
- **Admin:** admin / admin

## 🔍 Comandos Úteis

### Ver status

```bash
docker-compose ps
```

### Entrar no container

```bash
docker-compose exec java-backend sh
```

### Acessar MySQL

```bash
docker-compose exec mysql mysql -u ptmd_user -pptmd_password ptmd_db
```

### Reconstruir apenas o backend

```bash
docker-compose up -d --build java-backend
```

### Limpar tudo

```bash
docker-compose down -v
```

