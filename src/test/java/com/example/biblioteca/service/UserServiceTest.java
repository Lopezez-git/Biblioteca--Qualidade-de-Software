package com.example.biblioteca.service;

import com.example.biblioteca.MongoTestBase;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest extends MongoTestBase {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void limpar() {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Deve cadastrar usuário com senha criptografada")
    void deveCadastrarUsuario() {
        User user = new User("joao", "joao@email.com", "senha123");
        User salvo = userService.cadastrar(user);

        assertNotNull(salvo.getId());
        assertNotEquals("senha123", salvo.getPassword());
        assertTrue(salvo.isAtivo());
        assertEquals("ROLE_USER", salvo.getRoles().get(0));
    }

    @Test
    @Order(2)
    @DisplayName("Deve detectar username indisponível")
    void deveDetectarUsernameIndisponivel() {
        userService.cadastrar(new User("maria", "maria@email.com", "senha123"));

        assertFalse(userService.usernameDisponivel("maria"));
        assertTrue(userService.usernameDisponivel("outro"));
    }

    @Test
    @Order(3)
    @DisplayName("Deve detectar email indisponível")
    void deveDetectarEmailIndisponivel() {
        userService.cadastrar(new User("pedro", "pedro@email.com", "senha123"));

        assertFalse(userService.emailDisponivel("pedro@email.com"));
        assertTrue(userService.emailDisponivel("novo@email.com"));
    }

    @ParameterizedTest
    @Order(4)
    @DisplayName("Deve validar emails corretos")
    @ValueSource(strings = {"teste@email.com", "user.name+tag@domain.co.uk", "a@b.io"})
    void deveValidarEmailCorreto(String email) {
        assertTrue(userService.isEmailValido(email));
    }

    @ParameterizedTest
    @Order(5)
    @DisplayName("Deve rejeitar emails inválidos")
    @ValueSource(strings = {"invalido", "sem@", "@semdominio.com", "", "  "})
    void deveRejeitarEmailInvalido(String email) {
        assertFalse(userService.isEmailValido(email));
    }

    @Test
    @Order(6)
    @DisplayName("Email nulo deve ser inválido")
    void emailNuloDeveSerInvalido() {
        assertFalse(userService.isEmailValido(null));
    }

    @ParameterizedTest
    @Order(7)
    @DisplayName("Deve validar força da senha com múltiplos cenários")
    @CsvSource({
            "abc123, true",
            "senha_forte, true",
            "12345, false",
            "'', false"
    })
    void deveValidarForcaSenha(String senha, boolean esperado) {
        assertEquals(esperado, userService.isSenhaForte(senha));
    }

    @Test
    @Order(8)
    @DisplayName("Senha nula deve ser fraca")
    void senhaNulaDeveSerFraca() {
        assertFalse(userService.isSenhaForte(null));
    }

    @Test
    @Order(9)
    @DisplayName("Deve buscar usuário por username")
    void deveBuscarPorUsername() {
        userService.cadastrar(new User("ana", "ana@email.com", "senha123"));
        assertTrue(userService.buscarPorUsername("ana").isPresent());
        assertFalse(userService.buscarPorUsername("naoexiste").isPresent());
    }

    @Test
    @Order(10)
    @DisplayName("Deve buscar usuário por email")
    void deveBuscarPorEmail() {
        userService.cadastrar(new User("carlos", "carlos@email.com", "senha123"));
        assertTrue(userService.buscarPorEmail("carlos@email.com").isPresent());
        assertFalse(userService.buscarPorEmail("nao@existe.com").isPresent());
    }

    @Test
    @Order(11)
    @DisplayName("Username nulo deve ser considerado disponível")
    void usernameNuloDeveSerDisponivel() {
        assertTrue(userService.usernameDisponivel(null));
    }

    @Test
    @Order(12)
    @DisplayName("Email nulo deve ser considerado disponível")
    void emailNuloDeveSerDisponivel() {
        assertTrue(userService.emailDisponivel(null));
    }

}