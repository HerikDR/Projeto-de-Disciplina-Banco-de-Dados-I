package com.simulados.model;

import java.time.LocalDateTime;

/**
 * Classe Model que representa a tabela Simulado do banco de dados
 * Relacionamento: Um Simulado pertence a um Usuario (FK: id_usuario)
 */
public class Simulado {

    private Integer idSimulado;
    private Integer idUsuario;              // Foreign Key para Usuario
    private LocalDateTime dataRealizacao;

    // Construtor vazio
    public Simulado() {
    }

    // Construtor completo
    public Simulado(Integer idSimulado, Integer idUsuario, LocalDateTime dataRealizacao) {
        this.idSimulado = idSimulado;
        this.idUsuario = idUsuario;
        this.dataRealizacao = dataRealizacao;
    }

    // Construtor sem ID (para inserção - data será definida automaticamente pelo banco)
    public Simulado(Integer idUsuario) {
        this.idUsuario = idUsuario;
        this.dataRealizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Integer getIdSimulado() {
        return idSimulado;
    }

    public void setIdSimulado(Integer idSimulado) {
        this.idSimulado = idSimulado;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDateTime dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    @Override
    public String toString() {
        return "Simulado{" +
                "idSimulado=" + idSimulado +
                ", idUsuario=" + idUsuario +
                ", dataRealizacao=" + dataRealizacao +
                '}';
    }
}