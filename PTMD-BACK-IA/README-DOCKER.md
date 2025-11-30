# 🐳 Docker - PTMD Python API (IA)

Este guia explica como executar apenas a API Python de IA usando Docker Compose.

## 🚀 Inicialização Rápida

### 1. Construir e iniciar

```bash
cd PTMD-YOLO-API
docker-compose up --build
```

### 2. Iniciar em background

```bash
docker-compose up -d --build
```

### 3. Ver logs

```bash
docker-compose logs -f python-api
```

### 4. Parar o serviço

```bash
docker-compose down
```

## 📡 Acessos

- **Python API:** http://localhost:8081
- **Documentação:** http://localhost:8081/docs

## 🔧 Configuração

### Volumes

O diretório `models` é montado como volume para persistir os modelos de IA:

```yaml
volumes:
  - ./models:/app/models
```

Certifique-se de que os modelos (`ptmdNA.pt` e `ptmdClsA.pt`) estão no diretório `models/`.

## 📝 Endpoints

### POST /predict

Upload de imagem para diagnóstico:

```bash
curl -X POST http://localhost:8081/predict \
  -F "file=@imagem.jpg"
```

## 🔍 Comandos Úteis

### Ver status

```bash
docker-compose ps
```

### Entrar no container

```bash
docker-compose exec python-api sh
```

### Reconstruir

```bash
docker-compose up -d --build python-api
```

### Ver logs em tempo real

```bash
docker-compose logs -f
```

### Limpar

```bash
docker-compose down
```

## ⚠️ Notas Importantes

1. **Modelos:** Os modelos de IA devem estar no diretório `models/`
2. **Porta:** A API roda na porta 8081
3. **Dependências:** O container instala todas as dependências do `requirements.txt`
4. **OpenCV:** Requer bibliotecas gráficas (já configuradas no Dockerfile)

