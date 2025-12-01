package com.simulados.repository;

import com.simulados.model.Desempenho;
import java.util.List;
import java.util.Optional;

/**
 * Interface Repository para operações CRUD da entidade Desempenho
 */
public interface DesempenhoRepository {

    Desempenho salvar(Desempenho desempenho);

    List<Desempenho> buscarTodos();

    Optional<Desempenho> buscarPorId(Integer id);

    List<Desempenho> buscarPorUsuario(Integer idUsuario);

    List<Desempenho> buscarPorCurso(Integer idCurso);

    List<Desempenho> buscarPorMateria(Integer idMateria);

    Optional<Desempenho> buscarDesempenho(Integer idUsuario, Integer idCurso, Integer idMateria);

    boolean atualizar(Desempenho desempenho);

    boolean deletar(Integer id);

    boolean deletarPorUsuario(Integer idUsuario);

    int contarTodos();
}


