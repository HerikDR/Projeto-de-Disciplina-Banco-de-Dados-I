package com.simulados.repository;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Simulado;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação Repository para operações CRUD da entidade Simulado usando JDBC puro
 */
public class SimuladoRepositoryImpl implements SimuladoRepository {

    private final Connection connection;

    // Construtor - obtém conexão do DatabaseConnection
    public SimuladoRepositoryImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Simulado salvar(Simulado simulado) {
        String sql = "INSERT INTO simulado (id_usuario, data_realizacao) VALUES (?, ?) RETURNING id_simulado";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, simulado.getIdUsuario());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                simulado.setIdSimulado(rs.getInt("id_simulado"));
                return simulado;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar simulado: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Simulado> buscarTodos() {
        List<Simulado> simulados = new ArrayList<>();
        String sql = "SELECT * FROM simulado ORDER BY data_realizacao DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                simulados.add(mapearSimulado(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os simulados: " + e.getMessage());
            e.printStackTrace();
        }

        return simulados;
    }

    @Override
    public Optional<Simulado> buscarPorId(Integer id) {
        String sql = "SELECT * FROM simulado WHERE id_simulado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearSimulado(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar simulado por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Simulado> buscarPorUsuario(Integer idUsuario) {
        List<Simulado> simulados = new ArrayList<>();
        String sql = "SELECT * FROM simulado WHERE id_usuario = ? ORDER BY data_realizacao DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                simulados.add(mapearSimulado(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar simulados por usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return simulados;
    }

    @Override
    public List<Simulado> buscarUltimosSimulados(Integer idUsuario, int limite) {
        List<Simulado> simulados = new ArrayList<>();
        String sql = "SELECT * FROM simulado WHERE id_usuario = ? ORDER BY data_realizacao DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, limite);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                simulados.add(mapearSimulado(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar últimos simulados: " + e.getMessage());
            e.printStackTrace();
        }

        return simulados;
    }

    @Override
    public boolean atualizar(Simulado simulado) {
        String sql = "UPDATE simulado SET id_usuario = ?, data_realizacao = ? WHERE id_simulado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, simulado.getIdUsuario());
            stmt.setTimestamp(2, Timestamp.valueOf(simulado.getDataRealizacao()));
            stmt.setInt(3, simulado.getIdSimulado());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar simulado: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM simulado WHERE id_simulado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar simulado: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int contarTodos() {
        String sql = "SELECT COUNT(*) as total FROM simulado";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar simulados: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int contarPorUsuario(Integer idUsuario) {
        String sql = "SELECT COUNT(*) as total FROM simulado WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar simulados por usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Método auxiliar para mapear ResultSet em objeto Simulado
    private Simulado mapearSimulado(ResultSet rs) throws SQLException {
        Simulado simulado = new Simulado();
        simulado.setIdSimulado(rs.getInt("id_simulado"));
        simulado.setIdUsuario(rs.getInt("id_usuario"));

        Timestamp timestamp = rs.getTimestamp("data_realizacao");
        if (timestamp != null) {
            simulado.setDataRealizacao(timestamp.toLocalDateTime());
        }

        return simulado;
    }
}

