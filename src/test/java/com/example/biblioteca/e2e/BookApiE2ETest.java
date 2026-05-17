package com.example.biblioteca.e2e;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.entity.Book;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.BookRepository;
import com.example.biblioteca.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookApiE2ETest extends MongoTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String USUARIO = "testuser";

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
        User u = new User(USUARIO, "test@email.com", passwordEncoder.encode("senha123"));
        u.setRoles(List.of("ROLE_USER"));
        userRepository.save(u);
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/livros retorna lista vazia para novo usuário")
    void deveRetornarListaVazia() throws Exception {
        mockMvc.perform(get("/api/livros").with(user(USUARIO).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/livros cria livro com sucesso")
    void deveCriarLivro() throws Exception {
        Book livro = new Book("Dom Quixote", "Cervantes", 1605, USUARIO);
        livro.setIsbn("9780142437230");

        mockMvc.perform(post("/api/livros")
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo", is("Dom Quixote")))
                .andExpect(jsonPath("$.autor", is("Cervantes")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/livros retorna 400 para ano inválido")
    void deveRejeitarAnoInvalido() throws Exception {
        Book livro = new Book("Livro Futuro", "Autor", 3000, USUARIO);

        mockMvc.perform(post("/api/livros")
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/livros retorna 409 para ISBN duplicado")
    void deveRejeitarIsbnDuplicado() throws Exception {
        Book l1 = new Book("Livro 1", "Autor", 2000, USUARIO);
        l1.setIsbn("9780142437230");
        bookRepository.save(l1);

        Book l2 = new Book("Livro 2", "Outro Autor", 2001, USUARIO);
        l2.setIsbn("9780142437230");

        mockMvc.perform(post("/api/livros")
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(l2)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/livros/{id} retorna livro existente")
    void deveRetornarLivroPorId() throws Exception {
        Book livro = bookRepository.save(new Book("Grande Sertão", "Guimarães Rosa", 1956, USUARIO));

        mockMvc.perform(get("/api/livros/" + livro.getId()).with(user(USUARIO).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Grande Sertão")));
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/livros/{id} retorna 404 para ID inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
        mockMvc.perform(get("/api/livros/id-nao-existe").with(user(USUARIO).roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    @DisplayName("PUT /api/livros/{id} atualiza livro existente")
    void deveAtualizarLivro() throws Exception {
        Book livro = bookRepository.save(new Book("Titulo Antigo", "Autor", 2000, USUARIO));

        livro.setTitulo("Titulo Novo");
        livro.setStatus(Book.StatusLeitura.LIDO);

        mockMvc.perform(put("/api/livros/" + livro.getId())
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Titulo Novo")))
                .andExpect(jsonPath("$.status", is("LIDO")));
    }

    @Test
    @Order(8)
    @DisplayName("PUT /api/livros/{id} retorna 404 para ID inexistente")
    void deveRetornar404AoAtualizarIdInexistente() throws Exception {
        Book livro = new Book("Livro X", "Autor", 2000, USUARIO);

        mockMvc.perform(put("/api/livros/id-invalido")
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("DELETE /api/livros/{id} remove livro")
    void deveDeletarLivro() throws Exception {
        Book livro = bookRepository.save(new Book("Para Deletar", "Autor", 2000, USUARIO));

        mockMvc.perform(delete("/api/livros/" + livro.getId()).with(user(USUARIO).roles("USER")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/livros/" + livro.getId()).with(user(USUARIO).roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    @DisplayName("DELETE /api/livros/{id} retorna 404 para ID inexistente")
    void deveRetornar404AoDeletarIdInexistente() throws Exception {
        mockMvc.perform(delete("/api/livros/nao-existe").with(user(USUARIO).roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    @DisplayName("GET /api/livros/estatisticas retorna contagens corretas")
    void deveRetornarEstatisticas() throws Exception {
        Book l1 = new Book("L1", "A", 2000, USUARIO); l1.setStatus(Book.StatusLeitura.LIDO);
        Book l2 = new Book("L2", "B", 2001, USUARIO); l2.setStatus(Book.StatusLeitura.LENDO);
        bookRepository.save(l1); bookRepository.save(l2);

        mockMvc.perform(get("/api/livros/estatisticas").with(user(USUARIO).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.lidos", is(1)))
                .andExpect(jsonPath("$.lendo", is(1)));
    }

    @Test
    @Order(12)
    @DisplayName("Endpoints sem autenticação retornam 401")
    void semAutenticacaoRetorna401() throws Exception {
        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(13)
    @DisplayName("Usuário não pode ver livros de outro usuário")
    void naoDeveVerLivrosDeOutroUsuario() throws Exception {
        Book livroOutroUser = bookRepository.save(new Book("Livro Secreto", "Autor", 2000, "outro_user"));

        mockMvc.perform(get("/api/livros/" + livroOutroUser.getId()).with(user(USUARIO).roles("USER")))
                .andExpect(status().isNotFound());
    }
}