package com.simulados.repository;

import com.simulados.model.Desempenho;
import java.util.List;
import java.util.Optional;

/**
 * Interface Repository para operações CRUD da entidade Desempenho
 */
public interface DesempenhoRepository {

    // CREATE - Inserir novo desempenho
    Desempenho salvar(Desempenho desempenho);

    // READ - Buscar todos os desempenhos
    List<Desempenho> buscarTodos();

    // READ - Buscar desempenho por ID
    Optional<Desempenho> buscarPorId(Integer id);

    // READ - Buscar desempenhos por usuário
    List<Desempenho> buscarPorUsuario(Integer idUsuario);

    // READ - Buscar desempenhos por curso
    List<Desempenho> buscarPorCurso(Integer idCurso);

    // READ - Buscar desempenhos por matéria
    List<Desempenho> buscarPorMateria(Integer idMateria);

    // READ - Buscar desempenho específico (usuário + curso + matéria)
    Optional<Desempenho> buscarDesempenho(Integer idUsuario, Integer idCurso, Integer idMateria);

    // UPDATE - Atualizar desempenho existente
    boolean atualizar(Desempenho desempenho);

    // DELETE - Deletar desempenho por ID
    boolean deletar(Integer id);

    // DELETE - Deletar todos os desempenhos de um usuário
    boolean deletarPorUsuario(Integer idUsuario);

    // Método auxiliar - Contar total de desempenhos
    int contarTodos();
}

