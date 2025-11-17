package com.simulados.repository;

import com.simulados.model.Questao;
import java.util.List;
import java.util.Optional;

/**
 * Interface Repository para operações CRUD da entidade Questao
 */
public interface QuestaoRepository {

    // CREATE - Inserir nova questão
    Questao salvar(Questao questao);

    // READ - Buscar todas as questões
    List<Questao> buscarTodas();

    // READ - Buscar questão por ID
    Optional<Questao> buscarPorId(Integer id);

    // READ - Buscar questões por matéria
    List<Questao> buscarPorMateria(Integer idMateria);

    // READ - Buscar N questões aleatórias por matéria (para montar simulados)
    List<Questao> buscarQuestoesAleatoriasPorMateria(Integer idMateria, int quantidade);

    // READ - Buscar todas as questões de forma aleatória
    List<Questao> buscarQuestoesAleatorias(int quantidade);

    // UPDATE - Atualizar questão existente
    boolean atualizar(Questao questao);

    // DELETE - Deletar questão por ID
    boolean deletar(Integer id);

    // Método auxiliar - Contar total de questões
    int contarTodas();

    // Método auxiliar - Contar questões por matéria
    int contarPorMateria(Integer idMateria);
}
