package com.simulados.model;

/**
 * Classe Model que representa a tabela Realiza do banco de dados
 * Tabela de relacionamento N:N entre Usuario e Simulado
 * Um usuário pode realizar vários simulados
 * Um simulado pode ser realizado por vários usuários (se compartilhado)
 */
public class Realiza {

    private Integer idUsuario;      // Foreign Key para Usuario (parte da PK composta)
    private Integer idSimulado;     // Foreign Key para Simulado (parte da PK composta)

    // Construtor vazio
    public Realiza() {
    }

    // Construtor completo
    public Realiza(Integer idUsuario, Integer idSimulado) {
        this.idUsuario = idUsuario;
        this.idSimulado = idSimulado;
    }

    // Getters e Setters
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdSimulado() {
        return idSimulado;
    }

    public void setIdSimulado(Integer idSimulado) {
        this.idSimulado = idSimulado;
    }

    @Override
    public String toString() {
        return "Realiza{" +
                "idUsuario=" + idUsuario +
                ", idSimulado=" + idSimulado +
                '}';
    }

    // Sobrescrevendo equals e hashCode para chave primária composta
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Realiza realiza = (Realiza) o;

        if (!idUsuario.equals(realiza.idUsuario)) return false;
        return idSimulado.equals(realiza.idSimulado);
    }

    @Override
    public int hashCode() {
        int result = idUsuario.hashCode();
        result = 31 * result + idSimulado.hashCode();
        return result;
    }
}