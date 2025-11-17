package com.simulados.repository;

import com.simulados.model.Realiza;
import java.util.List;
import java.util.Optional;

/**
 * Interface Repository para operações CRUD da entidade Realiza
 * Tabela de relacionamento N:N entre Usuario e Simulado
 */
public interface RealizaRepository {

    // CREATE - Inserir novo relacionamento
    Realiza salvar(Realiza realiza);

    // READ - Buscar todos os relacionamentos
    List<Realiza> buscarTodos();

    // READ - Buscar relacionamento específico
    Optional<Realiza> buscarRelacionamento(Integer idUsuario, Integer idSimulado);

    // READ - Buscar todos os simulados de um usuário
    List<Realiza> buscarSimuladosDoUsuario(Integer idUsuario);

    // READ - Buscar todos os usuários que fizeram um simulado
    List<Realiza> buscarUsuariosDoSimulado(Integer idSimulado);

    // DELETE - Deletar relacionamento específico
    boolean deletar(Integer idUsuario, Integer idSimulado);

    // DELETE - Deletar todos os relacionamentos de um usuário
    boolean deletarPorUsuario(Integer idUsuario);

    // DELETE - Deletar todos os relacionamentos de um simulado
    boolean deletarPorSimulado(Integer idSimulado);

    // Método auxiliar - Verificar se existe relacionamento
    boolean existeRelacionamento(Integer idUsuario, Integer idSimulado);

    // Método auxiliar - Contar quantos usuários fizeram um simulado
    int contarUsuariosPorSimulado(Integer idSimulado);

    // Método auxiliar - Contar quantos simulados um usuário fez
    int contarSimuladosPorUsuario(Integer idUsuario);
}
