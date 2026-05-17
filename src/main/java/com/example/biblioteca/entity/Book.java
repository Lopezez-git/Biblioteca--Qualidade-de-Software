package com.example.biblioteca.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "books")
public class Book {

    @Id
    private String id;

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotBlank(message = "Autor é obrigatório")
    private String autor;

    private String isbn;

    @NotNull(message = "Ano de publicação é obrigatório")
    private Integer anoPublicacao;

    private String genero;
    private String editora;
    private String descricao;
    private StatusLeitura status;
    private Integer avaliacaoNota; // 1-5
    private String notas;

    private String donoUsername;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public enum StatusLeitura {
        NAO_LIDO, LENDO, LIDO, RELENDO, ABANDONADO
    }

    public Book() {
        this.status = StatusLeitura.NAO_LIDO;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public Book(String titulo, String autor, Integer anoPublicacao, String donoUsername) {
        this();
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.donoUsername = donoUsername;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
        this.atualizadoEm = LocalDateTime.now();
    }

    public String getAutor() { return autor; }
    public void setAutor(String autor) {
        this.autor = autor;
        this.atualizadoEm = LocalDateTime.now();
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Integer getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(Integer anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getEditora() { return editora; }
    public void setEditora(String editora) { this.editora = editora; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public StatusLeitura getStatus() { return status; }
    public void setStatus(StatusLeitura status) { this.status = status; }

    public Integer getAvaliacaoNota() { return avaliacaoNota; }
    public void setAvaliacaoNota(Integer avaliacaoNota) { this.avaliacaoNota = avaliacaoNota; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getDonoUsername() { return donoUsername; }
    public void setDonoUsername(String donoUsername) { this.donoUsername = donoUsername; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
