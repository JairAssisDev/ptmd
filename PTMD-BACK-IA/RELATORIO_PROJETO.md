# Relatório Completo do Projeto PTMD-YOLO-API

## 📋 Visão Geral

O **PTMD-YOLO-API** é uma API REST desenvolvida em Python utilizando o framework FastAPI para diagnóstico médico automatizado de condições do ouvido através de análise de imagens. O sistema utiliza modelos de aprendizado de máquina baseados em YOLO (You Only Look Once) da biblioteca Ultralytics para classificação de imagens médicas.

## 🎯 Objetivo do Projeto

O projeto tem como objetivo fornecer uma solução automatizada para:
- Classificação binária de imagens do ouvido (Normal vs Anormal)
- Classificação multiclasse quando a imagem é identificada como anormal, identificando condições específicas:
  - AOM (Otite Média Aguda)
  - CSOM (Otite Média Crônica Supurativa)
  - Earwax (Cerúmen)
  - ExternalEarInfections (Infecções do Ouvido Externo)
  - Tympanoskleros (Timpanoesclerose)

## 🏗️ Arquitetura do Projeto

### Estrutura de Diretórios

```
PTMD-YOLO-API/
├── controller/
│   └── controller_predict.py    # Controlador de rotas para predições
├── service/
│   └── predict.py                # Serviço de lógica de negócio para diagnóstico
├── models/                       # Modelos de ML treinados
│   ├── ptmdNA.pt                # Modelo de classificação binária (Normal/Anormal)
│   ├── ptmdNA1.pt               # Modelo alternativo de classificação binária
│   ├── ptmdClsA.pt              # Modelo de classificação multiclasse
│   └── ptmdClsA1.pt             # Modelo alternativo de classificação multiclasse
├── main.py                       # Ponto de entrada da aplicação
├── requirements.txt              # Dependências do projeto
├── Dockerfile                    # Configuração de container Docker
└── docker-compose.yml            # Orquestração de containers
```

### Padrão Arquitetural

O projeto segue uma arquitetura em camadas (layered architecture):

1. **Camada de Apresentação**: `main.py` - Configuração do FastAPI e middlewares
2. **Camada de Controle**: `controller/controller_predict.py` - Rotas e validação de entrada
3. **Camada de Serviço**: `service/predict.py` - Lógica de negócio e integração com modelos ML
4. **Camada de Dados**: `models/` - Modelos de machine learning pré-treinados

## 🔧 Tecnologias e Dependências

### Stack Tecnológico Principal

- **Python 3.11**: Linguagem de programação base
- **FastAPI**: Framework web moderno e de alta performance para criação de APIs REST
- **Uvicorn**: Servidor ASGI para execução da aplicação FastAPI
- **Ultralytics YOLO**: Biblioteca para modelos de detecção e classificação de objetos/imagens
- **Pillow (PIL)**: Processamento de imagens
- **NumPy**: Operações matemáticas em arrays
- **Pydantic**: Validação de dados

### Dependências Detalhadas

```txt
fastapi                # Framework web
uvicorn                # Servidor ASGI
ultralytics            # Biblioteca YOLO
pydantic               # Validação de dados
fastapi[standard]      # Extensões padrão do FastAPI
```

## 📝 Componentes Principais

### 1. Main Application (`main.py`)

**Responsabilidades:**
- Inicialização da aplicação FastAPI
- Configuração de CORS (Cross-Origin Resource Sharing) para permitir requisições de qualquer origem
- Registro de rotas do controlador
- Configuração do servidor Uvicorn na porta 8081

**Características:**
- CORS configurado com permissões amplas (`allow_origins=["*"]`)
- Host configurado para `0.0.0.0` permitindo acesso externo
- Porta padrão: **8081**

### 2. Controlador de Predições (`controller/controller_predict.py`)

**Endpoint Principal:**
- **Rota**: `POST /predict`
- **Parâmetros**: Upload de arquivo de imagem
- **Validações**:
  - Verifica se o arquivo é uma imagem (`content_type.startswith('image/')`)
  - Tratamento de exceções com respostas HTTP apropriadas

