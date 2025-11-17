package com.simulados.model;

/**
 * Classe Model que representa a tabela Curso do banco de dados
 * Relacionamento: Um Curso pertence a um Usuario (FK: id_usuario)
 */
public class Curso {

    private Integer idCurso;
    private Integer idUsuario;      // Foreign Key para Usuario
    private String nomeCurso;

    // Construtor vazio
    public Curso() {
    }

    // Construtor completo
    public Curso(Integer idCurso, Integer idUsuario, String nomeCurso) {
        this.idCurso = idCurso;
        this.idUsuario = idUsuario;
        this.nomeCurso = nomeCurso;
    }

    // Construtor sem ID (para inserção)
    public Curso(Integer idUsuario, String nomeCurso) {
        this.idUsuario = idUsuario;
        this.nomeCurso = nomeCurso;
    }

    // Getters e Setters
    public Integer getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Integer idCurso) {
        this.idCurso = idCurso;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "idCurso=" + idCurso +
                ", idUsuario=" + idUsuario +
                ", nomeCurso='" + nomeCurso + '\'' +
                '}';
    }
}
