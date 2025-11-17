package com.simulados.model;

/**
 * Classe Model que representa a tabela Questao do banco de dados
 * Relacionamento: Uma Questao pertence a uma Materia (FK: id_materia)
 */
public class Questao {

    private Integer idQuestao;
    private String enunciado;
    private String alternativa;         // Armazena as alternativas (A-E) em formato texto
    private String respostaCorreta;     // 'A', 'B', 'C', 'D' ou 'E'
    private Integer idMateria;          // Foreign Key para Materia

    // Construtor vazio
    public Questao() {
    }

    // Construtor completo
    public Questao(Integer idQuestao, String enunciado, String alternativa,
                   String respostaCorreta, Integer idMateria) {
        this.idQuestao = idQuestao;
        this.enunciado = enunciado;
        this.alternativa = alternativa;
        this.respostaCorreta = respostaCorreta;
        this.idMateria = idMateria;
    }

    // Construtor sem ID (para inserção)
    public Questao(String enunciado, String alternativa, String respostaCorreta, Integer idMateria) {
        this.enunciado = enunciado;
        this.alternativa = alternativa;
        this.respostaCorreta = respostaCorreta;
        this.idMateria = idMateria;
    }

    // Getters e Setters
    public Integer getIdQuestao() {
        return idQuestao;
    }

    public void setIdQuestao(Integer idQuestao) {
        this.idQuestao = idQuestao;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getAlternativa() {
        return alternativa;
    }

    public void setAlternativa(String alternativa) {
        this.alternativa = alternativa;
    }

    public String getRespostaCorreta() {
        return respostaCorreta;
    }

    public void setRespostaCorreta(String respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }

    public Integer getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Integer idMateria) {
        this.idMateria = idMateria;
    }

    @Override
    public String toString() {
        return "Questao{" +
                "idQuestao=" + idQuestao +
                ", enunciado='" + enunciado + '\'' +
                ", respostaCorreta='" + respostaCorreta + '\'' +
                ", idMateria=" + idMateria +
                '}';
    }
}