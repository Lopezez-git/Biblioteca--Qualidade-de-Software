package com.example.biblioteca.controller;

import com.example.biblioteca.entity.User;
import com.example.biblioteca.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/cadastro")
    public String cadastroPage(Model model) {
        model.addAttribute("usuario", new User());
        return "auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@ModelAttribute User usuario,
                            @RequestParam String confirmarSenha,
                            RedirectAttributes ra) {
        if (!usuario.getPassword().equals(confirmarSenha)) {
            ra.addFlashAttribute("erro", "As senhas não coincidem");
            return "redirect:/auth/cadastro";
        }
        if (!userService.isEmailValido(usuario.getEmail())) {
            ra.addFlashAttribute("erro", "Email inválido");
            return "redirect:/auth/cadastro";
        }
        if (!userService.isSenhaForte(usuario.getPassword())) {
            ra.addFlashAttribute("erro", "Senha deve ter no mínimo 6 caracteres");
            return "redirect:/auth/cadastro";
        }
        if (!userService.usernameDisponivel(usuario.getUsername())) {
            ra.addFlashAttribute("erro", "Username já em uso");
            return "redirect:/auth/cadastro";
        }
        if (!userService.emailDisponivel(usuario.getEmail())) {
            ra.addFlashAttribute("erro", "Email já cadastrado");
            return "redirect:/auth/cadastro";
        }

        userService.cadastrar(usuario);
        ra.addFlashAttribute("sucesso", "Conta criada com sucesso! Faça login.");
        return "redirect:/auth/login";
    }
}
