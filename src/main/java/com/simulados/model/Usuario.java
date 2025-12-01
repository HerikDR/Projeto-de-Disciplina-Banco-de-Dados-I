package com.simulados.model;

import java.sql.Timestamp;

public class Usuario {
    private int idUsuario;
    private String nome;
    private String email;
    private String senha;
    private Timestamp dataCadastro;
    private String tipoUsuario;  // NOVO CAMPO

    // Constantes para tipos de usuário
    public static final String TIPO_ALUNO = "ALUNO";
    public static final String TIPO_ADMIN = "ADMIN";

    // Construtor vazio
    public Usuario() {}

    // Construtor completo
    public Usuario(int idUsuario, String nome, String email, String senha,
                   Timestamp dataCadastro, String tipoUsuario) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = dataCadastro;
        this.tipoUsuario = tipoUsuario != null ? tipoUsuario : TIPO_ALUNO;
    }

    // Construtor sem ID (para cadastro)
    public Usuario(String nome, String email, String senha, String tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario != null ? tipoUsuario : TIPO_ALUNO;
    }

    // Getters e Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
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

    public Timestamp getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Timestamp dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    // Métodos auxiliares para verificar tipo
    public boolean isAdmin() {
        return TIPO_ADMIN.equals(this.tipoUsuario);
    }

    public boolean isAluno() {
        return TIPO_ALUNO.equals(this.tipoUsuario);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}
