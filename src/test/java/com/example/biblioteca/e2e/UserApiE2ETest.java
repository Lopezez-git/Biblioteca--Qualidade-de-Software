package com.example.biblioteca.e2e;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserApiE2ETest extends MongoTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void limpar() {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/usuarios/cadastrar cria usuário com sucesso")
    void deveCadastrarUsuario() throws Exception {
        User user = new User("novousuario", "novo@email.com", "senha123");

        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("novousuario")))
                .andExpect(jsonPath("$.email", is("novo@email.com")))
                .andExpect(jsonPath("$.password", nullValue()));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/usuarios/cadastrar retorna 409 para username duplicado")
    void deveRejeitarUsernameDuplicado() throws Exception {
        User u1 = new User("duplicado", "um@email.com", "senha123");
        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u1)))
                .andExpect(status().isCreated());

        User u2 = new User("duplicado", "dois@email.com", "senha123");
        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u2)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/usuarios/cadastrar retorna 409 para email duplicado")
    void deveRejeitarEmailDuplicado() throws Exception {
        User u1 = new User("user1", "mesmo@email.com", "senha123");
        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u1)))
                .andExpect(status().isCreated());

        User u2 = new User("user2", "mesmo@email.com", "senha123");
        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u2)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/usuarios/cadastrar retorna 400 para email inválido")
    void deveRejeitarEmailInvalido() throws Exception {
        User user = new User("user3", "nao-e-um-email", "senha123");
        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/usuarios/cadastrar retorna 400 para senha fraca")
    void deveRejeitarSenhaFraca() throws Exception {
        User user = new User("user4", "user4@email.com", "123");
        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/usuarios/disponibilidade verifica username")
    void deveVerificarDisponibilidadeUsername() throws Exception {
        User u = new User("ocupado", "ocupado@email.com", "senha123");
        mockMvc.perform(post("/api/usuarios/cadastrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(u)));

        mockMvc.perform(get("/api/usuarios/disponibilidade").param("username", "ocupado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usernameDisponivel", is(false)));

        mockMvc.perform(get("/api/usuarios/disponibilidade").param("username", "livre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usernameDisponivel", is(true)));
    }
}