**Fluxo de Processamento:**
1. Recebe arquivo de imagem via upload
2. Valida tipo de arquivo
3. Converte imagem para array NumPy
4. Chama o serviço de diagnóstico
5. Retorna predições em formato JSON

### 3. Serviço de Diagnóstico (`service/predict.py`)

**Modelos Carregados:**
- `model`: YOLO model de classificação binária (`ptmdNA.pt`)
- `modelMulti`: YOLO model de classificação multiclasse (`ptmdClsA.pt`)

**Algoritmo de Diagnóstico:**

1. **Fase 1 - Classificação Binária:**
   - Classifica a imagem como "Normal" ou "Anormal"
   - Retorna classe predita e probabilidade

2. **Fase 2 - Classificação Multiclasse (condicional):**
   - Executada apenas se a imagem for classificada como "Anormal"
   - Identifica a condição específica entre 5 possibilidades:
     - `aom`: Otite Média Aguda
     - `csom`: Otite Média Crônica Supurativa
     - `earwax`: Cerúmen
     - `ExternalEarInfections`: Infecções do Ouvido Externo
     - `tympanoskleros`: Timpanoesclerose

**Formato de Resposta:**

**Caso Normal:**
```json
{
  "predictions": [{
    "class": "Normal",
    "Probabilidade": 0.9542,
    "MultClass": "",
    "ProbabilidadeMultClass": ""
  }]
}
```

**Caso Anormal:**
```json
{
  "predictions": [{
    "Class": "Anormal",
    "Probabilidade": 0.8765,
    "MultClass": "aom",
    "ProbabilidadeMultClass": 0.8234
  }]
}
```

### 4. Modelos de Machine Learning

O projeto utiliza 4 modelos YOLO pré-treinados:

| Modelo | Tipo | Descrição |
|--------|------|-----------|
| `ptmdNA.pt` | Binário | Classificação Normal/Anormal (modelo principal) |
| `ptmdNA1.pt` | Binário | Modelo alternativo de classificação binária |
| `ptmdClsA.pt` | Multiclasse | Classificação de condições específicas (modelo principal) |
| `ptmdClsA1.pt` | Multiclasse | Modelo alternativo de classificação multiclasse |

**Nota**: Atualmente, apenas `ptmdNA.pt` e `ptmdClsA.pt` estão em uso ativo no código.

## 🐳 Containerização

### Dockerfile

**Base Image:** `python:3.11-slim`

**Dependências do Sistema:**
- `libgl1-mesa-glx`: Necessário para OpenCV (processamento de imagens)
- `libglib2.0-0`: Biblioteca base do sistema

**Configurações:**
- Diretório de trabalho: `/app`
- Porta exposta: `8081`
- Comando padrão: Execução do Uvicorn

### Docker Compose

**Configuração:**
- Serviço: `api`
- Portas mapeadas: `8081:8081`
- Volumes: Montagem do diretório atual para desenvolvimento (`.:/app`)

**Uso:**
```bash
docker-compose up --build
```

## 🌐 API Endpoints

### POST /predict

**Descrição:** Endpoint para realizar diagnóstico de imagens do ouvido

**Request:**
- **Method**: POST
- **Content-Type**: multipart/form-data
- **Body**: Arquivo de imagem (campo `file`)

**Response Success (200):**
```json
{
  "predictions": [
    {
      "class": "Normal",
      "Probabilidade": 0.9542,
      "MultClass": "",
      "ProbabilidadeMultClass": ""
    }
  ]
}
```

**Response Error (400):**
```json
{
  "detail": "File provided is not an image"
}
```

**Response Error (500):**
```json
{
  "detail": "Mensagem de erro específica"
}
```

## 🔐 Segurança e Configurações

### CORS (Cross-Origin Resource Sharing)

**Configuração Atual:**
- `allow_origins=["*"]`: Permite requisições de qualquer origem
- `allow_credentials=True`: Permite envio de credenciais
- `allow_methods=["*"]`: Permite todos os métodos HTTP
- `allow_headers=["*"]`: Permite todos os cabeçalhos

**⚠️ Considerações de Segurança:**
- A configuração atual é muito permissiva para produção
- Recomenda-se restringir `allow_origins` para domínios específicos em ambiente de produção

