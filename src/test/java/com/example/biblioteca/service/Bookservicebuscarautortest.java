package com.example.biblioteca.service;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.entity.Book;
import com.example.biblioteca.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class BookServiceBuscarAutorTest extends MongoTestBase {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void limpar() {
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve buscar livros por autor parcial")
    void deveBuscarPorAutor() {
        bookService.salvar(new Book("Livro A", "Machado de Assis", 1899, "user1"));
        bookService.salvar(new Book("Livro B", "Tolkien", 1954, "user1"));

        List<Book> resultado = bookService.buscarPorAutor("user1", "machado");
        assertEquals(1, resultado.size());
        assertEquals("Machado de Assis", resultado.get(0).getAutor());
    }

    @Test
    @DisplayName("Deve retornar lista vazia se autor não encontrado")
    void deveRetornarVazioParaAutorInexistente() {
        bookService.salvar(new Book("Livro A", "Tolkien", 1954, "user1"));

        List<Book> resultado = bookService.buscarPorAutor("user1", "Asimov");
        assertTrue(resultado.isEmpty());
    }
}