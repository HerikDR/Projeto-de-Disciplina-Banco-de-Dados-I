package com.simulados.service;

import com.simulados.model.Curso;
import com.simulados.repository.CursoRepository;
import com.simulados.repository.CursoRepositoryImpl;
import com.simulados.repository.UsuarioRepository;
import com.simulados.repository.UsuarioRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service com lógica de negócio para operações relacionadas a Curso
 */
public class CursoService {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    // Construtor - instancia os repositories
    public CursoService() throws SQLException {
        this.cursoRepository = new CursoRepositoryImpl();
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    /**
     * Cadastra um novo curso no sistema
     * Valida se o usuário existe e se o nome do curso não está duplicado
     */
    public Curso cadastrarCurso(Integer idUsuario, String nomeCurso) throws SQLException {
        // Validação: verifica se o usuário existe
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (usuarioRepository.buscarPorId(idUsuario) == null) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        // Validação: nome do curso não pode ser vazio
        if (nomeCurso == null || nomeCurso.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do curso não pode ser vazio!");
        }

        // Validação: verifica se já existe um curso com esse nome para esse usuário
        Optional<Curso> cursoExistente = cursoRepository.buscarPorNome(nomeCurso.trim());
        if (cursoExistente.isPresent() && cursoExistente.get().getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("Você já possui um curso com esse nome!");
        }

        // Cria e salva o curso
        Curso novoCurso = new Curso(idUsuario, nomeCurso.trim());
        return cursoRepository.salvar(novoCurso);
    }

    /**
     * Busca todos os cursos cadastrados
     */
    public List<Curso> listarTodosCursos() {
        return cursoRepository.buscarTodos();
    }

    /**
     * Busca um curso por ID
     */
    public Optional<Curso> buscarCursoPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }
        return cursoRepository.buscarPorId(id);
    }

    /**
     * Busca todos os cursos de um usuário específico
     */
    public List<Curso> listarCursosPorUsuario(Integer idUsuario) throws SQLException {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        // Verifica se o usuário existe
        if (usuarioRepository.buscarPorId(idUsuario) == null) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        return cursoRepository.buscarPorUsuario(idUsuario);
    }

    /**
     * Busca um curso por nome
     */
    public Optional<Curso> buscarCursoPorNome(String nomeCurso) {
        if (nomeCurso == null || nomeCurso.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio!");
        }
        return cursoRepository.buscarPorNome(nomeCurso.trim());
    }

    /**
     * Atualiza os dados de um curso
     * Verifica se o novo nome não está sendo usado por outro curso do mesmo usuário
     */
    public boolean atualizarCurso(Integer id, Integer idUsuario, String nomeCurso) throws SQLException {
        // Verifica se o curso existe
        Optional<Curso> cursoExistente = cursoRepository.buscarPorId(id);
        if (!cursoExistente.isPresent()) {
            throw new IllegalArgumentException("Curso não encontrado!");
        }

        // Validação: verifica se o usuário existe
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (usuarioRepository.buscarPorId(idUsuario) == null) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        // Validação: nome não pode ser vazio
        if (nomeCurso == null || nomeCurso.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do curso não pode ser vazio!");
        }

        // Atualiza o curso
        Curso curso = cursoExistente.get();
        curso.setIdUsuario(idUsuario);
        curso.setNomeCurso(nomeCurso.trim());

        return cursoRepository.atualizar(curso);
    }

    /**
     * Deleta um curso do sistema
     * ATENÇÃO: Isso também deletará dados relacionados (CASCADE)
     */
    public boolean deletarCurso(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }

        // Verifica se o curso existe
        Optional<Curso> curso = cursoRepository.buscarPorId(id);
        if (!curso.isPresent()) {
            throw new IllegalArgumentException("Curso não encontrado!");
        }

        return cursoRepository.deletar(id);
    }

    /**
     * Conta o total de cursos cadastrados
     */
    public int contarCursos() {
        return cursoRepository.contarTodos();
    }

    /**
     * Conta quantos cursos um usuário possui
     */
    public int contarCursosPorUsuario(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            return 0;
        }
        return cursoRepository.buscarPorUsuario(idUsuario).size();
    }

    /**
     * Verifica se um curso existe pelo ID
     */
    public boolean cursoExiste(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return cursoRepository.buscarPorId(id).isPresent();
    }

    /**
     * Verifica se um curso pertence a um usuário específico
     */
    public boolean cursoPertenceAoUsuario(Integer idCurso, Integer idUsuario) {
        Optional<Curso> curso = cursoRepository.buscarPorId(idCurso);
        return curso.isPresent() && curso.get().getIdUsuario().equals(idUsuario);
    }
}

