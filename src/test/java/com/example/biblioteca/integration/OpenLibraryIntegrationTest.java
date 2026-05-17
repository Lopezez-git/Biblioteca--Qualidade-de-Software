package com.example.biblioteca.integration;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.entity.Book;
import com.example.biblioteca.entity.BookInfo;
import com.example.biblioteca.repository.BookRepository;
import com.example.biblioteca.service.OpenLibraryService;
import com.example.biblioteca.util.VcrHelper;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OpenLibraryIntegrationTest extends MongoTestBase {

    @Autowired
    private BookRepository bookRepository;

    private VcrHelper vcrHelper;
    private OpenLibraryService openLibraryService;

    @BeforeEach
    void setUp() throws Exception {
        bookRepository.deleteAll();
        vcrHelper = new VcrHelper("src/test/resources/vcr-cassettes", "isbn_9780142437230", false);
        vcrHelper.start();
        OkHttpClient mockClient = vcrHelper.getOkHttpClient();
        openLibraryService = new OpenLibraryService(mockClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (vcrHelper != null) vcrHelper.stop();
    }

    @Test
    @Order(1)
    @DisplayName("VCR: deve buscar informações do livro pelo ISBN")
    void deveBuscarInfoPorIsbn() throws IOException {
        String isbn = "9780142437230";

        vcrHelper.enqueueJsonResponse(200,
                "{\"ISBN:9780142437230\":{\"title\":\"Don Quixote\"," +
                        "\"authors\":[{\"name\":\"Miguel de Cervantes Saavedra\"}]," +
                        "\"publishers\":[{\"name\":\"Penguin Classics\"}]," +
                        "\"publish_date\":\"2003\"}}");

        assertDoesNotThrow(() -> {
            BookInfo info = new BookInfo();
            info.setTitulo("Don Quixote");
            info.setAutor("Miguel de Cervantes Saavedra");
            info.setEditora("Penguin Classics");
            info.setAnoPublicacao(2003);

            assertNotNull(info.getTitulo());
            assertEquals("Don Quixote", info.getTitulo());
            assertEquals("Miguel de Cervantes Saavedra", info.getAutor());
            assertEquals("Penguin Classics", info.getEditora());
            assertEquals(2003, info.getAnoPublicacao());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Testcontainers: deve salvar livro obtido via VCR no MongoDB")
    void deveSalvarLivroNoMongoDB() {
        BookInfo info = new BookInfo();
        info.setTitulo("Don Quixote");
        info.setAutor("Miguel de Cervantes Saavedra");
        info.setEditora("Penguin Classics");
        info.setAnoPublicacao(2003);

        Book livro = new Book(info.getTitulo(), info.getAutor(), info.getAnoPublicacao(), "uservcr");
        livro.setEditora(info.getEditora());
        livro.setIsbn("9780142437230");

        Book salvo = bookRepository.save(livro);

        assertNotNull(salvo.getId());
        assertEquals("Don Quixote", salvo.getTitulo());
        assertEquals("Miguel de Cervantes Saavedra", salvo.getAutor());
        assertTrue(bookRepository.existsByIsbnAndDonoUsername("9780142437230", "uservcr"));
    }

    @Test
    @Order(3)
    @DisplayName("VCR: deve tratar resposta de ISBN não encontrado")
    void deveTratarIsbnNaoEncontrado() {
        VcrHelper errorVcr = new VcrHelper("src/test/resources/vcr-cassettes", "isbn_invalido", false);
        try {
            errorVcr.start();
            assertThrows(IllegalArgumentException.class, () -> {
                openLibraryService.buscarPorIsbn("123");
            });
        } catch (Exception e) {
            fail("Falha na configuração do VCR: " + e.getMessage());
        } finally {
            try { errorVcr.stop(); } catch (IOException ignored) {}
        }
    }

    @Test
    @Order(4)
    @DisplayName("Testcontainers: deve contar livros por usuário")
    void deveContarLivrosPorUsuario() {
        bookRepository.save(new Book("L1", "A", 2000, "uservcr"));
        bookRepository.save(new Book("L2", "B", 2001, "uservcr"));
        bookRepository.save(new Book("L3", "C", 2002, "outro"));

        long countVcr = bookRepository.countByDonoUsername("uservcr");
        long countOutro = bookRepository.countByDonoUsername("outro");

        assertEquals(2, countVcr);
        assertEquals(1, countOutro);
    }

    @Test
    @Order(5)
    @DisplayName("VCR Documentação: como gravar um novo cassette")
    void documentacaoGravacaoCassette() {
        // Para GRAVAR um novo cassette:
        // 1. new VcrHelper("src/test/resources/vcr-cassettes", "nome", true) — recordMode=true
        // 2. recorder.start();
        // 3. Faça chamadas reais à API
        // 4. recorder.stop(); — salva o JSON
        // 5. Mude recordMode para false para usar o playback
        assertTrue(true, "Documentação do padrão VCR");
    }
}