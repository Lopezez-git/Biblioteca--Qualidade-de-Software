package com.example.biblioteca.controller;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest extends MongoTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void limpar() {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("GET /auth/login retorna página de login")
    void deveRetornarPaginaLogin() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @Order(2)
    @DisplayName("GET /auth/cadastro retorna página de cadastro")
    void deveRetornarPaginaCadastro() throws Exception {
        mockMvc.perform(get("/auth/cadastro"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/cadastro"));
    }

    @Test
    @Order(3)
    @DisplayName("POST /auth/cadastro cria usuário e redireciona para login")
    void deveCadastrarERedirecionarParaLogin() throws Exception {
        mockMvc.perform(post("/auth/cadastro")
                        .param("username", "novousr")
                        .param("email", "novo@email.com")
                        .param("password", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    @Order(4)
    @DisplayName("POST /auth/cadastro redireciona com erro se senhas não coincidem")
    void deveRejeitarSenhasQueNaoCoincidem() throws Exception {
        mockMvc.perform(post("/auth/cadastro")
                        .param("username", "usr2")
                        .param("email", "usr2@email.com")
                        .param("password", "senha123")
                        .param("confirmarSenha", "diferente"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/cadastro"));
    }

    @Test
    @Order(5)
    @DisplayName("POST /auth/cadastro redireciona com erro se email inválido")
    void deveRejeitarEmailInvalido() throws Exception {
        mockMvc.perform(post("/auth/cadastro")
                        .param("username", "usr3")
                        .param("email", "email-invalido")
                        .param("password", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/cadastro"));
    }

    @Test
    @Order(6)
    @DisplayName("POST /auth/cadastro redireciona com erro se username duplicado")
    void deveRejeitarUsernameDuplicado() throws Exception {
        mockMvc.perform(post("/auth/cadastro")
                        .param("username", "duplicado")
                        .param("email", "d1@email.com")
                        .param("password", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/auth/cadastro")
                        .param("username", "duplicado")
                        .param("email", "d2@email.com")
                        .param("password", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/cadastro"));
    }
}