package com.example.biblioteca.controller.api;

import com.example.biblioteca.entity.BookInfo;
import com.example.biblioteca.service.OpenLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/open-library")
@CrossOrigin(origins = "*")
public class OpenLibraryApiController {

    @Autowired
    private OpenLibraryService openLibraryService;

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Object> buscarPorIsbn(@PathVariable String isbn) {
        try {
            BookInfo info = openLibraryService.buscarPorIsbn(isbn);
            return ResponseEntity.ok(info);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
