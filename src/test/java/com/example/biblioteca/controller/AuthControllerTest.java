package com.example.biblioteca.controller;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class AuthControllerTest extends MongoTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limpar() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso")
    void deveCadastrarUsuario() throws Exception {

        User user = new User(
                "allan",
                "allan@email.com",
                "123456"
        );

        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("allan"))
                .andExpect(jsonPath("$.email").value("allan@email.com"));
    }

    @Test
    @DisplayName("Deve rejeitar username duplicado")
    void deveRejeitarUsernameDuplicado() throws Exception {

        User user1 = new User(
                "allan",
                "allan@email.com",
                "123456"
        );

        User user2 = new User(
                "allan",
                "outro@email.com",
                "123456"
        );

        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve rejeitar email inválido")
    void deveRejeitarEmailInvalido() throws Exception {

        User user = new User(
                "allan",
                "email-invalido",
                "123456"
        );

        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }
}