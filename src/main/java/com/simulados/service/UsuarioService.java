package com.simulados.service;

import com.simulados.model.Usuario;
import com.simulados.repository.UsuarioRepository;
import com.simulados.repository.UsuarioRepositoryImpl;

import java.util.List;
import java.util.Optional;

/**
 * Service com lógica de negócio para operações relacionadas a Usuario
 */
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Construtor - instancia o repository
    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    /**
     * Cadastra um novo usuário no sistema
     * Valida se o email já existe antes de salvar
     */
    public Usuario cadastrarUsuario(String nome, String email, String senha) {
        // Validação: verifica se o email já está em uso
        Optional<Usuario> usuarioExistente = usuarioRepository.buscarPorEmail(email);
        if (usuarioExistente.isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado no sistema!");
        }

        // Validação: verifica se os campos não estão vazios
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio!");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio!");
        }

        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres!");
        }

        // Validação: verifica formato do email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido!");
        }

        // Cria e salva o usuário
        Usuario novoUsuario = new Usuario(nome, email, senha);
        return usuarioRepository.salvar(novoUsuario);
    }

    /**
     * Busca todos os usuários cadastrados
     */
    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.buscarTodos();
    }

    /**
     * Busca um usuário por ID
     */
    public Optional<Usuario> buscarUsuarioPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }
        return usuarioRepository.buscarPorId(id);
    }

    /**
     * Busca um usuário por email
     */
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio!");
        }
        return usuarioRepository.buscarPorEmail(email);
    }

    /**
     * Atualiza os dados de um usuário
     * Verifica se o novo email não está sendo usado por outro usuário
     */
    public boolean atualizarUsuario(Integer id, String nome, String email, String senha) {
        // Verifica se o usuário existe
        Optional<Usuario> usuarioExistente = usuarioRepository.buscarPorId(id);
        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        // Verifica se o email já está em uso por outro usuário
        Optional<Usuario> usuarioComEmail = usuarioRepository.buscarPorEmail(email);
        if (usuarioComEmail.isPresent() && !usuarioComEmail.get().getIdUsuario().equals(id)) {
            throw new IllegalArgumentException("Email já está em uso por outro usuário!");
        }

        // Validações
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio!");
        }

        if (senha != null && senha.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres!");
        }

        // Atualiza o usuário
        Usuario usuario = usuarioExistente.get();
        usuario.setNome(nome);
        usuario.setEmail(email);
        if (senha != null && !senha.isEmpty()) {
            usuario.setSenha(senha);
        }

        return usuarioRepository.atualizar(usuario);
    }

    /**
     * Deleta um usuário do sistema
     * ATENÇÃO: Isso também deletará todos os dados relacionados (CASCADE)
     */
    public boolean deletarUsuario(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }

        // Verifica se o usuário existe
        Optional<Usuario> usuario = usuarioRepository.buscarPorId(id);
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        return usuarioRepository.deletar(id);
    }

    /**
     * Valida login do usuário (autenticação simples)
     * NOTA: Em produção, use hash de senha (BCrypt, etc)
     */
    public Optional<Usuario> fazerLogin(String email, String senha) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio!");
        }

        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia!");
        }

        Optional<Usuario> usuario = usuarioRepository.buscarPorEmail(email);

        // Verifica se o usuário existe e se a senha está correta
        if (usuario.isPresent() && usuario.get().getSenha().equals(senha)) {
            return usuario;
        }

        return Optional.empty();
    }

    /**
     * Conta o total de usuários cadastrados
     */
    public int contarUsuarios() {
        return usuarioRepository.contarTodos();
    }

    /**
     * Verifica se um usuário existe pelo ID
     */
    public boolean usuarioExiste(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return usuarioRepository.buscarPorId(id).isPresent();
    }
}

