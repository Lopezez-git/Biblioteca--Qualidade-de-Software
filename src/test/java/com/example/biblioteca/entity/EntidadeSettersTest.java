package com.example.biblioteca.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntidadeSettersTest {

    @Test
    @DisplayName("Book.setTitulo atualiza atualizadoEm")
    void bookSetTituloAtualizaTimestamp() throws InterruptedException {
        Book book = new Book();
        LocalDateTime antes = book.getAtualizadoEm();
        Thread.sleep(5);
        book.setTitulo("Novo Título");
        assertNotNull(book.getAtualizadoEm());
        assertFalse(book.getAtualizadoEm().isBefore(antes));
    }

    @Test
    @DisplayName("Book.setAutor atualiza atualizadoEm")
    void bookSetAutorAtualizaTimestamp() throws InterruptedException {
        Book book = new Book();
        LocalDateTime antes = book.getAtualizadoEm();
        Thread.sleep(5);
        book.setAutor("Novo Autor");
        assertNotNull(book.getAtualizadoEm());
        assertFalse(book.getAtualizadoEm().isBefore(antes));
    }

    @Test
    @DisplayName("Book getters e setters de campos opcionais")
    void bookGettersSettersCamposOpcionais() {
        Book book = new Book();
        book.setGenero("Ficção Científica");
        book.setEditora("Aleph");
        book.setDescricao("Uma descrição");
        book.setAvaliacaoNota(5);
        book.setNotas("Notas do leitor");

        assertEquals("Ficção Científica", book.getGenero());
        assertEquals("Aleph", book.getEditora());
        assertEquals("Uma descrição", book.getDescricao());
        assertEquals(5, book.getAvaliacaoNota());
        assertEquals("Notas do leitor", book.getNotas());
    }

    @Test
    @DisplayName("Book construtor com parâmetros inicializa corretamente")
    void bookConstrutorComParametros() {
        Book book = new Book("Duna", "Herbert", 1965, "user1");
        assertEquals("Duna", book.getTitulo());
        assertEquals("Herbert", book.getAutor());
        assertEquals(1965, book.getAnoPublicacao());
        assertEquals("user1", book.getDonoUsername());
        assertEquals(Book.StatusLeitura.NAO_LIDO, book.getStatus());
        assertNotNull(book.getCriadoEm());
        assertNotNull(book.getAtualizadoEm());
    }

    @Test
    @DisplayName("Book setCriadoEm e setAtualizadoEm funcionam")
    void bookSetTimestamps() {
        Book book = new Book();
        LocalDateTime agora = LocalDateTime.now();
        book.setCriadoEm(agora);
        book.setAtualizadoEm(agora);
        assertEquals(agora, book.getCriadoEm());
        assertEquals(agora, book.getAtualizadoEm());
    }

    @Test
    @DisplayName("User.setUsername atualiza atualizadoEm")
    void userSetUsernameAtualizaTimestamp() throws InterruptedException {
        User user = new User();
        LocalDateTime antes = user.getAtualizadoEm();
        Thread.sleep(5);
        user.setUsername("novousername");
        assertNotNull(user.getAtualizadoEm());
        assertFalse(user.getAtualizadoEm().isBefore(antes));
    }

    @Test
    @DisplayName("User.setEmail atualiza atualizadoEm")
    void userSetEmailAtualizaTimestamp() throws InterruptedException {
        User user = new User();
        LocalDateTime antes = user.getAtualizadoEm();
        Thread.sleep(5);
        user.setEmail("novo@email.com");
        assertNotNull(user.getAtualizadoEm());
        assertFalse(user.getAtualizadoEm().isBefore(antes));
    }

    @Test
    @DisplayName("User construtor com parâmetros inicializa corretamente")
    void userConstrutorComParametros() {
        User user = new User("joao", "joao@email.com", "senha123");
        assertEquals("joao", user.getUsername());
        assertEquals("joao@email.com", user.getEmail());
        assertEquals("senha123", user.getPassword());
        assertTrue(user.isAtivo());
        assertEquals(1, user.getRoles().size());
        assertEquals("ROLE_USER", user.getRoles().get(0));
        assertNotNull(user.getCriadoEm());
        assertNotNull(user.getAtualizadoEm());
    }

    @Test
    @DisplayName("User getNomeCompleto e setNomeCompleto")
    void userNomeCompleto() {
        User user = new User();
        user.setNomeCompleto("João da Silva");
        assertEquals("João da Silva", user.getNomeCompleto());
    }

    @Test
    @DisplayName("User setCriadoEm e setAtualizadoEm funcionam")
    void userSetTimestamps() {
        User user = new User();
        LocalDateTime agora = LocalDateTime.now();
        user.setCriadoEm(agora);
        user.setAtualizadoEm(agora);
        assertEquals(agora, user.getCriadoEm());
        assertEquals(agora, user.getAtualizadoEm());
    }

    @Test
    @DisplayName("BookInfo getters e setters")
    void bookInfoGettersSetters() {
        BookInfo info = new BookInfo();
        info.setTitulo("Título");
        info.setAutor("Autor");
        info.setEditora("Editora");
        info.setAnoPublicacao(2020);
        info.setCapa("http://capa.jpg");

        assertEquals("Título", info.getTitulo());
        assertEquals("Autor", info.getAutor());
        assertEquals("Editora", info.getEditora());
        assertEquals(2020, info.getAnoPublicacao());
        assertEquals("http://capa.jpg", info.getCapa());
    }
}