package com.example.biblioteca.service;

import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User cadastrar(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("ROLE_USER"));
        return userRepository.save(user);
    }

    public Optional<User> buscarPorUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean usernameDisponivel(String username) {
        return !userRepository.existsByUsername(username);
    }

    public boolean emailDisponivel(String email) {
        return !userRepository.existsByEmail(email);
    }

    public boolean isEmailValido(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public boolean isSenhaForte(String senha) {
        if (senha == null || senha.length() < 6) return false;
        return true; // mínimo 6 chars para o projeto
    }
}
