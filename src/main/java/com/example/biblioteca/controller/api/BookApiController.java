package com.example.biblioteca.controller.api;

import com.example.biblioteca.entity.Book;
import com.example.biblioteca.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class BookApiController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> listarTodos(@AuthenticationPrincipal UserDetails user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(bookService.listarTodos(user.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> buscarPorId(@PathVariable String id,
                                            @AuthenticationPrincipal UserDetails user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Optional<Book> book = bookService.buscarPorId(id, user.getUsername());
        return book.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody Book book,
                                        @AuthenticationPrincipal UserDetails user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!bookService.isAnoValido(book.getAnoPublicacao())) {
            return ResponseEntity.badRequest().body("Ano de publicação inválido");
        }
        if (!bookService.isIsbnValido(book.getIsbn())) {
            return ResponseEntity.badRequest().body("ISBN inválido");
        }
        if (book.getIsbn() != null && !book.getIsbn().isBlank()
                && bookService.isbnJaCadastrado(book.getIsbn(), user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("ISBN já cadastrado");
        }

        book.setDonoUsername(user.getUsername());
        Book salvo = bookService.salvar(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable String id,
                                            @RequestBody Book book,
                                            @AuthenticationPrincipal UserDetails user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Book> existente = bookService.buscarPorId(id, user.getUsername());
        if (existente.isEmpty()) return ResponseEntity.notFound().build();

        if (!bookService.isAnoValido(book.getAnoPublicacao())) {
            return ResponseEntity.badRequest().body("Ano de publicação inválido");
        }

        book.setId(id);
        book.setDonoUsername(user.getUsername());
        book.setCriadoEm(existente.get().getCriadoEm());
        return ResponseEntity.ok(bookService.salvar(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id,
                                        @AuthenticationPrincipal UserDetails user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Book> existente = bookService.buscarPorId(id, user.getUsername());
        if (existente.isEmpty()) return ResponseEntity.notFound().build();

        bookService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<BookService.EstatisticasBiblioteca> estatisticas(
            @AuthenticationPrincipal UserDetails user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(bookService.calcularEstatisticas(user.getUsername()));
    }
}
