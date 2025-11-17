package com.simulados.repository;

import com.simulados.model.Materia;
import java.util.List;
import java.util.Optional;

/**
 * Interface Repository para operações CRUD da entidade Materia
 */
public interface MateriaRepository {

    // CREATE - Inserir nova matéria
    Materia salvar(Materia materia);

    // READ - Buscar todas as matérias
    List<Materia> buscarTodas();

    // READ - Buscar matéria por ID
    Optional<Materia> buscarPorId(Integer id);

    // READ - Buscar matéria por nome
    Optional<Materia> buscarPorNome(String nome);

    // UPDATE - Atualizar matéria existente
    boolean atualizar(Materia materia);

    // DELETE - Deletar matéria por ID
    boolean deletar(Integer id);

    // Método auxiliar - Contar total de matérias
    int contarTodas();
}
