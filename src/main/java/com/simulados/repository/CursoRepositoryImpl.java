package com.simulados.repository;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Curso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação Repository para operações CRUD da entidade Curso usando JDBC puro
 */
public class CursoRepositoryImpl implements CursoRepository {

    private final Connection connection;

    // Construtor - obtém conexão do DatabaseConnection
    public CursoRepositoryImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Curso salvar(Curso curso) {
        String sql = "INSERT INTO curso (id_usuario, nome_curso) VALUES (?, ?) RETURNING id_curso";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, curso.getIdUsuario());
            stmt.setString(2, curso.getNomeCurso());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                curso.setIdCurso(rs.getInt("id_curso"));
                return curso;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar curso: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Curso> buscarTodos() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT * FROM curso ORDER BY id_curso";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cursos.add(mapearCurso(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os cursos: " + e.getMessage());
            e.printStackTrace();
        }

        return cursos;
    }

    @Override
    public Optional<Curso> buscarPorId(Integer id) {
        String sql = "SELECT * FROM curso WHERE id_curso = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearCurso(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar curso por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Curso> buscarPorUsuario(Integer idUsuario) {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT * FROM curso WHERE id_usuario = ? ORDER BY nome_curso";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cursos.add(mapearCurso(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar cursos por usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return cursos;
    }

    @Override
    public Optional<Curso> buscarPorNome(String nomeCurso) {
        String sql = "SELECT * FROM curso WHERE nome_curso = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nomeCurso);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearCurso(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar curso por nome: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean atualizar(Curso curso) {
        String sql = "UPDATE curso SET id_usuario = ?, nome_curso = ? WHERE id_curso = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, curso.getIdUsuario());
            stmt.setString(2, curso.getNomeCurso());
            stmt.setInt(3, curso.getIdCurso());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar curso: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM curso WHERE id_curso = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar curso: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int contarTodos() {
        String sql = "SELECT COUNT(*) as total FROM curso";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar cursos: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Método auxiliar para mapear ResultSet em objeto Curso
    private Curso mapearCurso(ResultSet rs) throws SQLException {
        Curso curso = new Curso();
        curso.setIdCurso(rs.getInt("id_curso"));
        curso.setIdUsuario(rs.getInt("id_usuario"));
        curso.setNomeCurso(rs.getString("nome_curso"));
        return curso;
    }
}

