package com.simulados.service;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Desempenho;
import com.simulados.repository.DesempenhoRepository;
import com.simulados.repository.DesempenhoRepositoryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesempenhoService {

    private DesempenhoRepository desempenhoRepository;

    public DesempenhoService() throws SQLException {
        this.desempenhoRepository = new DesempenhoRepositoryImpl();
    }

    /**
     * salva um novo desempenho
     */
    public void salvarDesempenho(Desempenho desempenho) throws SQLException {
        if (desempenho == null) {
            throw new IllegalArgumentException("Desempenho não pode ser nulo");
        }
        desempenhoRepository.salvar(desempenho);
    }

    /**
     * busca desempenho por ID
     */
    public Desempenho buscarPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        return desempenhoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Desempenho não encontrado"));
    }

    /**
     * busca todos os desempenhos de um usuário
     */
    public List<Desempenho> buscarPorUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido");
        }
        return desempenhoRepository.buscarPorUsuario(idUsuario);
    }

    /**
     * busca desempenhos por curso
     */
    public List<Desempenho> buscarPorCurso(int idCurso) throws SQLException {
        if (idCurso <= 0) {
            throw new IllegalArgumentException("ID do curso inválido");
        }
        return desempenhoRepository.buscarPorCurso(idCurso);
    }

    /**
     * busca desempenhos por matéria
     */
    public List<Desempenho> buscarPorMateria(int idMateria) throws SQLException {
        if (idMateria <= 0) {
            throw new IllegalArgumentException("ID da matéria inválido");
        }
        return desempenhoRepository.buscarPorMateria(idMateria);
    }

    /**
     * busca todos os desempenhos
     */
    public List<Desempenho> buscarTodos() throws SQLException {
        return desempenhoRepository.buscarTodos();
    }

    /**
     * atualiza um desempenho
     */
    public void atualizarDesempenho(Desempenho desempenho) throws SQLException {
        if (desempenho == null) {
            throw new IllegalArgumentException("Desempenho não pode ser nulo");
        }
        if (desempenho.getIdDesempenho() <= 0) {
            throw new IllegalArgumentException("ID do desempenho inválido");
        }
        desempenhoRepository.atualizar(desempenho);
    }

    /**
     * deleta um desempenho
     */
    public void deletarDesempenho(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        desempenhoRepository.deletar(id);
    }

    /**
     * busca desempenho agregado por matéria de um usuário
     * retorna lista com estatísticas de cada matéria
     */
    public List<Map<String, Object>> buscarDesempenhoPorMateria(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido");
        }

        List<Map<String, Object>> resultado = new ArrayList<>();

        String sql = "SELECT " +
                "m.id_materia, " +
                "m.nome AS nome_materia, " +
                "COUNT(ru.id_resposta) AS total_questoes, " +
                "SUM(CASE WHEN ru.resposta_fornecida = q.resposta_correta THEN 1 ELSE 0 END) AS total_acertos, " +
                "(SUM(CASE WHEN ru.resposta_fornecida = q.resposta_correta THEN 1 ELSE 0 END) * 100.0 / " +
                "NULLIF(COUNT(ru.id_resposta), 0)) AS percentual_acerto " +
                "FROM resposta_usuario ru " +
                "INNER JOIN questao q ON ru.id_questao = q.id_questao " +
                "INNER JOIN materia m ON q.id_materia = m.id_materia " +
                "INNER JOIN simulado s ON ru.id_simulado = s.id_simulado " +
                "WHERE s.id_usuario = ? " +
                "GROUP BY m.id_materia, m.nome " +
                "ORDER BY m.nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> materia = new HashMap<>();
                    materia.put("idMateria", rs.getInt("id_materia"));
                    materia.put("nomeMateria", rs.getString("nome_materia"));
                    materia.put("totalQuestoes", rs.getInt("total_questoes"));
                    materia.put("totalAcertos", rs.getInt("total_acertos"));
                    materia.put("percentualAcerto", rs.getDouble("percentual_acerto"));
                    resultado.add(materia);
                }
            }
        }

        return resultado;
    }

    /**
     * busca estatísticas de um simulado específico de um usuário
     * retorna total de questões, acertos, erros e taxa de acerto
     */
    public Map<String, Object> buscarEstatisticasSimulado(int idUsuario, int idSimulado) throws SQLException {
        if (idUsuario <= 0 || idSimulado <= 0) {
            throw new IllegalArgumentException("IDs inválidos");
        }

        Map<String, Object> estatisticas = new HashMap<>();

        String sql = "SELECT " +
                "COUNT(ru.id_resposta) AS total_questoes, " +
                "SUM(CASE WHEN ru.resposta_fornecida = q.resposta_correta THEN 1 ELSE 0 END) AS acertos, " +
                "SUM(CASE WHEN ru.resposta_fornecida != q.resposta_correta THEN 1 ELSE 0 END) AS erros, " +
                "(SUM(CASE WHEN ru.resposta_fornecida = q.resposta_correta THEN 1 ELSE 0 END) * 100.0 / " +
                "NULLIF(COUNT(ru.id_resposta), 0)) AS taxa_acerto " +
                "FROM resposta_usuario ru " +
                "INNER JOIN questao q ON ru.id_questao = q.id_questao " +
                "INNER JOIN simulado s ON ru.id_simulado = s.id_simulado " +
                "WHERE s.id_usuario = ? AND s.id_simulado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idSimulado);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estatisticas.put("totalQuestoes", rs.getInt("total_questoes"));
                    estatisticas.put("acertos", rs.getInt("acertos"));
                    estatisticas.put("erros", rs.getInt("erros"));

                    // taxa_acerto pode ser null se não houver questões
                    double taxaAcerto = rs.getDouble("taxa_acerto");
                    estatisticas.put("taxaAcerto", rs.wasNull() ? 0.0 : taxaAcerto);
                } else {
                    // se não encontrou dados, retornar zeros
                    estatisticas.put("totalQuestoes", 0);
                    estatisticas.put("acertos", 0);
                    estatisticas.put("erros", 0);
                    estatisticas.put("taxaAcerto", 0.0);
                }
            }
        }

        return estatisticas;
    }

    /**
     * busca estatísticas detalhadas por matéria de um simulado específico
     * útil para gráficos de desempenho por matéria em um simulado
     */
    public List<Map<String, Object>> buscarEstatisticasPorMateriaDoSimulado(int idUsuario, int idSimulado) throws SQLException {
        if (idUsuario <= 0 || idSimulado <= 0) {
            throw new IllegalArgumentException("IDs inválidos");
        }

        List<Map<String, Object>> resultado = new ArrayList<>();

        String sql = "SELECT " +
                "m.id_materia, " +
                "m.nome AS nome_materia, " +
                "COUNT(ru.id_resposta) AS total_questoes, " +
                "SUM(CASE WHEN ru.resposta_fornecida = q.resposta_correta THEN 1 ELSE 0 END) AS acertos, " +
                "SUM(CASE WHEN ru.resposta_fornecida != q.resposta_correta THEN 1 ELSE 0 END) AS erros, " +
                "(SUM(CASE WHEN ru.resposta_fornecida = q.resposta_correta THEN 1 ELSE 0 END) * 100.0 / " +
                "NULLIF(COUNT(ru.id_resposta), 0)) AS taxa_acerto " +
                "FROM resposta_usuario ru " +
                "INNER JOIN questao q ON ru.id_questao = q.id_questao " +
                "INNER JOIN materia m ON q.id_materia = m.id_materia " +
                "INNER JOIN simulado s ON ru.id_simulado = s.id_simulado " +
                "WHERE s.id_usuario = ? AND s.id_simulado = ? " +
                "GROUP BY m.id_materia, m.nome " +
                "ORDER BY m.nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idSimulado);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> materia = new HashMap<>();
                    materia.put("idMateria", rs.getInt("id_materia"));
                    materia.put("nomeMateria", rs.getString("nome_materia"));
                    materia.put("totalQuestoes", rs.getInt("total_questoes"));
                    materia.put("acertos", rs.getInt("acertos"));
                    materia.put("erros", rs.getInt("erros"));

                    double taxaAcerto = rs.getDouble("taxa_acerto");
                    materia.put("taxaAcerto", rs.wasNull() ? 0.0 : taxaAcerto);

                    resultado.add(materia);
                }
            }
        }

        return resultado;
    }
}
