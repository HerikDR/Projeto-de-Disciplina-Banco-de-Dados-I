package com.simulados.service;

import com.simulados.model.Usuario;
import com.simulados.repository.UsuarioRepository;
import com.simulados.repository.UsuarioRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    /**
     * Cadastra um novo usuário no sistema
     */
    public void cadastrarUsuario(Usuario usuario) throws SQLException {
        // Validações
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }

        // Verifica se email já existe
        if (usuarioRepository.emailExiste(usuario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Define tipo padrão se não foi especificado
        if (usuario.getTipoUsuario() == null || usuario.getTipoUsuario().trim().isEmpty()) {
            usuario.setTipoUsuario(Usuario.TIPO_ALUNO);
        }

        // Valida tipo de usuário
        if (!Usuario.TIPO_ALUNO.equals(usuario.getTipoUsuario()) &&
                !Usuario.TIPO_ADMIN.equals(usuario.getTipoUsuario())) {
            throw new IllegalArgumentException("Tipo de usuário inválido");
        }

        usuarioRepository.salvar(usuario);
    }

    /**
     * Realiza login do usuário
     */
    public Usuario login(String email, String senha) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }

        Usuario usuario = usuarioRepository.buscarPorEmail(email);

        if (usuario == null) {
            throw new IllegalArgumentException("Email não encontrado");
        }

        if (!usuario.getSenha().equals(senha)) {
            throw new IllegalArgumentException("Senha incorreta");
        }

        return usuario;
    }

    /**
     * Busca usuário por ID
     */
    public Usuario buscarPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        Usuario usuario = usuarioRepository.buscarPorId(id);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        return usuario;
    }

    /**
     * Busca todos os usuários
     */
    public List<Usuario> buscarTodos() throws SQLException {
        return usuarioRepository.buscarTodos();
    }

    /**
     * Busca usuários por tipo (ALUNO ou ADMIN)
     */
    public List<Usuario> buscarPorTipo(String tipoUsuario) throws SQLException {
        if (!Usuario.TIPO_ALUNO.equals(tipoUsuario) &&
                !Usuario.TIPO_ADMIN.equals(tipoUsuario)) {
            throw new IllegalArgumentException("Tipo de usuário inválido");
        }

        return usuarioRepository.buscarPorTipo(tipoUsuario);
    }

    /**
     * Busca apenas alunos (atalho para buscarPorTipo)
     */
    public List<Usuario> buscarAlunos() throws SQLException {
        return usuarioRepository.buscarPorTipo(Usuario.TIPO_ALUNO);
    }

    /**
     * Busca apenas administradores
     */
    public List<Usuario> buscarAdmins() throws SQLException {
        return usuarioRepository.buscarPorTipo(Usuario.TIPO_ADMIN);
    }

    /**
     * Atualiza dados do usuário
     */
    public void atualizarUsuario(Usuario usuario) throws SQLException {
        if (usuario.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido");
        }

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (usuario.getTipoUsuario() == null ||
                (!Usuario.TIPO_ALUNO.equals(usuario.getTipoUsuario()) &&
                        !Usuario.TIPO_ADMIN.equals(usuario.getTipoUsuario()))) {
            throw new IllegalArgumentException("Tipo de usuário inválido");
        }

        usuarioRepository.atualizar(usuario);
    }

    /**
     * Deleta usuário
     */
    public void deletarUsuario(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        usuarioRepository.deletar(id);
    }

    /**
     * Verifica se o email já está cadastrado
     */
    public boolean emailExiste(String email) throws SQLException {
        return usuarioRepository.emailExiste(email);
    }
}


