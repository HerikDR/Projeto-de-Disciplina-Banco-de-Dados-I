package com.simulados.repository;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Materia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação Repository para operações CRUD da entidade Materia usando JDBC puro
 */
public class MateriaRepositoryImpl implements MateriaRepository {

    private final Connection connection;

    // Construtor - obtém conexão do DatabaseConnection
    public MateriaRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Materia salvar(Materia materia) {
        String sql = "INSERT INTO materia (nome) VALUES (?) RETURNING id_materia";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, materia.getNome());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                materia.setIdMateria(rs.getInt("id_materia"));
                return materia;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar matéria: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Materia> buscarTodas() {
        List<Materia> materias = new ArrayList<>();
        String sql = "SELECT * FROM materia ORDER BY nome";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                materias.add(mapearMateria(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todas as matérias: " + e.getMessage());
            e.printStackTrace();
        }

        return materias;
    }

    @Override
    public Optional<Materia> buscarPorId(Integer id) {
        String sql = "SELECT * FROM materia WHERE id_materia = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearMateria(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar matéria por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Materia> buscarPorNome(String nome) {
        String sql = "SELECT * FROM materia WHERE nome = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearMateria(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar matéria por nome: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean atualizar(Materia materia) {
        String sql = "UPDATE materia SET nome = ? WHERE id_materia = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, materia.getNome());
            stmt.setInt(2, materia.getIdMateria());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar matéria: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM materia WHERE id_materia = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar matéria: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int contarTodas() {
        String sql = "SELECT COUNT(*) as total FROM materia";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar matérias: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Método auxiliar para mapear ResultSet em objeto Materia
    private Materia mapearMateria(ResultSet rs) throws SQLException {
        Materia materia = new Materia();
        materia.setIdMateria(rs.getInt("id_materia"));
        materia.setNome(rs.getString("nome"));
        return materia;
    }
}
