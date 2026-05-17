# 📚 Gerenciador de Biblioteca Pessoal

Projeto Semestral — Análise e Desenvolvimento de Sistemas  
Disciplina: Qualidade de Software

---

## Visão Geral

Aplicação web completa para cadastro e gerenciamento de livros de uma biblioteca pessoal, com autenticação de usuários, persistência em MongoDB e testes automatizados usando **Testcontainers** e **VCR**.

---

## Tecnologias

| Camada       | Tecnologia                              |
|--------------|-----------------------------------------|
| Backend      | Java 21 + Spring Boot 3.2               |
| Persistência | MongoDB (NoSQL) + Spring Data MongoDB   |
| Frontend     | Thymeleaf + CSS responsivo              |
| Segurança    | Spring Security (BCrypt + sessão)       |
| HTTP Client  | OkHttp 4.12                             |
| Testes       | JUnit 5, Testcontainers, VCR (MockWebServer) |
| Cobertura    | JaCoCo (mínimo 80%)                     |
| Qualidade    | SonarQube / SonarCloud                  |
| CI/CD        | GitHub Actions                          |

---

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/example/biblioteca/
│   │   ├── BibliotecaApplication.java
│   │   ├── config/SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── BookController.java
│   │   │   ├── HomeController.java
│   │   │   └── api/
│   │   │       ├── BookApiController.java
│   │   │       ├── UserApiController.java
│   │   │       └── OpenLibraryApiController.java
│   │   ├── entity/
│   │   │   ├── Book.java
│   │   │   ├── User.java
│   │   │   └── BookInfo.java
│   │   ├── repository/
│   │   │   ├── BookRepository.java
│   │   │   └── UserRepository.java
│   │   └── service/
│   │       ├── BookService.java
│   │       ├── UserService.java
│   │       └── OpenLibraryService.java
│   └── resources/
│       ├── application.properties
│       ├── static/css/style.css
│       ├── static/js/app.js
│       └── templates/
│           ├── layout.html
│           ├── auth/{login,cadastro}.html
│           └── book/{list,form}.html
└── test/
    ├── java/com/example/biblioteca/
    │   ├── util/VcrHelper.java
    │   ├── service/{BookServiceTest,UserServiceTest}.java
    │   ├── controller/{BookControllerTest,AuthControllerTest}.java
    │   ├── e2e/{BookApiE2ETest,UserApiE2ETest}.java
    │   └── integration/OpenLibraryIntegrationTest.java
    └── resources/
        ├── application-test.properties
        ├── application-ci.properties
        └── vcr-cassettes/
            ├── isbn_9780142437230.json
            └── isbn_invalido.json

docs/
└── RTM.md  ← Matriz de Rastreabilidade + Diagramas UML
```

---

## Como Executar

### Pré-requisitos

- Java 21+
- Docker (para Testcontainers)
- MongoDB Atlas ou local

### 1. Configurar MongoDB

Edite `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://<user>:<password>@cluster0.xxx.mongodb.net/biblioteca
```

### 2. Executar a aplicação

```bash
./mvnw spring-boot:run
```

Acesse: http://localhost:8080

### 3. Executar os testes (requer Docker para Testcontainers)

```bash
./mvnw clean test
```

### 4. Gerar relatório de cobertura

```bash
./mvnw clean verify
```

Relatório gerado em: `target/site/jacoco/index.html`

---

## Estratégia de Testes

### Sem Mocks — regra do projeto

Todos os testes usam:
- **Testcontainers** para MongoDB real (container Docker)
- **VCR (MockWebServer)** para chamadas HTTP externas

### Tipos de testes

| Tipo                | Classe                          | Descrição                                   |
|---------------------|---------------------------------|---------------------------------------------|
| Unitário/Integração | `BookServiceTest`               | Lógica de negócio, validações (caixa-branca)|
| Unitário/Integração | `UserServiceTest`               | Cadastro, validação de email/senha          |
| Caixa-Preta E2E     | `BookApiE2ETest`                | Endpoints REST completos                    |
| Caixa-Preta E2E     | `UserApiE2ETest`                | Cadastro e disponibilidade via API          |
| Controller MVC      | `BookControllerTest`            | Views Thymeleaf, redirecionamentos          |
| Controller MVC      | `AuthControllerTest`            | Login, cadastro via formulário              |
| VCR + Testcontainers| `OpenLibraryIntegrationTest`    | API externa + persistência                  |

### Parametrizados (múltiplos cenários)

- `BookServiceTest.deveValidarAnoPublicacao` — 5 cenários (1605, 2025, 999, 2030, 0)
- `BookServiceTest.deveValidarIsbnCorreto` / `deveRejeitarIsbnInvalido`
- `UserServiceTest.deveValidarEmailCorreto` / `deveRejeitarEmailInvalido`
- `UserServiceTest.deveValidarForcaSenha`

---

## SonarQube

Configure os secrets no repositório GitHub:

| Secret               | Descrição                       |
|---------------------|---------------------------------|
| `SONAR_TOKEN`       | Token do SonarCloud             |
| `SONAR_ORGANIZATION`| Nome da organização SonarCloud  |

A análise roda automaticamente no push para `main`/`master`.

---

## CI/CD (GitHub Actions)

```
push/PR → build → test → coverage (JaCoCo) → sonarqube (main apenas)
```

Artefatos gerados:
- `jacoco-report` — relatório HTML de cobertura
- `surefire-reports` — resultados dos testes
- `app-jar` — JAR da aplicação

---

## Funcionalidades

- **Cadastro e login** de usuários com sessão gerenciada pelo Spring Security
- **CRUD completo** de livros (título, autor, ISBN, ano, gênero, editora, descrição, notas)
- **Status de leitura**: Não Lido, Lendo, Lido, Relendo, Abandonado
- **Avaliação** com estrelas (1–5)
- **Filtro** por status e busca por título
- **Estatísticas** da biblioteca (totais por status)
- **Busca de livros por ISBN** via Open Library API (com VCR nos testes)
- **Isolamento**: cada usuário vê apenas seus próprios livros
- **Design responsivo** para desktop e mobile

---

## RTM

Ver [`docs/RTM.md`](docs/RTM.md) para a Matriz de Rastreabilidade completa com diagramas UML de sequência para todos os 15 requisitos funcionais.
