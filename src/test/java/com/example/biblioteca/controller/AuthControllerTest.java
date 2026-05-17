package com.example.biblioteca.controller;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @BeforeEach
    void limpar() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso")
    void deveCadastrarUsuario() throws Exception {

        mockMvc.perform(post("/auth/cadastro")
                        .with(csrf())
                        .param("username", "allan")
                        .param("email", "allan@email.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Deve rejeitar username duplicado")
    void deveRejeitarUsernameDuplicado() throws Exception {

        mockMvc.perform(post("/auth/cadastro")
                .with(csrf())
                .param("username", "allan")
                .param("email", "allan@email.com")
                .param("password", "123456"));

        mockMvc.perform(post("/auth/cadastro")
                        .with(csrf())
                        .param("username", "allan")
                        .param("email", "outro@email.com")
                        .param("password", "123456"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("Deve rejeitar email inválido")
    void deveRejeitarEmailInvalido() throws Exception {

        mockMvc.perform(post("/auth/cadastro")
                        .with(csrf())
                        .param("username", "allan")
                        .param("email", "email-invalido")
                        .param("password", "123456"))
                .andExpect(status().isOk());
    }
}