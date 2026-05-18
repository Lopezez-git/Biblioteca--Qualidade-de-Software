package com.example.biblioteca.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void deveTestarGettersESetters() {

        User user = new User();

        user.setUsername("joao");
        user.setEmail("joao@email.com");
        user.setPassword("123");
        user.setAtivo(true);
        user.setRoles(List.of("ROLE_USER"));

        assertEquals("joao", user.getUsername());
        assertEquals("joao@email.com", user.getEmail());
        assertEquals("123", user.getPassword());
        assertTrue(user.isAtivo());
        assertEquals(1, user.getRoles().size());
    }
}