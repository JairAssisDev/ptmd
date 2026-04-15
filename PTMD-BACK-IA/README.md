# 🤖 PTMD Backend IA — Microsserviço de Diagnóstico com IA

> Microsserviço Python responsável pela **análise de imagens otológicas** utilizando modelos YOLO para classificação binária (Normal/Anormal) e multiclasse de condições do ouvido.

---

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Guia de Instalação](#-guia-de-instalação)
  - [Instalação Local](#opção-1--instalação-local)
  - [Instalação com Docker](#opção-2--instalação-com-docker)
- [Modelos de IA](#-modelos-de-ia)
- [Endpoint da API](#-endpoint-da-api)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Integração com o Backend Java](#-integração-com-o-backend-java)
- [Troubleshooting](#-troubleshooting)

---

## 🔍 Visão Geral

O PTMD-BACK-IA é um microsserviço que recebe imagens de exames otológicos e retorna um diagnóstico automático utilizando modelos de deep learning YOLO. O processamento ocorre em duas etapas:

1. **Classificação Binária:** Determina se a imagem é **Normal** ou **Anormal**
2. **Classificação Multiclasse:** Se anormal, identifica a condição específica entre 5 possíveis diagnósticos

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

## 🛠 Tecnologias

| Tecnologia | Versão | Descrição |
|---|---|---|
| **Python** | 3.11+ | Linguagem principal |
| **FastAPI** | latest | Framework web assíncrono |
| **Uvicorn** | latest | Servidor ASGI |
| **Ultralytics (YOLO)** | latest | Framework de modelos de IA |
| **Pydantic** | latest | Validação de dados |
| **Pillow (PIL)** | — | Processamento de imagens |
| **NumPy** | — | Operações com arrays |
| **Docker** | — | Containerização |

---

## ✅ Pré-requisitos

### Para instalação local

- [Python 3.11+](https://www.python.org/downloads/) instalado e configurado no `PATH`
- [pip](https://pip.pypa.io/en/stable/) (geralmente incluído com Python)
- Modelos YOLO treinados na pasta `models/` (arquivos `.pt`)

### Para instalação com Docker

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando

### Verificando os pré-requisitos

```bash
# Verificar Python
python --version
# Esperado: Python 3.11.x ou superior

# Verificar pip
pip --version

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
cd ptmd/PTMD-BACK-IA
```

#### Passo 2: Criar ambiente virtual (recomendado)

```bash
# Criar venv
python -m venv venv

# Ativar venv (Windows)
venv\Scripts\activate

# Ativar venv (Linux/Mac)
source venv/bin/activate
```

#### Passo 3: Instalar dependências

```bash
pip install --upgrade pip
pip install -r requirements.txt
```

> ⚠️ **Nota:** A biblioteca `ultralytics` (YOLO) pode demorar para instalar, pois inclui dependências como PyTorch. O download pode ser grande (~2 GB dependendo da plataforma).

#### Passo 4: Verificar os modelos de IA

Certifique-se de que os modelos YOLO estejam na pasta `models/`:

```
PTMD-BACK-IA/
└── models/
    ├── ptmdNA.pt          # Modelo binário (Normal/Anormal)
    ├── ptmdNA1.pt         # Modelo binário (backup)
    ├── ptmdClsA.pt        # Modelo multiclasse
    └── ptmdClsA1.pt       # Modelo multiclasse (backup)
```

> 💡 **Importante:** Os modelos `.pt` são arquivos binários grandes e devem estar presentes antes de iniciar a aplicação. Eles são carregados na memória durante a inicialização.

#### Passo 5: Executar a aplicação

```bash
# Opção 1: Executar diretamente
python main.py

# Opção 2: Executar com uvicorn (recomendado)
uvicorn main:app --host 0.0.0.0 --port 8081
```

✅ **O microsserviço estará disponível em:** `http://localhost:8081`

#### Passo 6: Testar o endpoint

```bash
# Testar com uma imagem
curl -X POST "http://localhost:8081/predict" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@caminho/para/imagem.jpg"
```

---

### Opção 2 — Instalação com Docker

#### Passo 1: Clonar o repositório

```bash
git clone https://github.com/JairAssisDev/ptmd.git
cd ptmd/PTMD-BACK-IA
```

#### Passo 2: Verificar os modelos

Certifique-se de que a pasta `models/` contém os arquivos `.pt`:

```bash
ls models/
# Deve listar: ptmdNA.pt, ptmdNA1.pt, ptmdClsA.pt, ptmdClsA1.pt
```

#### Passo 3: Subir o container

```bash
docker compose up --build -d
```

Isso irá criar e iniciar:

| Container | Porta | Descrição |
|---|---|---|
| `ptmd-python-api-standalone` | `8081` | Microsserviço de IA |

#### Passo 4: Verificar se o container está rodando

```bash
docker compose ps
```

#### Passo 5: Acompanhar os logs

```bash
docker logs -f ptmd-python-api-standalone
```

✅ **O microsserviço estará disponível em:** `http://localhost:8081`

#### Parar o container

```bash
docker compose down
```

---

## 🧠 Modelos de IA

O sistema utiliza dois modelos YOLO treinados com o framework [Ultralytics](https://docs.ultralytics.com/):

### Modelo Binário (`ptmdNA.pt`)

- **Objetivo:** Classificar a imagem como **Normal** ou **Anormal**
- **Classes:** `["Normal", "Anormal"]`
- **Saída:** Classe predita + probabilidade

### Modelo Multiclasse (`ptmdClsA.pt`)

- **Objetivo:** Identificar a condição específica quando a imagem é **Anormal**
- **Classes:** `["aom", "csom", "earwax", "ExternalEarInfections", "tympanoskleros"]`
- **Saída:** Classe predita + probabilidade

### Fluxo de processamento

```
Imagem recebida
    │
    ▼
┌──────────────────┐
│  Modelo Binário  │  ptmdNA.pt
│  Normal/Anormal  │
└────────┬─────────┘
         │
    ┌────┴────┐
    │         │
 Normal    Anormal
    │         │
    ▼         ▼
 Retorna  ┌──────────────────┐
 resultado│ Modelo Multiclasse│  ptmdClsA.pt
          │  Classificação    │
          └────────┬──────────┘
                   │
                   ▼
              Retorna resultado
              com classe + prob
```

---

## 🔗 Endpoint da API

### `POST /predict`

Recebe uma imagem e retorna o diagnóstico da IA.

**Request:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `file` | `UploadFile` (multipart/form-data) | Imagem do exame (JPG, PNG) |

**Response (Normal):**

```json
{
  "predictions": [
    {
      "class": "Normal",
      "Probabilidade": 0.9523,
      "MultClass": "",
      "ProbabilidadeMultClass": ""
    }
  ]
}
```

**Response (Anormal):**

```json
{
  "predictions": [
    {
      "Class": "Anormal",
      "Probabilidade": 0.8765,
      "MultClass": "aom",
      "ProbabilidadeMultClass": 0.8234
    }
  ]
}
```

> ⚠️ **Nota sobre inconsistência:** Quando o resultado é **Normal**, o campo usa `"class"` (minúsculo). Quando é **Anormal**, usa `"Class"` (maiúsculo). O backend Java trata essa inconsistência via DTO com ambos os campos.

**Códigos de resposta:**

| Código | Descrição |
|---|---|
| `200` | Diagnóstico retornado com sucesso |
| `400` | Arquivo enviado não é uma imagem |
| `500` | Erro interno no processamento |

---

## 📁 Estrutura do Projeto

```
PTMD-BACK-IA/
├── controller/
│   └── controller_predict.py   # Endpoint REST /predict
├── service/
│   └── predict.py              # Lógica de IA (carrega modelos YOLO)
├── models/
│   ├── ptmdNA.pt               # Modelo binário (Normal/Anormal)
│   ├── ptmdNA1.pt              # Modelo binário (backup)
│   ├── ptmdClsA.pt             # Modelo multiclasse
│   └── ptmdClsA1.pt            # Modelo multiclasse (backup)
├── main.py                     # Entry point FastAPI
├── requirements.txt            # Dependências Python
├── Dockerfile                  # Build do container
└── docker-compose.yml          # Orquestração standalone
```

---

## 🔌 Integração com o Backend Java

O backend Java (PTMD-BACK) se comunica com este microsserviço via HTTP:

| Configuração | Valor (Local) | Valor (Docker) |
|---|---|---|
| **URL** | `http://localhost:8081/predict` | `http://python-api:8081/predict` |
| **Método** | `POST` | `POST` |
| **Content-Type** | `multipart/form-data` | `multipart/form-data` |
| **Timeout** | 60 segundos | 60 segundos |

### Comunicação

```
┌──────────────┐    POST /predict     ┌──────────────┐
│  PTMD-BACK   │ ──────────────────▶  │  PTMD-BACK-IA│
│  (Java:8080) │    multipart/form    │ (Python:8081) │
│              │ ◀──────────────────  │              │
└──────────────┘    JSON response     └──────────────┘
```

---

## 🐛 Troubleshooting

### Erro: Modelo YOLO não encontrado

```
Failed to load YOLO model
```

**Soluções:**
1. Verifique se os arquivos `.pt` estão na pasta `models/`
2. Confirme os nomes: `ptmdNA.pt` e `ptmdClsA.pt`
3. No Docker, verifique se o volume está montado: `./models:/app/models`

### Erro: Dependência OpenCV não encontrada

```
ImportError: libGL.so.1: cannot open shared object file
```

**Soluções:**
1. Instalar dependências do sistema:
   ```bash
   # Ubuntu/Debian
   sudo apt-get install libgl1 libglib2.0-0

   # O Dockerfile já inclui essas dependências
   ```

### Erro: Porta 8081 já em uso

```
Address already in use
```

**Soluções:**
1. Verificar o que está usando a porta:
   ```bash
   # Windows
   netstat -ano | findstr :8081

   # Linux
   lsof -i :8081
   ```
2. Encerrar o processo ou usar outra porta:
   ```bash
   uvicorn main:app --host 0.0.0.0 --port 8082
   ```

### Erro: Memória insuficiente

Os modelos YOLO consomem memória significativa (~200 MB cada). Se houver problemas de memória:
1. Feche outros programas
2. Certifique-se de ter pelo menos 2 GB de RAM disponível
3. No Docker, aumente o limite de memória nas configurações do Docker Desktop

### Docker: Container não inicia

```bash
# Ver logs detalhados
docker logs ptmd-python-api-standalone

# Recriar do zero
docker compose down
docker compose up --build -d
```

---

## 📄 Licença

Este projeto faz parte do sistema **PTMD-YOLO** — Sistema de Diagnóstico Médico com IA.

**Repositório:** [https://github.com/JairAssisDev/ptmd](https://github.com/JairAssisDev/ptmd)
