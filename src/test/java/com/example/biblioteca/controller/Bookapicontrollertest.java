package com.example.biblioteca.controller;

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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookApiControllerTest extends MongoTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USUARIO = "api_user";

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
        User u = new User(USUARIO, "api@email.com", passwordEncoder.encode("senha123"));
        u.setRoles(List.of("ROLE_USER"));
        userRepository.save(u);
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/livros sem autenticação retorna 401")
    void deveRetornar401SemAuth() throws Exception {
        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/livros retorna lista vazia para usuário autenticado")
    void deveRetornarListaVazia() throws Exception {
        mockMvc.perform(get("/api/livros").with(user(USUARIO).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/livros cria livro com sucesso")
    void deveCriarLivro() throws Exception {
        Book livro = new Book("Fundação", "Asimov", 1951, null);

        mockMvc.perform(post("/api/livros")
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Fundação"));
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/livros rejeita ano inválido")
    void deveRejeitarAnoInvalidoNaApi() throws Exception {
        Book livro = new Book("Livro Inválido", "Autor", 3000, null);

        mockMvc.perform(post("/api/livros")
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/livros rejeita ISBN inválido")
    void deveRejeitarIsbnInvalidoNaApi() throws Exception {
        Book livro = new Book("Livro ISBN", "Autor", 2020, null);
        livro.setIsbn("INVALIDO");

        mockMvc.perform(post("/api/livros")
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/livros rejeita ISBN duplicado")
    void deveRejeitarIsbnDuplicado() throws Exception {
        Book livro1 = new Book("Livro 1", "Autor", 2020, null);
        livro1.setIsbn("9780142437230");

        mockMvc.perform(post("/api/livros")
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro1)))
                .andExpect(status().isCreated());

        Book livro2 = new Book("Livro 2", "Autor", 2021, null);
        livro2.setIsbn("9780142437230");

        mockMvc.perform(post("/api/livros")
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro2)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/livros/{id} retorna livro existente")
    void deveBuscarLivroPorId() throws Exception {
        Book salvo = bookRepository.save(new Book("Duna", "Herbert", 1965, USUARIO));

        mockMvc.perform(get("/api/livros/" + salvo.getId())
                        .with(user(USUARIO).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Duna"));
    }

    @Test
    @Order(8)
    @DisplayName("GET /api/livros/{id} retorna 404 para ID inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
        mockMvc.perform(get("/api/livros/id-inexistente")
                        .with(user(USUARIO).roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("PUT /api/livros/{id} atualiza livro com sucesso")
    void deveAtualizarLivro() throws Exception {
        Book salvo = bookRepository.save(new Book("Título Original", "Autor", 2020, USUARIO));
        Book atualizado = new Book("Título Atualizado", "Autor", 2021, null);

        mockMvc.perform(put("/api/livros/" + salvo.getId())
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Título Atualizado"));
    }

    @Test
    @Order(10)
    @DisplayName("PUT /api/livros/{id} retorna 404 para ID inexistente")
    void deveRetornar404AoAtualizarIdInexistente() throws Exception {
        Book livro = new Book("Livro", "Autor", 2020, null);

        mockMvc.perform(put("/api/livros/nao-existe")
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    @DisplayName("PUT /api/livros/{id} rejeita ano inválido")
    void deveRejeitarAnoInvalidoNaAtualizacao() throws Exception {
        Book salvo = bookRepository.save(new Book("Livro", "Autor", 2020, USUARIO));
        Book atualizado = new Book("Livro", "Autor", 9999, null);

        mockMvc.perform(put("/api/livros/" + salvo.getId())
                        .with(user(USUARIO).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(12)
    @DisplayName("DELETE /api/livros/{id} remove livro com sucesso")
    void deveDeletarLivroViaApi() throws Exception {
        Book salvo = bookRepository.save(new Book("Para Deletar", "Autor", 2020, USUARIO));

        mockMvc.perform(delete("/api/livros/" + salvo.getId())
                        .with(user(USUARIO).roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(13)
    @DisplayName("DELETE /api/livros/{id} retorna 404 para ID inexistente")
    void deveRetornar404AoDeletarIdInexistente() throws Exception {
        mockMvc.perform(delete("/api/livros/nao-existe")
                        .with(user(USUARIO).roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(14)
    @DisplayName("GET /api/livros/estatisticas retorna estatísticas")
    void deveRetornarEstatisticasViaApi() throws Exception {
        mockMvc.perform(get("/api/livros/estatisticas")
                        .with(user(USUARIO).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists());
    }

    @Test
    @Order(15)
    @DisplayName("GET /api/livros/estatisticas retorna 401 sem auth")
    void deveRetornar401NasEstatisticasSemAuth() throws Exception {
        mockMvc.perform(get("/api/livros/estatisticas"))
                .andExpect(status().isUnauthorized());
    }
}