package com.example.biblioteca.service;

import com.example.biblioteca.entity.Book;
import com.example.biblioteca.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> listarTodos(String donoUsername) {
        return bookRepository.findByDonoUsername(donoUsername);
    }

    public Optional<Book> buscarPorId(String id, String donoUsername) {
        return bookRepository.findByIdAndDonoUsername(id, donoUsername);
    }

    public Book salvar(Book book) {
        book.setAtualizadoEm(LocalDateTime.now());
        return bookRepository.save(book);
    }

    public void deletar(String id) {
        bookRepository.deleteById(id);
    }

    public List<Book> filtrarPorStatus(String donoUsername, Book.StatusLeitura status) {
        return bookRepository.findByDonoUsernameAndStatus(donoUsername, status);
    }

    public List<Book> buscarPorTitulo(String donoUsername, String titulo) {
        return bookRepository.findByDonoUsernameAndTituloContainingIgnoreCase(donoUsername, titulo);
    }

    public List<Book> buscarPorAutor(String donoUsername, String autor) {
        return bookRepository.findByDonoUsernameAndAutorContainingIgnoreCase(donoUsername, autor);
    }

    public boolean isbnJaCadastrado(String isbn, String donoUsername) {
        if (isbn == null || isbn.isBlank()) return false;
        return bookRepository.existsByIsbnAndDonoUsername(isbn, donoUsername);
    }

    public boolean isIsbnValido(String isbn) {
        if (isbn == null || isbn.isBlank()) return true; // ISBN é opcional
        String limpo = isbn.replaceAll("[^0-9X]", "");
        return limpo.length() == 10 || limpo.length() == 13;
    }

    public boolean isAnoValido(Integer ano) {
        if (ano == null) return false;
        int anoAtual = LocalDateTime.now().getYear();
        return ano >= 1000 && ano <= anoAtual + 1;
    }

    public EstatisticasBiblioteca calcularEstatisticas(String donoUsername) {
        long total = bookRepository.countByDonoUsername(donoUsername);
        long lidos = bookRepository.countByDonoUsernameAndStatus(donoUsername, Book.StatusLeitura.LIDO);
        long lendo = bookRepository.countByDonoUsernameAndStatus(donoUsername, Book.StatusLeitura.LENDO);
        long naoLidos = bookRepository.countByDonoUsernameAndStatus(donoUsername, Book.StatusLeitura.NAO_LIDO);
        return new EstatisticasBiblioteca(total, lidos, lendo, naoLidos);
    }

    public record EstatisticasBiblioteca(long total, long lidos, long lendo, long naoLidos) {}
}
