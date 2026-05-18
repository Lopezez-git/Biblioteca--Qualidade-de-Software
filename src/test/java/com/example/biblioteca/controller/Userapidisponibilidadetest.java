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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class UserApiDisponibilidadeTest extends MongoTestBase {

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
    @DisplayName("GET /api/usuarios/disponibilidade verifica username disponível")
    void deveVerificarUsernameDisponivel() throws Exception {
        mockMvc.perform(get("/api/usuarios/disponibilidade")
                        .param("username", "naoexiste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usernameDisponivel").value(true));
    }

    @Test
    @DisplayName("GET /api/usuarios/disponibilidade verifica username indisponível")
    void deveVerificarUsernameIndisponivel() throws Exception {
        userService.cadastrar(new User("joao", "joao@email.com", "senha123"));

        mockMvc.perform(get("/api/usuarios/disponibilidade")
                        .param("username", "joao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usernameDisponivel").value(false));
    }

    @Test
    @DisplayName("GET /api/usuarios/disponibilidade verifica email disponível")
    void deveVerificarEmailDisponivel() throws Exception {
        mockMvc.perform(get("/api/usuarios/disponibilidade")
                        .param("email", "novo@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailDisponivel").value(true));
    }

    @Test
    @DisplayName("GET /api/usuarios/disponibilidade verifica ambos")
    void deveVerificarAmbos() throws Exception {
        mockMvc.perform(get("/api/usuarios/disponibilidade")
                        .param("username", "livre")
                        .param("email", "livre@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usernameDisponivel").value(true))
                .andExpect(jsonPath("$.emailDisponivel").value(true));
    }
}