package com.example.biblioteca.controller.api;

import com.example.biblioteca.entity.User;
import com.example.biblioteca.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UserApiController {

    @Autowired
    private UserService userService;

    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrar(@RequestBody User user) {
        if (!userService.isEmailValido(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email inválido");
        }
        if (!userService.isSenhaForte(user.getPassword())) {
            return ResponseEntity.badRequest().body("Senha deve ter no mínimo 6 caracteres");
        }
        if (!userService.usernameDisponivel(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username já em uso");
        }
        if (!userService.emailDisponivel(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email já cadastrado");
        }

        User salvo = userService.cadastrar(user);
        salvo.setPassword(null); // não retornar a senha
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/disponibilidade")
    public ResponseEntity<Map<String, Boolean>> verificarDisponibilidade(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {

        Map<String, Boolean> result = new java.util.HashMap<>();
        if (username != null) result.put("usernameDisponivel", userService.usernameDisponivel(username));
        if (email != null) result.put("emailDisponivel", userService.emailDisponivel(email));
        return ResponseEntity.ok(result);
    }
}
