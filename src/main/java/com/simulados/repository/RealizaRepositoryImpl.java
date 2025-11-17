package com.simulados.repository;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Realiza;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação Repository para operações CRUD da entidade Realiza usando JDBC puro
 */
public class RealizaRepositoryImpl implements RealizaRepository {

    private final Connection connection;

    // Construtor - obtém conexão do DatabaseConnection
    public RealizaRepositoryImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Realiza salvar(Realiza realiza) {
        String sql = "INSERT INTO realiza (id_usuario, id_simulado) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, realiza.getIdUsuario());
            stmt.setInt(2, realiza.getIdSimulado());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                return realiza;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar relacionamento: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Realiza> buscarTodos() {
        List<Realiza> relacionamentos = new ArrayList<>();
        String sql = "SELECT * FROM realiza ORDER BY id_usuario, id_simulado";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                relacionamentos.add(mapearRealiza(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os relacionamentos: " + e.getMessage());
            e.printStackTrace();
        }

        return relacionamentos;
    }

    @Override
    public Optional<Realiza> buscarRelacionamento(Integer idUsuario, Integer idSimulado) {
        String sql = "SELECT * FROM realiza WHERE id_usuario = ? AND id_simulado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idSimulado);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearRealiza(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar relacionamento: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Realiza> buscarSimuladosDoUsuario(Integer idUsuario) {
        List<Realiza> relacionamentos = new ArrayList<>();
        String sql = "SELECT * FROM realiza WHERE id_usuario = ? ORDER BY id_simulado DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                relacionamentos.add(mapearRealiza(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar simulados do usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return relacionamentos;
    }

    @Override
    public List<Realiza> buscarUsuariosDoSimulado(Integer idSimulado) {
        List<Realiza> relacionamentos = new ArrayList<>();
        String sql = "SELECT * FROM realiza WHERE id_simulado = ? ORDER BY id_usuario";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idSimulado);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                relacionamentos.add(mapearRealiza(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuários do simulado: " + e.getMessage());
            e.printStackTrace();
        }

        return relacionamentos;
    }

    @Override
    public boolean deletar(Integer idUsuario, Integer idSimulado) {
        String sql = "DELETE FROM realiza WHERE id_usuario = ? AND id_simulado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idSimulado);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar relacionamento: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletarPorUsuario(Integer idUsuario) {
        String sql = "DELETE FROM realiza WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar relacionamentos por usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletarPorSimulado(Integer idSimulado) {
        String sql = "DELETE FROM realiza WHERE id_simulado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idSimulado);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar relacionamentos por simulado: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean existeRelacionamento(Integer idUsuario, Integer idSimulado) {
        String sql = "SELECT COUNT(*) as total FROM realiza WHERE id_usuario = ? AND id_simulado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idSimulado);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar relacionamento: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int contarUsuariosPorSimulado(Integer idSimulado) {
        String sql = "SELECT COUNT(*) as total FROM realiza WHERE id_simulado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idSimulado);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar usuários por simulado: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int contarSimuladosPorUsuario(Integer idUsuario) {
        String sql = "SELECT COUNT(*) as total FROM realiza WHERE id_usuario = ?";

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

    // Método auxiliar para mapear ResultSet em objeto Realiza
    private Realiza mapearRealiza(ResultSet rs) throws SQLException {
        Realiza realiza = new Realiza();
        realiza.setIdUsuario(rs.getInt("id_usuario"));
        realiza.setIdSimulado(rs.getInt("id_simulado"));
        return realiza;
    }
}