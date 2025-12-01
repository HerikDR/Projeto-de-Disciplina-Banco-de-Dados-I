package com.simulados.repository;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryImpl implements UsuarioRepository {

    // Queries SQL
    private static final String INSERT =
            "INSERT INTO Usuario (nome, email, senha, tipo_usuario) VALUES (?, ?, ?, ?)";

    private static final String SELECT_BY_ID =
            "SELECT id_usuario, nome, email, senha, data_cadastro, tipo_usuario " +
                    "FROM Usuario WHERE id_usuario = ?";

    private static final String SELECT_BY_EMAIL =
            "SELECT id_usuario, nome, email, senha, data_cadastro, tipo_usuario " +
                    "FROM Usuario WHERE email = ?";

    private static final String SELECT_ALL =
            "SELECT id_usuario, nome, email, senha, data_cadastro, tipo_usuario " +
                    "FROM Usuario ORDER BY nome";

    private static final String SELECT_BY_TIPO =
            "SELECT id_usuario, nome, email, senha, data_cadastro, tipo_usuario " +
                    "FROM Usuario WHERE tipo_usuario = ? ORDER BY nome";

    private static final String UPDATE =
            "UPDATE Usuario SET nome = ?, email = ?, senha = ?, tipo_usuario = ? " +
                    "WHERE id_usuario = ?";

    private static final String DELETE =
            "DELETE FROM Usuario WHERE id_usuario = ?";

    private static final String EMAIL_EXISTS =
            "SELECT COUNT(*) FROM Usuario WHERE email = ?";

    @Override
    public void salvar(Usuario usuario) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTipoUsuario() != null ?
                    usuario.getTipoUsuario() : Usuario.TIPO_ALUNO);

            stmt.executeUpdate();

            // Recuperar ID gerado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Usuario buscarPorId(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return construirUsuario(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Usuario buscarPorEmail(String email) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_EMAIL)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return construirUsuario(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Usuario> buscarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(construirUsuario(rs));
            }
        }

        return usuarios;
    }

    @Override
    public List<Usuario> buscarPorTipo(String tipoUsuario) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TIPO)) {

            stmt.setString(1, tipoUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(construirUsuario(rs));
                }
            }
        }

        return usuarios;
    }

    @Override
    public void atualizar(Usuario usuario) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTipoUsuario());
            stmt.setInt(5, usuario.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean emailExiste(String email) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(EMAIL_EXISTS)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Método auxiliar para construir objeto Usuario a partir do ResultSet
    private Usuario construirUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
        usuario.setTipoUsuario(rs.getString("tipo_usuario"));
        return usuario;
    }
}

