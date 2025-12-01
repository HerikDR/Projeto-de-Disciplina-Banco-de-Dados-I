package com.simulados.controller;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    /**
     * GET /api/admin/relatorio-geral
     * Retorna desempenho geral de todos os alunos
     */
    @GetMapping("/relatorio-geral")
    public ResponseEntity<Map<String, Object>> relatorioGeral() {
        try {
            List<Map<String, Object>> resultados = new ArrayList<>();

            String sql = "SELECT * FROM vw_admin_desempenho_geral";

            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Map<String, Object> aluno = new HashMap<>();
                    aluno.put("idUsuario", rs.getInt("id_usuario"));
                    aluno.put("nomeAluno", rs.getString("nome_aluno"));
                    aluno.put("email", rs.getString("email"));
                    aluno.put("totalSimulados", rs.getInt("total_simulados"));
                    aluno.put("totalQuestoes", rs.getInt("total_questoes_respondidas"));
                    aluno.put("totalAcertos", rs.getInt("total_acertos"));
                    aluno.put("percentualAcerto", rs.getDouble("percentual_acerto_geral"));
                    resultados.add(aluno);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("alunos", resultados);
            response.put("total", resultados.size());

            return ResponseEntity.ok(response);

        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar relatório geral: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/admin/relatorio-por-materia
     * Retorna desempenho de todos os alunos por matéria
     */
    @GetMapping("/relatorio-por-materia")
    public ResponseEntity<Map<String, Object>> relatorioPorMateria() {
        try {
            List<Map<String, Object>> resultados = new ArrayList<>();

            String sql = "SELECT * FROM vw_admin_desempenho_por_materia";

            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Map<String, Object> registro = new HashMap<>();
                    registro.put("idUsuario", rs.getInt("id_usuario"));
                    registro.put("nomeAluno", rs.getString("nome_aluno"));
                    registro.put("materia", rs.getString("materia"));
                    registro.put("questoesRespondidas", rs.getInt("questoes_respondidas"));
                    registro.put("acertos", rs.getInt("acertos"));
                    registro.put("percentualAcerto", rs.getDouble("percentual_acerto"));
                    resultados.add(registro);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("registros", resultados);
            response.put("total", resultados.size());

            return ResponseEntity.ok(response);

        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar relatório por matéria: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/admin/relatorio-aluno/{idAluno}
     * Retorna desempenho detalhado de um aluno específico
     */
    @GetMapping("/relatorio-aluno/{idAluno}")
    public ResponseEntity<Map<String, Object>> relatorioAluno(@PathVariable int idAluno) {
        try {
            Map<String, Object> resultado = new HashMap<>();

            // Buscar desempenho geral do aluno
            String sqlGeral = "SELECT * FROM vw_admin_desempenho_geral WHERE id_usuario = ?";

            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlGeral)) {

                stmt.setInt(1, idAluno);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        resultado.put("idUsuario", rs.getInt("id_usuario"));
                        resultado.put("nomeAluno", rs.getString("nome_aluno"));
                        resultado.put("email", rs.getString("email"));
                        resultado.put("totalSimulados", rs.getInt("total_simulados"));
                        resultado.put("totalQuestoes", rs.getInt("total_questoes_respondidas"));
                        resultado.put("totalAcertos", rs.getInt("total_acertos"));
                        resultado.put("percentualAcerto", rs.getDouble("percentual_acerto_geral"));
                    } else {
                        Map<String, Object> response = new HashMap<>();
                        response.put("sucesso", false);
                        response.put("mensagem", "Aluno não encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                }
            }

            // Buscar desempenho por matéria do aluno
            List<Map<String, Object>> porMateria = new ArrayList<>();
            String sqlMateria = "SELECT * FROM vw_admin_desempenho_por_materia WHERE id_usuario = ?";

            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlMateria)) {

                stmt.setInt(1, idAluno);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> materia = new HashMap<>();
                        materia.put("materia", rs.getString("materia"));
                        materia.put("questoesRespondidas", rs.getInt("questoes_respondidas"));
                        materia.put("acertos", rs.getInt("acertos"));
                        materia.put("percentualAcerto", rs.getDouble("percentual_acerto"));
                        porMateria.add(materia);
                    }
                }
            }

            resultado.put("desempenhoPorMateria", porMateria);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("aluno", resultado);

            return ResponseEntity.ok(response);

        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar relatório do aluno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/admin/estatisticas
     * Retorna estatísticas gerais do sistema
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> estatisticas() {
        try {
            Map<String, Object> stats = new HashMap<>();

            String sql = "SELECT " +
                    "COUNT(DISTINCT CASE WHEN tipo_usuario = 'ALUNO' THEN id_usuario END) as total_alunos, " +
                    "COUNT(DISTINCT CASE WHEN tipo_usuario = 'ADMIN' THEN id_usuario END) as total_admins, " +
                    "COUNT(DISTINCT s.id_simulado) as total_simulados, " +
                    "COUNT(DISTINCT q.id_questao) as total_questoes " +
                    "FROM Usuario u " +
                    "LEFT JOIN Simulado s ON u.id_usuario = s.id_usuario " +
                    "CROSS JOIN Questao q";

            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    stats.put("totalAlunos", rs.getInt("total_alunos"));
                    stats.put("totalAdmins", rs.getInt("total_admins"));
                    stats.put("totalSimulados", rs.getInt("total_simulados"));
                    stats.put("totalQuestoes", rs.getInt("total_questoes"));
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("estatisticas", stats);

            return ResponseEntity.ok(response);

        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar estatísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
