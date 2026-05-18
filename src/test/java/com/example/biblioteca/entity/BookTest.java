package com.example.biblioteca.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void deveTestarGettersESetters() {

        Book book = new Book();

        book.setTitulo("Duna");
        book.setAutor("Frank Herbert");
        book.setAnoPublicacao(1965);
        book.setStatus(Book.StatusLeitura.LIDO);
        book.setIsbn("123");

        assertEquals("Duna", book.getTitulo());
        assertEquals("Frank Herbert", book.getAutor());
        assertEquals(1965, book.getAnoPublicacao());
        assertEquals(Book.StatusLeitura.LIDO, book.getStatus());
        assertEquals("123", book.getIsbn());
    }
}