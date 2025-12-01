package com.simulados.repository;

import com.simulados.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioRepository {
    void salvar(Usuario usuario) throws SQLException;
    Usuario buscarPorId(int id) throws SQLException;
    Usuario buscarPorEmail(String email) throws SQLException;
    List<Usuario> buscarTodos() throws SQLException;
    List<Usuario> buscarPorTipo(String tipoUsuario) throws SQLException;  // NOVO MÉTODO
    void atualizar(Usuario usuario) throws SQLException;
    void deletar(int id) throws SQLException;
    boolean emailExiste(String email) throws SQLException;
}