### Tratamento de Erros

O projeto implementa tratamento de erros em múltiplos níveis:
- Validação de tipo de arquivo no controlador
- Tratamento de exceções genéricas com logging
- Respostas HTTP apropriadas (400, 500)

## 📊 Fluxo de Processamento

```
1. Cliente → POST /predict (upload de imagem)
           ↓
2. Controlador valida tipo de arquivo
           ↓
3. Imagem convertida para array NumPy
           ↓
4. Serviço carrega modelo binário (ptmdNA.pt)
           ↓
5. Classificação Normal/Anormal
           ↓
6a. Se Normal → Retorna resultado
    ↓
6b. Se Anormal → Carrega modelo multiclasse (ptmdClsA.pt)
           ↓
7. Classificação de condição específica
           ↓
8. Retorna resultado completo
```

## 🚀 Como Executar

### Execução Local (sem Docker)

```bash
# Instalar dependências
pip install -r requirements.txt

# Executar aplicação
python main.py
```

### Execução com Docker

```bash
# Construir e executar container
docker-compose up --build

# Executar em background
docker-compose up -d
```

### Execução Direta com Uvicorn

```bash
uvicorn main:app --host 0.0.0.0 --port 8081
```

## 📈 Métricas e Performance

### Modelos de ML

- **Tipo**: YOLO (Classification)
- **Framework**: Ultralytics
- **Formato**: PyTorch (.pt)

### Processamento

- **Entrada**: Imagens em formato RGB
- **Saída**: Probabilidades e classes preditas
- **Processamento**: Assíncrono (async/await)

## 🔍 Logging

O projeto utiliza logging nativo do Python para:
- Registro de erros ao carregar modelos
- Registro de erros durante o diagnóstico
- Debugging e monitoramento

## 🎯 Casos de Uso

1. **Diagnóstico Automatizado**: Classificação automática de imagens do ouvido
2. **Triagem Médica**: Identificação rápida de condições anormais
3. **Análise em Lote**: Processamento de múltiplas imagens via API
4. **Integração com Sistemas**: API REST para integração com outras aplicações

## 📋 Requisitos do Sistema

### Software
- Python 3.11+
- Docker e Docker Compose (opcional)

### Hardware
- Espaço para modelos (~tamanho dos arquivos .pt)
- RAM suficiente para carregar modelos YOLO
- GPU recomendada para processamento mais rápido (opcional)

### Dependências do Sistema (para Docker)
- libgl1-mesa-glx
- libglib2.0-0

## 🔄 Possíveis Melhorias

1. **Segurança**:
   - Restringir CORS para origens específicas
   - Implementar autenticação/autorização
   - Validação de tamanho de arquivo

2. **Performance**:
   - Cache de modelos carregados (já implementado)
   - Processamento em lote
   - Suporte a GPU

3. **Funcionalidades**:
   - Endpoint de health check
   - Documentação automática (Swagger/OpenAPI já disponível via FastAPI)
   - Versionamento de API
   - Histórico de predições

4. **Qualidade de Código**:
   - Testes unitários
   - Testes de integração
   - Validação de entrada mais robusta

5. **Monitoramento**:
   - Métricas de performance
   - Logging estruturado
   - Alertas de erro

## 📚 Documentação Adicional

### Documentação Automática do FastAPI

Quando a aplicação estiver rodando, acesse:
- **Swagger UI**: `http://localhost:8081/docs`
- **ReDoc**: `http://localhost:8081/redoc`
- **OpenAPI JSON**: `http://localhost:8081/openapi.json`

## 👤 Autor

- **Desenvolvedor**: jairvictor (conforme Dockerfile)

## 📝 Notas Finais

Este projeto representa uma solução completa para diagnóstico médico automatizado utilizando inteligência artificial. A arquitetura modular facilita manutenção e extensão, enquanto a containerização permite fácil deploy em diferentes ambientes.

O sistema está pronto para uso, mas recomenda-se implementar melhorias de segurança e monitoramento antes de deploy em produção.

---

**Data de Geração do Relatório**: $(date)
**Versão do Projeto**: 1.0.0

