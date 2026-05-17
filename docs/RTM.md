# RTM — Matriz de Rastreabilidade de Requisitos
## Gerenciador de Biblioteca Pessoal

---

## Índice

1. [Requisitos Funcionais](#requisitos-funcionais)
2. [Matriz de Rastreabilidade](#matriz-de-rastreabilidade)
3. [Diagramas UML de Sequência](#diagramas-uml-de-sequência)

---

## Requisitos Funcionais

| ID     | Requisito                                              | Prioridade |
|--------|--------------------------------------------------------|------------|
| RF-001 | Cadastro de novo usuário                               | Alta       |
| RF-002 | Autenticação (login/logout) com gerenciamento de sessão| Alta       |
| RF-003 | Listar livros do usuário autenticado                   | Alta       |
| RF-004 | Adicionar novo livro à biblioteca                      | Alta       |
| RF-005 | Editar dados de um livro existente                     | Alta       |
| RF-006 | Remover livro da biblioteca                            | Alta       |
| RF-007 | Filtrar livros por status de leitura                   | Média      |
| RF-008 | Buscar livros por título                               | Média      |
| RF-009 | Exibir estatísticas da biblioteca (totais por status)  | Média      |
| RF-010 | Buscar informações de livro por ISBN via API externa   | Baixa      |
| RF-011 | Validação de ISBN                                      | Média      |
| RF-012 | Validação de ano de publicação                         | Média      |
| RF-013 | Validação de email no cadastro                         | Alta       |
| RF-014 | Impedir ISBN duplicado para o mesmo usuário            | Média      |
| RF-015 | Usuário só acessa seus próprios livros                 | Alta       |

---

## Matriz de Rastreabilidade

| ID     | Requisito                            | Classe de Teste                          | Método(s) de Teste                                                                              | Tipo           |
|--------|--------------------------------------|------------------------------------------|-------------------------------------------------------------------------------------------------|----------------|
| RF-001 | Cadastro de usuário                  | `UserApiE2ETest`                         | `deveCadastrarUsuario`                                                                          | Caixa-Preta E2E|
| RF-001 | Cadastro de usuário                  | `AuthControllerTest`                     | `deveCadastrarERedirecionarParaLogin`                                                           | Caixa-Preta E2E|
| RF-001 | Cadastro de usuário                  | `UserServiceTest`                        | `deveCadastrarUsuario`                                                                          | Integração     |
| RF-002 | Autenticação / sessão                | `AuthControllerTest`                     | `deveRetornarPaginaLogin`, `deveRejeitarSenhasQueNaoCoincidem`                                  | Caixa-Preta    |
| RF-002 | Autenticação / sessão                | `BookControllerTest`                     | `deveRedirecionarParaLoginSemAuth`                                                              | Caixa-Preta    |
| RF-002 | Autenticação / sessão                | `BookApiE2ETest`                         | `semAutenticacaoRetorna401`                                                                     | Caixa-Preta    |
| RF-003 | Listar livros do usuário             | `BookApiE2ETest`                         | `deveRetornarListaVazia`                                                                        | Caixa-Preta E2E|
| RF-003 | Listar livros do usuário             | `BookServiceTest`                        | `deveListarApenasLivrosDoUsuario`                                                               | Integração     |
| RF-003 | Listar livros do usuário             | `BookControllerTest`                     | `deveRetornarListaParaUsuarioAutenticado`                                                       | Caixa-Preta    |
| RF-004 | Adicionar livro                      | `BookApiE2ETest`                         | `deveCriarLivro`, `deveRejeitarAnoInvalido`, `deveRejeitarIsbnDuplicado`                        | Caixa-Preta E2E|
| RF-004 | Adicionar livro                      | `BookServiceTest`                        | `deveSalvarERecuperarLivro`                                                                     | Integração     |
| RF-004 | Adicionar livro                      | `BookControllerTest`                     | `deveSalvarLivroERedirecionar`                                                                  | Caixa-Preta    |
| RF-005 | Editar livro                         | `BookApiE2ETest`                         | `deveAtualizarLivro`, `deveRetornar404AoAtualizarIdInexistente`                                 | Caixa-Preta E2E|
| RF-005 | Editar livro                         | `BookServiceTest`                        | `deveAtualizarStatusLeitura`                                                                    | Integração     |
| RF-005 | Editar livro                         | `BookControllerTest`                     | `deveRetornarFormularioEdicao`                                                                  | Caixa-Preta    |
| RF-006 | Remover livro                        | `BookApiE2ETest`                         | `deveDeletarLivro`, `deveRetornar404AoDeletarIdInexistente`                                     | Caixa-Preta E2E|
| RF-006 | Remover livro                        | `BookServiceTest`                        | `deveDeletarLivro`                                                                              | Integração     |
| RF-006 | Remover livro                        | `BookControllerTest`                     | `deveDeletarERedirecionar`                                                                      | Caixa-Preta    |
| RF-007 | Filtrar por status                   | `BookServiceTest`                        | `deveFiltrarPorStatus`                                                                          | Integração     |
| RF-007 | Filtrar por status                   | `BookControllerTest`                     | `deveFiltrarPorStatus`                                                                          | Caixa-Preta    |
| RF-008 | Buscar por título                    | `BookServiceTest`                        | `deveBuscarPorTitulo`                                                                           | Integração     |
| RF-008 | Buscar por título                    | `BookControllerTest`                     | `deveBuscarPorTitulo`                                                                           | Caixa-Preta    |
| RF-009 | Estatísticas                         | `BookApiE2ETest`                         | `deveRetornarEstatisticas`                                                                      | Caixa-Preta E2E|
| RF-009 | Estatísticas                         | `BookServiceTest`                        | `deveCalcularEstatisticas`                                                                      | Integração     |
| RF-010 | Busca ISBN via API externa (VCR)     | `OpenLibraryIntegrationTest`             | `deveBuscarInfoPorIsbn`, `deveSalvarLivroNoMongoDB`, `deveTratarIsbnNaoEncontrado`              | VCR + TC       |
| RF-011 | Validação de ISBN                    | `BookServiceTest`                        | `deveValidarIsbnCorreto`, `deveRejeitarIsbnInvalido`, `isbnNuloOuVazioEhValido` (parametrizados)| Caixa-Branca   |
| RF-012 | Validação de ano de publicação       | `BookServiceTest`                        | `deveValidarAnoPublicacao` (parametrizado com 5 cenários), `anoNuloDeveSerInvalido`             | Caixa-Branca   |
| RF-013 | Validação de email                   | `UserServiceTest`                        | `deveValidarEmailCorreto`, `deveRejeitarEmailInvalido`, `emailNuloDeveSerInvalido` (parametrizados)| Caixa-Branca|
| RF-013 | Validação de email                   | `UserApiE2ETest`                         | `deveRejeitarEmailInvalido`                                                                     | Caixa-Preta E2E|
| RF-013 | Validação de email                   | `AuthControllerTest`                     | `deveRejeitarEmailInvalido`                                                                     | Caixa-Preta    |
| RF-014 | ISBN duplicado                       | `BookApiE2ETest`                         | `deveRejeitarIsbnDuplicado`                                                                     | Caixa-Preta E2E|
| RF-014 | ISBN duplicado                       | `BookServiceTest`                        | `deveDetectarIsbnDuplicado`                                                                     | Integração     |
| RF-015 | Isolamento por usuário               | `BookApiE2ETest`                         | `naoDeveVerLivrosDeOutroUsuario`                                                                | Caixa-Preta E2E|
| RF-015 | Isolamento por usuário               | `BookServiceTest`                        | `deveListarApenasLivrosDoUsuario`, `buscaPorIdDeOutroUsuarioRetornaVazio`                       | Integração     |
| RF-015 | Isolamento por usuário               | `BookControllerTest`                     | `deveRedirecionarSeNaoForDono`                                                                  | Caixa-Preta    |

---

## Diagramas UML de Sequência

### RF-001 — Cadastro de Usuário

```mermaid
sequenceDiagram
    actor U as Usuário
    participant AC as AuthController
    participant US as UserService
    participant UR as UserRepository
    participant DB as MongoDB

    U->>AC: POST /auth/cadastro (username, email, senha)
    AC->>US: isEmailValido(email)
    US-->>AC: true
    AC->>US: isSenhaForte(senha)
    US-->>AC: true
    AC->>US: usernameDisponivel(username)
    US->>UR: existsByUsername(username)
    UR->>DB: query
    DB-->>UR: false
    UR-->>US: false
    US-->>AC: true
    AC->>US: emailDisponivel(email)
    US->>UR: existsByEmail(email)
    UR-->>US: false
    US-->>AC: true
    AC->>US: cadastrar(user)
    US->>US: encode(senha)
    US->>UR: save(user)
    UR->>DB: insert
    DB-->>UR: user salvo
    UR-->>US: user
    US-->>AC: user
    AC-->>U: redirect /auth/login (sucesso)
```

---

### RF-002 — Autenticação e Gerenciamento de Sessão

```mermaid
sequenceDiagram
    actor U as Usuário
    participant SC as Spring Security
    participant UDS as UserDetailsService
    participant UR as UserRepository
    participant DB as MongoDB

    U->>SC: POST /auth/login (username, senha)
    SC->>UDS: loadUserByUsername(username)
    UDS->>UR: findByUsername(username)
    UR->>DB: query
    DB-->>UR: user document
    UR-->>UDS: User
    UDS-->>SC: UserDetails
    SC->>SC: matches(senha, encodedPassword)
    alt Credenciais válidas
        SC-->>U: redirect /livros (sessão criada)
    else Credenciais inválidas
        SC-->>U: redirect /auth/login?error=true
    end

    Note over U,SC: Logout
    U->>SC: POST /auth/logout
    SC->>SC: invalidar sessão
    SC-->>U: redirect /auth/login?logout=true
```

---

### RF-003 — Listar Livros

```mermaid
sequenceDiagram
    actor U as Usuário Autenticado
    participant BC as BookController
    participant BS as BookService
    participant BR as BookRepository
    participant DB as MongoDB

    U->>BC: GET /livros
    BC->>BS: listarTodos(username)
    BS->>BR: findByDonoUsername(username)
    BR->>DB: query {donoUsername: username}
    DB-->>BR: [Book]
    BR-->>BS: List<Book>
    BS-->>BC: List<Book>
    BC->>BS: calcularEstatisticas(username)
    BS->>BR: countByDonoUsername(username)
    BR-->>BS: long
    BS-->>BC: EstatisticasBiblioteca
    BC-->>U: View book/list (livros + stats)
```

---

### RF-004 — Adicionar Livro (API REST)

```mermaid
sequenceDiagram
    actor C as Cliente (JSON)
    participant BAC as BookApiController
    participant BS as BookService
    participant BR as BookRepository
    participant DB as MongoDB

    C->>BAC: POST /api/livros {titulo, autor, anoPublicacao, isbn, ...}
    BAC->>BS: isAnoValido(anoPublicacao)
    BS-->>BAC: true
    BAC->>BS: isIsbnValido(isbn)
    BS-->>BAC: true
    BAC->>BS: isbnJaCadastrado(isbn, username)
    BS->>BR: existsByIsbnAndDonoUsername(isbn, username)
    BR->>DB: query
    DB-->>BR: false
    BR-->>BS: false
    BS-->>BAC: false
    BAC->>BS: salvar(book)
    BS->>BR: save(book)
    BR->>DB: insert
    DB-->>BR: book com id
    BR-->>BS: Book
    BS-->>BAC: Book
    BAC-->>C: 201 Created {book}
```

---

### RF-006 — Remover Livro

```mermaid
sequenceDiagram
    actor C as Cliente
    participant BAC as BookApiController
    participant BS as BookService
    participant BR as BookRepository
    participant DB as MongoDB

    C->>BAC: DELETE /api/livros/{id}
    BAC->>BS: buscarPorId(id, username)
    BS->>BR: findByIdAndDonoUsername(id, username)
    BR->>DB: query
    alt Livro encontrado
        DB-->>BR: Book
        BR-->>BS: Optional<Book> (presente)
        BS-->>BAC: Optional (presente)
        BAC->>BS: deletar(id)
        BS->>BR: deleteById(id)
        BR->>DB: delete
        BAC-->>C: 204 No Content
    else Não encontrado ou não é dono
        DB-->>BR: null
        BR-->>BS: Optional.empty()
        BS-->>BAC: Optional.empty()
        BAC-->>C: 404 Not Found
    end
```

---

### RF-010 — Busca ISBN via Open Library (VCR)

```mermaid
sequenceDiagram
    actor U as Usuário
    participant BAC as BookApiController
    participant OLS as OpenLibraryService
    participant VCR as VcrHelper (Teste)
    participant OL as Open Library API

    Note over VCR,OL: Em produção: chamada real à API
    Note over VCR,OL: Nos testes: VCR intercepta e reproduz cassette

    U->>BAC: GET /api/open-library/isbn/{isbn}
    BAC->>OLS: buscarPorIsbn(isbn)
    OLS->>OLS: validar ISBN
    OLS->>OL: GET /api/books?bibkeys=ISBN:{isbn}&format=json&jscmd=data
    OL-->>OLS: 200 OK {JSON com dados do livro}
    OLS->>OLS: parseBookInfo(json, isbn)
    OLS-->>BAC: BookInfo
    BAC-->>U: 200 OK {titulo, autor, editora, anoPublicacao}
```

---

### RF-015 — Isolamento por Usuário

```mermaid
sequenceDiagram
    actor U1 as Usuário 1
    actor U2 as Usuário 2
    participant BAC as BookApiController
    participant BS as BookService
    participant BR as BookRepository
    participant DB as MongoDB

    Note over U1,DB: Usuário 1 cria livro
    U1->>BAC: POST /api/livros {titulo: "Livro Secreto"}
    BAC->>BS: salvar(book com donoUsername=user1)
    BS->>BR: save(book)
    BR->>DB: insert {donoUsername: "user1"}

    Note over U2,DB: Usuário 2 tenta acessar o livro do Usuário 1
    U2->>BAC: GET /api/livros/{id}
    BAC->>BS: buscarPorId(id, "user2")
    BS->>BR: findByIdAndDonoUsername(id, "user2")
    BR->>DB: query {_id: id, donoUsername: "user2"}
    DB-->>BR: null (donoUsername não bate)
    BR-->>BS: Optional.empty()
    BS-->>BAC: Optional.empty()
    BAC-->>U2: 404 Not Found
```
