package com.example.biblioteca.controller;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.entity.Book;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.BookRepository;
import com.example.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerTest extends MongoTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String USUARIO = "mvc_user";

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();

        User u = new User(
                USUARIO,
                "mvc@email.com",
                passwordEncoder.encode("senha123")
        );

        u.setRoles(List.of("ROLE_USER"));

        userRepository.save(u);
    }

    @Test
    @Order(1)
    @DisplayName("GET /livros redireciona para login quando não autenticado")
    void deveRedirecionarParaLoginSemAuth() throws Exception {

        mockMvc.perform(get("/livros"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Order(2)
    @DisplayName("GET /livros retorna 200 para usuário autenticado")
    void deveRetornarListaParaUsuarioAutenticado() throws Exception {

        mockMvc.perform(
                        get("/livros")
                                .with(user(USUARIO).roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("book/list"));
    }

    @Test
    @Order(3)
    @DisplayName("GET /livros/novo retorna formulário")
    void deveRetornarFormularioNovo() throws Exception {

        mockMvc.perform(
                        get("/livros/novo")
                                .with(user(USUARIO).roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("book/form"))
                .andExpect(model().attributeExists("livro"));
    }

    @Test
    @Order(4)
    @DisplayName("POST /livros/salvar salva e redireciona")
    void deveSalvarLivroERedirecionar() throws Exception {

        mockMvc.perform(
                        post("/livros/salvar")
                                .with(user(USUARIO).roles("USER"))
                                .with(csrf())
                                .param("titulo", "Memórias Póstumas")
                                .param("autor", "Machado de Assis")
                                .param("anoPublicacao", "1881")
                                .param("status", "NAO_LIDO")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/livros"));
    }

    @Test
    @Order(5)
    @DisplayName("GET /livros/editar/{id} retorna formulário de edição")
    void deveRetornarFormularioEdicao() throws Exception {

        Book livro = bookRepository.save(
                new Book("Livro Edit", "Autor", 2020, USUARIO)
        );

        mockMvc.perform(
                        get("/livros/editar/" + livro.getId())
                                .with(user(USUARIO).roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("book/form"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /livros/editar/{id} redireciona se livro não for do usuário")
    void deveRedirecionarSeNaoForDono() throws Exception {

        Book livro = bookRepository.save(
                new Book("Livro Alheio", "Autor", 2020, "outro")
        );

        mockMvc.perform(
                        get("/livros/editar/" + livro.getId())
                                .with(user(USUARIO).roles("USER"))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/livros"));
    }

    @Test
    @Order(7)
    @DisplayName("POST /livros/deletar/{id} remove e redireciona")
    void deveDeletarERedirecionar() throws Exception {

        Book livro = bookRepository.save(
                new Book("Para Remover", "Autor", 2020, USUARIO)
        );

        mockMvc.perform(
                        post("/livros/deletar/" + livro.getId())
                                .with(user(USUARIO).roles("USER"))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/livros"));
    }

    @Test
    @Order(8)
    @DisplayName("GET /livros com filtro de status retorna view correta")
    void deveFiltrarPorStatus() throws Exception {

        mockMvc.perform(
                        get("/livros")
                                .with(user(USUARIO).roles("USER"))
                                .param("status", "LIDO")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("book/list"));
    }

    @Test
    @Order(9)
    @DisplayName("GET /livros com busca retorna view correta")
    void deveBuscarPorTitulo() throws Exception {

        mockMvc.perform(
                        get("/livros")
                                .with(user(USUARIO).roles("USER"))
                                .param("busca", "machado")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("book/list"));
    }
}