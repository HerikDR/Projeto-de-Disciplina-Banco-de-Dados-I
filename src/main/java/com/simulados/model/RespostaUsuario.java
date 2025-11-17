package com.simulados.model;

import java.time.LocalDateTime;

/**
 * Classe Model que representa a tabela Resposta_usuario do banco de dados
 * Relacionamentos:
 * - Pertence a um Usuario (FK: id_usuario)
 * - Pertence a um Simulado (FK: id_simulado)
 * - Refere-se a uma Questao (FK: id_questao)
 */
public class RespostaUsuario {

    private Integer idResposta;
    private Integer idSimulado;             // Foreign Key para Simulado
    private Integer idQuestao;              // Foreign Key para Questao
    private Integer idUsuario;              // Foreign Key para Usuario
    private String respostaFornecida;       // 'A', 'B', 'C', 'D', 'E' ou 'N' (não respondida)
    private LocalDateTime dataResposta;

    // Construtor vazio
    public RespostaUsuario() {
    }

    // Construtor completo
    public RespostaUsuario(Integer idResposta, Integer idSimulado, Integer idQuestao,
                           Integer idUsuario, String respostaFornecida, LocalDateTime dataResposta) {
        this.idResposta = idResposta;
        this.idSimulado = idSimulado;
        this.idQuestao = idQuestao;
        this.idUsuario = idUsuario;
        this.respostaFornecida = respostaFornecida;
        this.dataResposta = dataResposta;
    }

    // Construtor sem ID (para inserção)
    public RespostaUsuario(Integer idSimulado, Integer idQuestao, Integer idUsuario,
                           String respostaFornecida) {
        this.idSimulado = idSimulado;
        this.idQuestao = idQuestao;
        this.idUsuario = idUsuario;
        this.respostaFornecida = respostaFornecida;
        this.dataResposta = LocalDateTime.now();
    }

    // Getters e Setters
    public Integer getIdResposta() {
        return idResposta;
    }

    public void setIdResposta(Integer idResposta) {
        this.idResposta = idResposta;
    }

    public Integer getIdSimulado() {
        return idSimulado;
    }

    public void setIdSimulado(Integer idSimulado) {
        this.idSimulado = idSimulado;
    }

    public Integer getIdQuestao() {
        return idQuestao;
    }

    public void setIdQuestao(Integer idQuestao) {
        this.idQuestao = idQuestao;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRespostaFornecida() {
        return respostaFornecida;
    }

    public void setRespostaFornecida(String respostaFornecida) {
        this.respostaFornecida = respostaFornecida;
    }

    public LocalDateTime getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(LocalDateTime dataResposta) {
        this.dataResposta = dataResposta;
    }

    @Override
    public String toString() {
        return "RespostaUsuario{" +
                "idResposta=" + idResposta +
                ", idSimulado=" + idSimulado +
                ", idQuestao=" + idQuestao +
                ", idUsuario=" + idUsuario +
                ", respostaFornecida='" + respostaFornecida + '\'' +
                ", dataResposta=" + dataResposta +
                '}';
    }
}