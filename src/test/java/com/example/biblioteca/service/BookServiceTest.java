package com.example.biblioteca.service;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.entity.Book;
import com.example.biblioteca.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import com.example.biblioteca.MongoTestBase;
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookServiceTest extends MongoTestBase {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void limpar() {
        bookRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Deve salvar e recuperar um livro por ID")
    void deveSalvarERecuperarLivro() {
        Book livro = new Book("Dom Casmurro", "Machado de Assis", 1899, "user1");
        Book salvo = bookService.salvar(livro);

        assertNotNull(salvo.getId());
        Optional<Book> encontrado = bookService.buscarPorId(salvo.getId(), "user1");
        assertTrue(encontrado.isPresent());
        assertEquals("Dom Casmurro", encontrado.get().getTitulo());
    }

    @Test
    @Order(2)
    @DisplayName("Deve listar apenas livros do usuário dono")
    void deveListarApenasLivrosDoUsuario() {
        bookService.salvar(new Book("Livro A", "Autor A", 2000, "user1"));
        bookService.salvar(new Book("Livro B", "Autor B", 2001, "user2"));

        List<Book> livrosUser1 = bookService.listarTodos("user1");
        assertEquals(1, livrosUser1.size());
        assertEquals("Livro A", livrosUser1.get(0).getTitulo());
    }

    @Test
    @Order(3)
    @DisplayName("Deve deletar livro por ID")
    void deveDeletarLivro() {
        Book livro = bookService.salvar(new Book("Para Deletar", "Autor", 2020, "user1"));
        String id = livro.getId();

        bookService.deletar(id);

        Optional<Book> resultado = bookService.buscarPorId(id, "user1");
        assertFalse(resultado.isPresent());
    }

    @Test
    @Order(4)
    @DisplayName("Deve atualizar status de leitura")
    void deveAtualizarStatusLeitura() {
        Book livro = bookService.salvar(new Book("Livro Status", "Autor", 2020, "user1"));
        assertEquals(Book.StatusLeitura.NAO_LIDO, livro.getStatus());

        livro.setStatus(Book.StatusLeitura.LIDO);
        Book atualizado = bookService.salvar(livro);

        assertEquals(Book.StatusLeitura.LIDO, atualizado.getStatus());
    }

    @Test
    @Order(5)
    @DisplayName("Deve filtrar livros por status")
    void deveFiltrarPorStatus() {
        Book l1 = new Book("L1", "A", 2000, "user1"); l1.setStatus(Book.StatusLeitura.LIDO);
        Book l2 = new Book("L2", "B", 2001, "user1"); l2.setStatus(Book.StatusLeitura.LENDO);
        Book l3 = new Book("L3", "C", 2002, "user1"); l3.setStatus(Book.StatusLeitura.LIDO);
        bookService.salvar(l1); bookService.salvar(l2); bookService.salvar(l3);

        List<Book> lidos = bookService.filtrarPorStatus("user1", Book.StatusLeitura.LIDO);
        assertEquals(2, lidos.size());
    }

    @Test
    @Order(6)
    @DisplayName("Deve buscar livros por título parcial")
    void deveBuscarPorTitulo() {
        bookService.salvar(new Book("O Senhor dos Anéis", "Tolkien", 1954, "user1"));
        bookService.salvar(new Book("O Hobbit", "Tolkien", 1937, "user1"));
        bookService.salvar(new Book("Duna", "Herbert", 1965, "user1"));

        List<Book> resultados = bookService.buscarPorTitulo("user1", "o");
        assertTrue(resultados.size() >= 2);
    }

    @ParameterizedTest
    @Order(7)
    @DisplayName("Deve validar ISBNs corretos")
    @ValueSource(strings = {"9780142437230", "0142437239", "978-0-14-243723-0"})
    void deveValidarIsbnCorreto(String isbn) {
        assertTrue(bookService.isIsbnValido(isbn), "ISBN deveria ser válido: " + isbn);
    }

    @ParameterizedTest
    @Order(8)
    @DisplayName("Deve rejeitar ISBNs inválidos")
    @ValueSource(strings = {"123", "12345678901234", "abcdefghij"})
    void deveRejeitarIsbnInvalido(String isbn) {
        assertFalse(bookService.isIsbnValido(isbn), "ISBN deveria ser inválido: " + isbn);
    }

    @Test
    @Order(9)
    @DisplayName("ISBN nulo ou vazio deve ser considerado válido (opcional)")
    void isbnNuloOuVazioEhValido() {
        assertTrue(bookService.isIsbnValido(null));
        assertTrue(bookService.isIsbnValido(""));
        assertTrue(bookService.isIsbnValido("   "));
    }

    @ParameterizedTest
    @Order(10)
    @DisplayName("Deve validar ano de publicação com múltiplos cenários")
    @CsvSource({
            "1605, true",
            "2025, true",
            "999, false",
            "2030, false",
            "0, false"
    })
    void deveValidarAnoPublicacao(int ano, boolean esperado) {
        assertEquals(esperado, bookService.isAnoValido(ano),
                "Ano " + ano + " deveria ser " + (esperado ? "válido" : "inválido"));
    }

    @Test
    @Order(11)
    @DisplayName("Ano nulo deve ser inválido")
    void anoNuloDeveSerInvalido() {
        assertFalse(bookService.isAnoValido(null));
    }

    @Test
    @Order(12)
    @DisplayName("Deve calcular estatísticas corretamente")
    void deveCalcularEstatisticas() {
        Book l1 = new Book("L1", "A", 2000, "user1"); l1.setStatus(Book.StatusLeitura.LIDO);
        Book l2 = new Book("L2", "B", 2001, "user1"); l2.setStatus(Book.StatusLeitura.LENDO);
        Book l3 = new Book("L3", "C", 2002, "user1"); l3.setStatus(Book.StatusLeitura.NAO_LIDO);
        bookService.salvar(l1); bookService.salvar(l2); bookService.salvar(l3);

        var stats = bookService.calcularEstatisticas("user1");
        assertEquals(3, stats.total());
        assertEquals(1, stats.lidos());
        assertEquals(1, stats.lendo());
        assertEquals(1, stats.naoLidos());
    }

    @Test
    @Order(13)
    @DisplayName("ISBN já cadastrado deve retornar true")
    void deveDetectarIsbnDuplicado() {
        Book livro = new Book("Livro ISBN", "Autor", 2020, "user1");
        livro.setIsbn("9780142437230");
        bookService.salvar(livro);

        assertTrue(bookService.isbnJaCadastrado("9780142437230", "user1"));
        assertFalse(bookService.isbnJaCadastrado("9780142437230", "user2"));
    }

    @Test
    @Order(14)
    @DisplayName("buscarPorId de outro usuário deve retornar vazio")
    void buscaPorIdDeOutroUsuarioRetornaVazio() {
        Book livro = bookService.salvar(new Book("Livro Privado", "Autor", 2020, "user1"));
        Optional<Book> resultado = bookService.buscarPorId(livro.getId(), "user2");
        assertFalse(resultado.isPresent());
    }

    @Test
    @Order(15)
    @DisplayName("Filtrar por status sem resultados deve retornar lista vazia")
    void filtrarPorStatusSemResultados() {

        List<Book> resultado =
                bookService.filtrarPorStatus("user1", Book.StatusLeitura.LIDO);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(16)
    @DisplayName("Buscar título inexistente deve retornar lista vazia")
    void buscarTituloInexistente() {

        bookService.salvar(new Book("Duna", "Herbert", 1965, "user1"));

        List<Book> resultado =
                bookService.buscarPorTitulo("user1", "Harry Potter");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(17)
    @DisplayName("Estatísticas sem livros devem retornar zero")
    void estatisticasSemLivros() {

        var stats = bookService.calcularEstatisticas("user1");

        assertEquals(0, stats.total());
        assertEquals(0, stats.lidos());
        assertEquals(0, stats.lendo());
        assertEquals(0, stats.naoLidos());
    }

    @Test
    @Order(18)
    @DisplayName("Deletar ID inexistente não deve lançar erro")
    void deletarIdInexistente() {

        assertDoesNotThrow(() ->
                bookService.deletar("id-inexistente"));
    }
}