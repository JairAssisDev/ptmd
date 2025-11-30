# PTMD Backend API

API Principal (Backend) para o sistema de diagnóstico médico "PTMD-YOLO" desenvolvida em Java Spring Boot 3.

## Tecnologias

- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Security** com JWT para autenticação
- **MySQL** (JPA/Hibernate)
- **Lombok** para redução de código
- **WebClient** para integração com microsserviço Python
- **SpringDoc OpenAPI** para documentação Swagger

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- MySQL 8.0+
- Microsserviço Python rodando em `http://localhost:8081`

## Configuração

### 1. Banco de Dados

Crie um banco de dados MySQL ou configure as credenciais no arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ptmd_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=sua_senha
```

### 2. Diretório de Uploads

O sistema criará automaticamente a pasta `uploads` na raiz do projeto para armazenar as imagens.

### 3. Microsserviço Python

Certifique-se de que o microsserviço Python está rodando em `http://localhost:8081`.

## Executando a Aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`

## 📚 Documentação Swagger/OpenAPI

A API possui documentação interativa via Swagger UI. Após iniciar a aplicação, acesse:

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### Funcionalidades do Swagger

- ✅ Documentação completa de todos os endpoints
- ✅ Teste interativo de endpoints diretamente no navegador
- ✅ Autenticação JWT integrada (botão "Authorize")
- ✅ Exemplos de requisições e respostas
- ✅ Validação de schemas
- ✅ Organização por tags (Autenticação, Consultas Médicas, Administração)

### Como usar o Swagger

1. Acesse `http://localhost:8080/swagger-ui.html`
2. Para testar endpoints protegidos:
   - Primeiro, faça login em `/api/auth/login`
   - Copie o token retornado
   - Clique no botão **"Authorize"** no topo da página
   - Cole o token no formato: `Bearer {seu_token}`
   - Agora você pode testar todos os endpoints protegidos

## Estrutura de Usuários

### Administrador (ADMIN)

**Credenciais padrão:**
- Email: `admin`
- Senha: `admin`

**Funcionalidades:**
- Dashboard com estatísticas
- Backup de imagens (download ZIP)
- Alterar senha própria

### Médico (MEDICO)

**Cadastro:**
- Rota pública: `POST /api/auth/register`
- Dados necessários: Nome, CPF, CRM, Data de Nascimento, Email, Senha

**Funcionalidades:**
- Login e autenticação JWT
- Criar consultas com pacientes
- Confirmar diagnóstico da IA
- Visualizar histórico de consultas

## Endpoints Principais

### Autenticação

- `POST /api/auth/register` - Cadastro de médico (público)
- `POST /api/auth/login` - Login (público)

### Médico

- `POST /api/medico/consultations` - Criar consulta (com upload de imagem)
- `PUT /api/medico/consultations/{id}/confirm` - Confirmar diagnóstico
- `GET /api/medico/consultations` - Listar minhas consultas

### Admin

- `GET /api/admin/dashboard` - Dashboard com estatísticas
- `POST /api/admin/change-password` - Alterar senha
- `GET /api/admin/backup` - Download backup de imagens (ZIP)

## Fluxo de Consulta

1. **Médico cria consulta:**
   - Envia dados do paciente e imagem da lesão
   - API salva imagem em disco
   - API envia imagem para microsserviço Python
   - Python retorna diagnóstico da IA
   - API salva resultado preliminar

2. **Médico confirma diagnóstico:**
   - Médico pode aceitar diagnóstico da IA ou escolher outro
   - API salva diagnóstico final validado

## Integração com IA

A API integra com o microsserviço Python através de `WebClient`:

- **Endpoint Python:** `POST http://localhost:8081/predict`
- **Formato:** MultipartFile (imagem)
- **Resposta:** JSON com predições (trata inconsistência "class"/"Class")

## Estrutura do Projeto

```
src/main/java/com/ptmd/
├── config/          # Configurações (DataInitializer)
├── controller/      # Controladores REST
├── dto/            # Data Transfer Objects
├── entity/         # Entidades JPA
├── exception/      # Handlers de exceção
├── repository/     # Repositórios JPA
├── security/       # Configuração de segurança e JWT
└── service/        # Lógica de negócio
```

## Segurança

- Autenticação via JWT
- RBAC (Role-Based Access Control)
- Senhas hashadas com BCrypt
- Proteção CSRF desabilitada (API stateless)

## Observações

- As imagens são armazenadas localmente na pasta `uploads/`
- O caminho das imagens é salvo no banco de dados
- O usuário admin é criado automaticamente na primeira execução
- O sistema trata a inconsistência do JSON do Python ("class" vs "Class")

