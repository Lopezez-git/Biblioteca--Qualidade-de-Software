package com.example.biblioteca.controller;

import com.example.biblioteca.entity.Book;
import com.example.biblioteca.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/livros")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public String listar(@AuthenticationPrincipal UserDetails user,
                         @RequestParam(required = false) String status,
                         @RequestParam(required = false) String busca,
                         Model model) {
        var livros = bookService.listarTodos(user.getUsername());

        if (status != null && !status.isBlank()) {
            try {
                Book.StatusLeitura s = Book.StatusLeitura.valueOf(status);
                livros = bookService.filtrarPorStatus(user.getUsername(), s);
            } catch (IllegalArgumentException ignored) {}
        } else if (busca != null && !busca.isBlank()) {
            livros = bookService.buscarPorTitulo(user.getUsername(), busca);
        }

        var stats = bookService.calcularEstatisticas(user.getUsername());
        model.addAttribute("livros", livros);
        model.addAttribute("estatisticas", stats);
        model.addAttribute("statusOptions", Book.StatusLeitura.values());
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("busca", busca);
        return "book/list";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("livro", new Book());
        model.addAttribute("statusOptions", Book.StatusLeitura.values());
        model.addAttribute("titulo_pagina", "Novo Livro");
        return "book/form";
    }

    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable String id,
                             @AuthenticationPrincipal UserDetails user,
                             Model model) {
        Optional<Book> livro = bookService.buscarPorId(id, user.getUsername());
        if (livro.isEmpty()) return "redirect:/livros";
        model.addAttribute("livro", livro.get());
        model.addAttribute("statusOptions", Book.StatusLeitura.values());
        model.addAttribute("titulo_pagina", "Editar Livro");
        return "book/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Book livro,
                         @AuthenticationPrincipal UserDetails user,
                         RedirectAttributes ra) {
        if (!bookService.isAnoValido(livro.getAnoPublicacao())) {
            ra.addFlashAttribute("erro", "Ano de publicação inválido");
            return "redirect:/livros/novo";
        }
        livro.setDonoUsername(user.getUsername());
        bookService.salvar(livro);
        ra.addFlashAttribute("sucesso", "Livro salvo com sucesso!");
        return "redirect:/livros";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable String id,
                          @AuthenticationPrincipal UserDetails user,
                          RedirectAttributes ra) {
        Optional<Book> livro = bookService.buscarPorId(id, user.getUsername());
        if (livro.isPresent()) {
            bookService.deletar(id);
            ra.addFlashAttribute("sucesso", "Livro removido com sucesso!");
        }
        return "redirect:/livros";
    }
}
