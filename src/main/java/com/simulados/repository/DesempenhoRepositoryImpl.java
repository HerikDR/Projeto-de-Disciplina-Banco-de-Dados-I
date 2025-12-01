package com.simulados.repository;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Desempenho;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação Repository para operações CRUD da entidade Desempenho usando JDBC puro
 */
public class DesempenhoRepositoryImpl implements DesempenhoRepository {

    private final Connection connection;

    // Construtor - obtém conexão do DatabaseConnection
    public DesempenhoRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Desempenho salvar(Desempenho desempenho) {
        String sql = "INSERT INTO desempenho (id_usuario, id_curso, id_materia) " +
                "VALUES (?, ?, ?) RETURNING id_desempenho";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, desempenho.getIdUsuario());
            stmt.setInt(2, desempenho.getIdCurso());
            stmt.setInt(3, desempenho.getIdMateria());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                desempenho.setIdDesempenho(rs.getInt("id_desempenho"));
                return desempenho;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar desempenho: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Desempenho> buscarTodos() {
        List<Desempenho> desempenhos = new ArrayList<>();
        String sql = "SELECT * FROM desempenho ORDER BY id_desempenho";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                desempenhos.add(mapearDesempenho(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os desempenhos: " + e.getMessage());
            e.printStackTrace();
        }

        return desempenhos;
    }

    @Override
    public Optional<Desempenho> buscarPorId(Integer id) {
        String sql = "SELECT * FROM desempenho WHERE id_desempenho = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearDesempenho(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar desempenho por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Desempenho> buscarPorUsuario(Integer idUsuario) {
        List<Desempenho> desempenhos = new ArrayList<>();
        String sql = "SELECT * FROM desempenho WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                desempenhos.add(mapearDesempenho(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar desempenhos por usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return desempenhos;
    }

    @Override
    public List<Desempenho> buscarPorCurso(Integer idCurso) {
        List<Desempenho> desempenhos = new ArrayList<>();
        String sql = "SELECT * FROM desempenho WHERE id_curso = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCurso);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                desempenhos.add(mapearDesempenho(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar desempenhos por curso: " + e.getMessage());
            e.printStackTrace();
        }

        return desempenhos;
    }

    @Override
    public List<Desempenho> buscarPorMateria(Integer idMateria) {
        List<Desempenho> desempenhos = new ArrayList<>();
        String sql = "SELECT * FROM desempenho WHERE id_materia = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMateria);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                desempenhos.add(mapearDesempenho(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar desempenhos por matéria: " + e.getMessage());
            e.printStackTrace();
        }

        return desempenhos;
    }

    @Override
    public Optional<Desempenho> buscarDesempenho(Integer idUsuario, Integer idCurso, Integer idMateria) {
        String sql = "SELECT * FROM desempenho WHERE id_usuario = ? AND id_curso = ? AND id_materia = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idCurso);
            stmt.setInt(3, idMateria);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearDesempenho(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar desempenho específico: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean atualizar(Desempenho desempenho) {
        String sql = "UPDATE desempenho SET id_usuario = ?, id_curso = ?, id_materia = ? " +
                "WHERE id_desempenho = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, desempenho.getIdUsuario());
            stmt.setInt(2, desempenho.getIdCurso());
            stmt.setInt(3, desempenho.getIdMateria());
            stmt.setInt(4, desempenho.getIdDesempenho());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar desempenho: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM desempenho WHERE id_desempenho = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar desempenho: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletarPorUsuario(Integer idUsuario) {
        String sql = "DELETE FROM desempenho WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar desempenhos por usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int contarTodos() {
        String sql = "SELECT COUNT(*) as total FROM desempenho";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar desempenhos: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Método auxiliar para mapear ResultSet em objeto Desempenho
    private Desempenho mapearDesempenho(ResultSet rs) throws SQLException {
        Desempenho desempenho = new Desempenho();
        desempenho.setIdDesempenho(rs.getInt("id_desempenho"));
        desempenho.setIdUsuario(rs.getInt("id_usuario"));
        desempenho.setIdCurso(rs.getInt("id_curso"));
        desempenho.setIdMateria(rs.getInt("id_materia"));
        return desempenho;
    }
}

