package com.simulados.service;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Simulado;
import com.simulados.model.Questao;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class SimuladoService {

    /**
     * gera um simulado aleatório com questões distribuídas entre as matérias
     * usando uma única conexão e transação
     */
    public Map<String, Object> gerarSimuladoAleatorio(int idUsuario, Map<Integer, Integer> materias) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido");
        }
        if (materias == null || materias.isEmpty()) {
            throw new IllegalArgumentException("É necessário selecionar pelo menos uma matéria");
        }

        // abre UMA conexão para todas as operações
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // inicia transação

            try {
                // 1. salvar simulado
                Simulado simulado = salvarSimuladoComConexao(conn, idUsuario);

                List<Questao> todasQuestoes = new ArrayList<>();

                // 2. buscar questões de cada matéria
                for (Map.Entry<Integer, Integer> entry : materias.entrySet()) {
                    Integer idMateria = entry.getKey();
                    Integer quantidade = entry.getValue();

                    if (quantidade > 0) {
                        List<Questao> questoesMateria = buscarQuestoesComConexao(
                                conn, idMateria, quantidade
                        );
                        todasQuestoes.addAll(questoesMateria);
                    }
                }

                // embaralhar as questões
                Collections.shuffle(todasQuestoes);

                // 3. associar questões ao simulado
                for (Questao questao : todasQuestoes) {
                    associarQuestaoComConexao(conn, simulado.getIdSimulado(), questao.getIdQuestao());
                }

                conn.commit(); // confirma transação

                // preparar resposta
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("idSimulado", simulado.getIdSimulado());
                resultado.put("idUsuario", simulado.getIdUsuario());
                resultado.put("dataRealizacao", simulado.getDataRealizacao());
                resultado.put("questoes", todasQuestoes);
                resultado.put("totalQuestoes", todasQuestoes.size());

                return resultado;

            } catch (SQLException e) {
                conn.rollback(); // desfaz tudo se der erro
                throw e;
            }
        }
    }

    /**
     * salva simulado usando conexão fornecida (NÃO fecha a conexão)
     */
    private Simulado salvarSimuladoComConexao(Connection conn, int idUsuario) throws SQLException {
        String sql = "INSERT INTO simulado (id_usuario, data_realizacao) VALUES (?, ?) RETURNING id_simulado";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Simulado simulado = new Simulado();
                    simulado.setIdSimulado(rs.getInt("id_simulado"));
                    simulado.setIdUsuario(idUsuario);
                    simulado.setDataRealizacao(LocalDateTime.now());
                    return simulado;
                } else {
                    throw new SQLException("Falha ao salvar simulado");
                }
            }
        }
    }

    /**
     * busca questões aleatórias usando conexão fornecida (NÃO fecha a conexão)
     */
    private List<Questao> buscarQuestoesComConexao(Connection conn, Integer idMateria, int quantidade) throws SQLException {
        List<Questao> questoes = new ArrayList<>();
        String sql = "SELECT * FROM questao WHERE id_materia = ? ORDER BY RANDOM() LIMIT ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMateria);
            stmt.setInt(2, quantidade);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Questao questao = new Questao();
                    questao.setIdQuestao(rs.getInt("id_questao"));
                    questao.setEnunciado(rs.getString("enunciado"));
                    questao.setAlternativa(rs.getString("alternativa"));
                    questao.setRespostaCorreta(rs.getString("resposta_correta"));
                    questao.setIdMateria(rs.getInt("id_materia"));
                    questoes.add(questao);
                }
            }
        }

        return questoes;
    }

    /**
     * associa questão ao simulado usando conexão fornecida (NÃO fecha a conexão)
     */
    private void associarQuestaoComConexao(Connection conn, int idSimulado, int idQuestao) throws SQLException {
        String sql = "INSERT INTO realiza (id_simulado, id_questao) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSimulado);
            stmt.setInt(2, idQuestao);
            stmt.executeUpdate();
        }
    }

    /**
     * distribui questões automaticamente entre todas as matérias disponíveis
     */
    public Map<Integer, Integer> distribuirQuestoesPorMaterias(int quantidadeTotal) throws SQLException {
        Map<Integer, Integer> materias = new HashMap<>();

        // buscar todas as matérias
        List<Integer> idsMaterias = buscarIdsMaterias();

        if (idsMaterias.isEmpty()) {
            throw new SQLException("Nenhuma matéria cadastrada no sistema");
        }

        // calcular questões por matéria
        int questoesPorMateria = quantidadeTotal / idsMaterias.size();
        int questoesRestantes = quantidadeTotal % idsMaterias.size();

        // distribuir questões
        for (int i = 0; i < idsMaterias.size(); i++) {
            int quantidade = questoesPorMateria;

            // distribuir questões restantes nas primeiras matérias
            if (i < questoesRestantes) {
                quantidade++;
            }

            if (quantidade > 0) {
                materias.put(idsMaterias.get(i), quantidade);
            }
        }

        return materias;
    }

    /**
     * busca IDs de todas as matérias cadastradas
     */
    private List<Integer> buscarIdsMaterias() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_materia FROM materia ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("id_materia"));
            }
        }

        return ids;
    }

    /**
     * busca todas as questões de um simulado específico
     */
    public List<Map<String, Object>> buscarQuestoesDoSimulado(int idSimulado) throws SQLException {
        if (idSimulado <= 0) {
            throw new IllegalArgumentException("ID do simulado inválido");
        }

        List<Map<String, Object>> questoes = new ArrayList<>();
        String sql = "SELECT q.id_questao, q.enunciado, q.alternativa, q.resposta_correta, " +
                "m.nome AS nome_materia, m.id_materia " +
                "FROM questao q " +
                "INNER JOIN materia m ON q.id_materia = m.id_materia " +
                "INNER JOIN realiza r ON q.id_questao = r.id_questao " +
                "WHERE r.id_simulado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSimulado);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> questao = new HashMap<>();
                    questao.put("idQuestao", rs.getInt("id_questao"));
                    questao.put("enunciado", rs.getString("enunciado"));
                    questao.put("alternativa", rs.getString("alternativa"));
                    questao.put("respostaCorreta", rs.getString("resposta_correta"));
                    questao.put("nomeMateria", rs.getString("nome_materia"));
                    questao.put("idMateria", rs.getInt("id_materia"));
                    questoes.add(questao);
                }
            }
        }

        return questoes;
    }

    /**
     * busca simulado por ID
     */
    public Simulado buscarPorId(int idSimulado) throws SQLException {
        if (idSimulado <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        String sql = "SELECT * FROM simulado WHERE id_simulado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSimulado);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Simulado simulado = new Simulado();
                    simulado.setIdSimulado(rs.getInt("id_simulado"));
                    simulado.setIdUsuario(rs.getInt("id_usuario"));

                    Timestamp timestamp = rs.getTimestamp("data_realizacao");
                    if (timestamp != null) {
                        simulado.setDataRealizacao(timestamp.toLocalDateTime());
                    }

                    return simulado;
                } else {
                    throw new IllegalArgumentException("Simulado não encontrado");
                }
            }
        }
    }

    /**
     * busca todos os simulados de um usuário
     */
    public List<Simulado> buscarPorUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido");
        }

        List<Simulado> simulados = new ArrayList<>();
        String sql = "SELECT * FROM simulado WHERE id_usuario = ? ORDER BY data_realizacao DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Simulado simulado = new Simulado();
                    simulado.setIdSimulado(rs.getInt("id_simulado"));
                    simulado.setIdUsuario(rs.getInt("id_usuario"));

                    Timestamp timestamp = rs.getTimestamp("data_realizacao");
                    if (timestamp != null) {
                        simulado.setDataRealizacao(timestamp.toLocalDateTime());
                    }

                    simulados.add(simulado);
                }
            }
        }

        return simulados;
    }

    /**
     * deleta um simulado
     */
    public void deletar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        String sql = "DELETE FROM simulado WHERE id_simulado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new IllegalArgumentException("Simulado não encontrado");
            }
        }
    }
}
