package com.example.biblioteca.controller;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.UserRepository;
import com.example.biblioteca.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class AuthControllerFormTest extends MongoTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void limpar() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /auth/login retorna página de login")
    void deveRetornarPaginaLogin() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("GET /auth/cadastro retorna página de cadastro com model")
    void deveRetornarPaginaCadastro() throws Exception {
        mockMvc.perform(get("/auth/cadastro"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/cadastro"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    @DisplayName("POST /auth/cadastro com senhas diferentes redireciona com erro")
    void deveRejeitarSenhasDiferentes() throws Exception {
        mockMvc.perform(post("/auth/cadastro")
                        .with(csrf())
                        .param("username", "joao")
                        .param("email", "joao@email.com")
                        .param("password", "senha123")
                        .param("confirmarSenha", "outrasenha"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/cadastro"));
    }

    @Test
    @DisplayName("POST /auth/cadastro com email inválido redireciona com erro")
    void deveRejeitarEmailInvalidoNoForm() throws Exception {
        mockMvc.perform(post("/auth/cadastro")
                        .with(csrf())
                        .param("username", "joao")
                        .param("email", "email-invalido")
                        .param("password", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/cadastro"));
    }

    @Test
    @DisplayName("POST /auth/cadastro com senha fraca redireciona com erro")
    void deveRejeitarSenhaFracaNoForm() throws Exception {
        mockMvc.perform(post("/auth/cadastro")
                        .with(csrf())
                        .param("username", "joao")
                        .param("email", "joao@email.com")
                        .param("password", "123")
                        .param("confirmarSenha", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/cadastro"));
    }

    @Test
    @DisplayName("POST /auth/cadastro com username duplicado redireciona com erro")
    void deveRejeitarUsernameDuplicadoNoForm() throws Exception {
        userService.cadastrar(new User("joao", "joao@email.com", "senha123"));

        mockMvc.perform(post("/auth/cadastro")
                        .with(csrf())
                        .param("username", "joao")
                        .param("email", "outro@email.com")
                        .param("password", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/cadastro"));
    }

    @Test
    @DisplayName("POST /auth/cadastro com email duplicado redireciona com erro")
    void deveRejeitarEmailDuplicadoNoForm() throws Exception {
        userService.cadastrar(new User("joao", "joao@email.com", "senha123"));

        mockMvc.perform(post("/auth/cadastro")
                        .with(csrf())
                        .param("username", "outro")
                        .param("email", "joao@email.com")
                        .param("password", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/cadastro"));
    }

    @Test
    @DisplayName("POST /auth/cadastro com dados válidos redireciona para login")
    void deveCadastrarComSucesso() throws Exception {
        mockMvc.perform(post("/auth/cadastro")
                        .with(csrf())
                        .param("username", "novousuario")
                        .param("email", "novo@email.com")
                        .param("password", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}