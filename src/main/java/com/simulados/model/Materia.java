package com.simulados.model;

/**
 * Classe Model que representa a tabela Materia do banco de dados
 */
public class Materia {

    private Integer idMateria;
    private String nome;

    // Construtor vazio
    public Materia() {
    }

    // Construtor completo
    public Materia(Integer idMateria, String nome) {
        this.idMateria = idMateria;
        this.nome = nome;
    }

    // Construtor sem ID (para inserção)
    public Materia(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Integer getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Integer idMateria) {
        this.idMateria = idMateria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Materia{" +
                "idMateria=" + idMateria +
                ", nome='" + nome + '\'' +
                '}';
    }
}
