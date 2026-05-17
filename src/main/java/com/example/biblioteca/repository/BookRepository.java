package com.example.biblioteca.repository;

import com.example.biblioteca.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> findByDonoUsername(String donoUsername);

    List<Book> findByDonoUsernameAndStatus(String donoUsername, Book.StatusLeitura status);

    List<Book> findByDonoUsernameAndAutorContainingIgnoreCase(String donoUsername, String autor);

    List<Book> findByDonoUsernameAndTituloContainingIgnoreCase(String donoUsername, String titulo);

    boolean existsByIsbnAndDonoUsername(String isbn, String donoUsername);

    Optional<Book> findByIdAndDonoUsername(String id, String donoUsername);

    long countByDonoUsername(String donoUsername);

    long countByDonoUsernameAndStatus(String donoUsername, Book.StatusLeitura status);
}
