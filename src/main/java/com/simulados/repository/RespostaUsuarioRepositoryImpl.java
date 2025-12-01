package com.simulados.repository;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.RespostaUsuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação Repository para operações CRUD da entidade RespostaUsuario usando JDBC puro
 */
public class RespostaUsuarioRepositoryImpl implements RespostaUsuarioRepository {

    private final Connection connection;

    // Construtor - obtém conexão do DatabaseConnection
    public RespostaUsuarioRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public RespostaUsuario salvar(RespostaUsuario respostaUsuario) {
        String sql = "INSERT INTO resposta_usuario (id_simulado, id_questao, id_usuario, resposta_fornecida, data_resposta) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id_resposta";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, respostaUsuario.getIdSimulado());
            stmt.setInt(2, respostaUsuario.getIdQuestao());
            stmt.setInt(3, respostaUsuario.getIdUsuario());
            stmt.setString(4, respostaUsuario.getRespostaFornecida());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                respostaUsuario.setIdResposta(rs.getInt("id_resposta"));
                return respostaUsuario;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar resposta: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<RespostaUsuario> buscarTodas() {
        List<RespostaUsuario> respostas = new ArrayList<>();
        String sql = "SELECT * FROM resposta_usuario ORDER BY data_resposta DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                respostas.add(mapearRespostaUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todas as respostas: " + e.getMessage());
            e.printStackTrace();
        }

        return respostas;
    }

    @Override
    public Optional<RespostaUsuario> buscarPorId(Integer id) {
        String sql = "SELECT * FROM resposta_usuario WHERE id_resposta = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearRespostaUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar resposta por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<RespostaUsuario> buscarPorSimulado(Integer idSimulado) {
        List<RespostaUsuario> respostas = new ArrayList<>();
        String sql = "SELECT * FROM resposta_usuario WHERE id_simulado = ? ORDER BY id_questao";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idSimulado);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                respostas.add(mapearRespostaUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar respostas por simulado: " + e.getMessage());
            e.printStackTrace();
        }

        return respostas;
    }

    @Override
    public List<RespostaUsuario> buscarPorUsuario(Integer idUsuario) {
        List<RespostaUsuario> respostas = new ArrayList<>();
        String sql = "SELECT * FROM resposta_usuario WHERE id_usuario = ? ORDER BY data_resposta DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                respostas.add(mapearRespostaUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar respostas por usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return respostas;
    }

    @Override
    public Optional<RespostaUsuario> buscarResposta(Integer idSimulado, Integer idQuestao, Integer idUsuario) {
        String sql = "SELECT * FROM resposta_usuario WHERE id_simulado = ? AND id_questao = ? AND id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idSimulado);
            stmt.setInt(2, idQuestao);
            stmt.setInt(3, idUsuario);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearRespostaUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar resposta específica: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean atualizar(RespostaUsuario respostaUsuario) {
        String sql = "UPDATE resposta_usuario SET id_simulado = ?, id_questao = ?, id_usuario = ?, " +
                "resposta_fornecida = ?, data_resposta = ? WHERE id_resposta = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, respostaUsuario.getIdSimulado());
            stmt.setInt(2, respostaUsuario.getIdQuestao());
            stmt.setInt(3, respostaUsuario.getIdUsuario());
            stmt.setString(4, respostaUsuario.getRespostaFornecida());
            stmt.setTimestamp(5, Timestamp.valueOf(respostaUsuario.getDataResposta()));
            stmt.setInt(6, respostaUsuario.getIdResposta());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar resposta: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM resposta_usuario WHERE id_resposta = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar resposta: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletarPorSimulado(Integer idSimulado) {
        String sql = "DELETE FROM resposta_usuario WHERE id_simulado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idSimulado);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar respostas por simulado: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int contarRespostasCorretas(Integer idSimulado) {
        String sql = "SELECT COUNT(*) as total FROM resposta_usuario r " +
                "INNER JOIN questao q ON r.id_questao = q.id_questao " +
                "WHERE r.id_simulado = ? AND r.resposta_fornecida = q.resposta_correta";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idSimulado);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar respostas corretas: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int contarRespostasPorSimulado(Integer idSimulado) {
        String sql = "SELECT COUNT(*) as total FROM resposta_usuario WHERE id_simulado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idSimulado);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar respostas por simulado: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Método auxiliar para mapear ResultSet em objeto RespostaUsuario
    private RespostaUsuario mapearRespostaUsuario(ResultSet rs) throws SQLException {
        RespostaUsuario resposta = new RespostaUsuario();
        resposta.setIdResposta(rs.getInt("id_resposta"));
        resposta.setIdSimulado(rs.getInt("id_simulado"));
        resposta.setIdQuestao(rs.getInt("id_questao"));
        resposta.setIdUsuario(rs.getInt("id_usuario"));
        resposta.setRespostaFornecida(rs.getString("resposta_fornecida"));

        Timestamp timestamp = rs.getTimestamp("data_resposta");
        if (timestamp != null) {
            resposta.setDataResposta(timestamp.toLocalDateTime());
        }

        return resposta;
    }
}

