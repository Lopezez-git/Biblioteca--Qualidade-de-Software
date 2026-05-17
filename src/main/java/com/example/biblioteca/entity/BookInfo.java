package com.example.biblioteca.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para resposta da Open Library API (usada com VCR).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookInfo {

    private String titulo;
    private String autor;
    private String editora;
    private Integer anoPublicacao;
    private String capa;

    @JsonProperty("title")
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTitulo() { return titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getEditora() { return editora; }
    public void setEditora(String editora) { this.editora = editora; }

    public Integer getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(Integer anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public String getCapa() { return capa; }
    public void setCapa(String capa) { this.capa = capa; }
}
