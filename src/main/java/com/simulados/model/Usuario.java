package com.simulados.model;

import java.time.LocalDateTime;

/**
 * Classe Model que representa a tabela Usuario do banco de dados
 */
public class Usuario {

    // Atributos que correspondem às colunas da tabela
    private Integer idUsuario;
    private String nome;
    private String email;
    private String senha;
    private LocalDateTime dataCadastro;

    // Construtor vazio (necessário para algumas operações)
    public Usuario() {
    }

    // Construtor com todos os campos
    public Usuario(Integer idUsuario, String nome, String email, String senha, LocalDateTime dataCadastro) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = dataCadastro;
    }

    // Construtor sem ID (para inserção - o ID será gerado pelo banco)
    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = LocalDateTime.now();
    }

    // Getters e Setters
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    // toString para facilitar debug
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}
