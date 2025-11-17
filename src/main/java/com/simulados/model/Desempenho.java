package com.simulados.model;

/**
 * Classe Model que representa a tabela Desempenho do banco de dados
 * Relacionamentos:
 * - Pertence a um Usuario (FK: id_usuario)
 * - Relaciona-se com um Curso (FK: id_curso)
 * - Relaciona-se com uma Materia (FK: id_materia)
 */
public class Desempenho {

    private Integer idDesempenho;
    private Integer idUsuario;      // Foreign Key para Usuario
    private Integer idCurso;        // Foreign Key para Curso
    private Integer idMateria;      // Foreign Key para Materia

    // Construtor vazio
    public Desempenho() {
    }

    // Construtor completo
    public Desempenho(Integer idDesempenho, Integer idUsuario, Integer idCurso, Integer idMateria) {
        this.idDesempenho = idDesempenho;
        this.idUsuario = idUsuario;
        this.idCurso = idCurso;
        this.idMateria = idMateria;
    }

    // Construtor sem ID (para inserção)
    public Desempenho(Integer idUsuario, Integer idCurso, Integer idMateria) {
        this.idUsuario = idUsuario;
        this.idCurso = idCurso;
        this.idMateria = idMateria;
    }

    // Getters e Setters
    public Integer getIdDesempenho() {
        return idDesempenho;
    }

    public void setIdDesempenho(Integer idDesempenho) {
        this.idDesempenho = idDesempenho;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Integer idCurso) {
        this.idCurso = idCurso;
    }

    public Integer getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Integer idMateria) {
        this.idMateria = idMateria;
    }

    @Override
    public String toString() {
        return "Desempenho{" +
                "idDesempenho=" + idDesempenho +
                ", idUsuario=" + idUsuario +
                ", idCurso=" + idCurso +
                ", idMateria=" + idMateria +
                '}';
    }
}
