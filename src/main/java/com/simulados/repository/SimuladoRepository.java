package com.simulados.repository;

import com.simulados.model.Simulado;
import java.util.List;
import java.util.Optional;

/**
 * Interface Repository para operações CRUD da entidade Simulado
 */
public interface SimuladoRepository {

    // CREATE - Inserir novo simulado
    Simulado salvar(Simulado simulado);

    // READ - Buscar todos os simulados
    List<Simulado> buscarTodos();

    // READ - Buscar simulado por ID
    Optional<Simulado> buscarPorId(Integer id);

    // READ - Buscar simulados por usuário
    List<Simulado> buscarPorUsuario(Integer idUsuario);

    // READ - Buscar últimos N simulados de um usuário
    List<Simulado> buscarUltimosSimulados(Integer idUsuario, int limite);

    // UPDATE - Atualizar simulado existente
    boolean atualizar(Simulado simulado);

    // DELETE - Deletar simulado por ID
    boolean deletar(Integer id);

    // Método auxiliar - Contar total de simulados
    int contarTodos();

    // Método auxiliar - Contar simulados por usuário
    int contarPorUsuario(Integer idUsuario);
}

