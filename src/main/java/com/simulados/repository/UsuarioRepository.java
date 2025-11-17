package com.simulados.repository;

import com.simulados.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interface Repository para operações CRUD da entidade Usuario
 */
public interface UsuarioRepository {

    // CREATE - Inserir novo usuário
    Usuario salvar(Usuario usuario);

    // READ - Buscar todos os usuários
    List<Usuario> buscarTodos();

    // READ - Buscar usuário por ID
    Optional<Usuario> buscarPorId(Integer id);

    // READ - Buscar usuário por email
    Optional<Usuario> buscarPorEmail(String email);

    // UPDATE - Atualizar usuário existente
    boolean atualizar(Usuario usuario);

    // DELETE - Deletar usuário por ID
    boolean deletar(Integer id);

    // Método auxiliar - Contar total de usuários
    int contarTodos();
}
