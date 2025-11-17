package com.simulados.repository;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Usuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação Repository para operações CRUD da entidade Usuario usando JDBC puro
 */
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final Connection connection;

    // Construtor - obtém conexão do DatabaseConnection
    public UsuarioRepositoryImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, email, senha, data_cadastro) VALUES (?, ?, ?, ?) RETURNING id_usuario";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                return usuario;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Usuario> buscarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY id_usuario";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os usuários: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por email: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ? WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setInt(4, usuario.getIdUsuario());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int contarTodos() {
        String sql = "SELECT COUNT(*) as total FROM usuario";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar usuários: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Método auxiliar para mapear ResultSet em objeto Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));

        Timestamp timestamp = rs.getTimestamp("data_cadastro");
        if (timestamp != null) {
            usuario.setDataCadastro(timestamp.toLocalDateTime());
        }

        return usuario;
    }
}
