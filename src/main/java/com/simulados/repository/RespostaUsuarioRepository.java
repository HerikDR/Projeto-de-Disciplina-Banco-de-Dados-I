package com.simulados.repository;

import com.simulados.model.RespostaUsuario;
import java.util.List;
import java.util.Optional;

/**
 * Interface Repository para operações CRUD da entidade RespostaUsuario
 */
public interface RespostaUsuarioRepository {

    // CREATE - Inserir nova resposta
    RespostaUsuario salvar(RespostaUsuario respostaUsuario);

    // READ - Buscar todas as respostas
    List<RespostaUsuario> buscarTodas();

    // READ - Buscar resposta por ID
    Optional<RespostaUsuario> buscarPorId(Integer id);

    // READ - Buscar respostas por simulado
    List<RespostaUsuario> buscarPorSimulado(Integer idSimulado);

    // READ - Buscar respostas por usuário
    List<RespostaUsuario> buscarPorUsuario(Integer idUsuario);

    // READ - Buscar resposta específica (simulado + questão + usuário)
    Optional<RespostaUsuario> buscarResposta(Integer idSimulado, Integer idQuestao, Integer idUsuario);

    // UPDATE - Atualizar resposta existente
    boolean atualizar(RespostaUsuario respostaUsuario);

    // DELETE - Deletar resposta por ID
    boolean deletar(Integer id);

    // DELETE - Deletar todas as respostas de um simulado
    boolean deletarPorSimulado(Integer idSimulado);

    // Método auxiliar - Contar respostas corretas de um simulado
    int contarRespostasCorretas(Integer idSimulado);

    // Método auxiliar - Contar total de respostas de um simulado
    int contarRespostasPorSimulado(Integer idSimulado);
}

