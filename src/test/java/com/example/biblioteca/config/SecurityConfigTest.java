package com.example.biblioteca.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void deveInstanciarClasse() {

        SecurityConfig config = new SecurityConfig();

        assertNotNull(config);
    }
}