package com.simulados.repository;

import com.simulados.model.Curso;
import java.util.List;
import java.util.Optional;

/**
 * Interface Repository para operações CRUD da entidade Curso
 */
public interface CursoRepository {

    // CREATE - Inserir novo curso
    Curso salvar(Curso curso);

    // READ - Buscar todos os cursos
    List<Curso> buscarTodos();

    // READ - Buscar curso por ID
    Optional<Curso> buscarPorId(Integer id);

    // READ - Buscar cursos por usuário
    List<Curso> buscarPorUsuario(Integer idUsuario);

    // READ - Buscar curso por nome
    Optional<Curso> buscarPorNome(String nomeCurso);

    // UPDATE - Atualizar curso existente
    boolean atualizar(Curso curso);

    // DELETE - Deletar curso por ID
    boolean deletar(Integer id);

    // Método auxiliar - Contar total de cursos
    int contarTodos();
}

