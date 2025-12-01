package com.simulados.service;

import com.simulados.model.Questao;
import com.simulados.repository.MateriaRepository;
import com.simulados.repository.MateriaRepositoryImpl;
import com.simulados.repository.QuestaoRepository;
import com.simulados.repository.QuestaoRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class QuestaoService {

    private final QuestaoRepository questaoRepository;
    private final MateriaRepository materiaRepository;

    public QuestaoService() throws SQLException {
        this.questaoRepository = new QuestaoRepositoryImpl();
        this.materiaRepository = new MateriaRepositoryImpl();
    }

    /**
     * Cadastra uma nova questão no sistema
     */
    public Questao cadastrarQuestao(String enunciado, String alternativa,
                                    String respostaCorreta, Integer idMateria) {
        if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("Enunciado não pode ser vazio!");
        }

        if (alternativa == null || alternativa.trim().isEmpty()) {
            throw new IllegalArgumentException("Alternativas não podem ser vazias!");
        }

        if (respostaCorreta == null || !respostaCorreta.matches("[A-E]")) {
            throw new IllegalArgumentException("Resposta correta deve ser A, B, C, D ou E!");
        }

        if (idMateria == null || idMateria <= 0) {
            throw new IllegalArgumentException("ID da matéria inválido!");
        }

        if (!materiaRepository.buscarPorId(idMateria).isPresent()) {
            throw new IllegalArgumentException("Matéria não encontrada!");
        }

        Questao novaQuestao = new Questao(enunciado.trim(), alternativa.trim(),
                respostaCorreta.toUpperCase(), idMateria);
        return questaoRepository.salvar(novaQuestao);
    }

    /**
     * Busca todas as questões cadastradas
     */
    public List<Questao> listarTodasQuestoes() {
        return questaoRepository.buscarTodas();
    }

    /**
     * Busca uma questão por ID
     */
    public Optional<Questao> buscarQuestaoPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }
        return questaoRepository.buscarPorId(id);
    }

    /**
     * Busca todas as questões de uma matéria específica
     */
    public List<Questao> listarQuestoesPorMateria(Integer idMateria) {
        if (idMateria == null || idMateria <= 0) {
            throw new IllegalArgumentException("ID da matéria inválido!");
        }

        if (!materiaRepository.buscarPorId(idMateria).isPresent()) {
            throw new IllegalArgumentException("Matéria não encontrada!");
        }

        return questaoRepository.buscarPorMateria(idMateria);
    }

    /**
     * Busca N questões aleatórias de uma matéria
     */
    public List<Questao> buscarQuestoesAleatoriasPorMateria(Integer idMateria, int quantidade) {
        if (idMateria == null || idMateria <= 0) {
            throw new IllegalArgumentException("ID da matéria inválido!");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero!");
        }

        if (!materiaRepository.buscarPorId(idMateria).isPresent()) {
            throw new IllegalArgumentException("Matéria não encontrada!");
        }

        int totalQuestoes = questaoRepository.contarPorMateria(idMateria);
        if (totalQuestoes < quantidade) {
            throw new IllegalStateException(
                    "Não há questões suficientes! Existem apenas " + totalQuestoes +
                            " questões para esta matéria."
            );
        }

        return questaoRepository.buscarQuestoesAleatoriasPorMateria(idMateria, quantidade);
    }

    /**
     * Busca N questões aleatórias de qualquer matéria
     */
    public List<Questao> buscarQuestoesAleatorias(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero!");
        }

        int totalQuestoes = questaoRepository.contarTodas();
        if (totalQuestoes < quantidade) {
            throw new IllegalStateException(
                    "Não há questões suficientes! Existem apenas " + totalQuestoes +
                            " questões cadastradas."
            );
        }

        return questaoRepository.buscarQuestoesAleatorias(quantidade);
    }

    /**
     * Atualiza uma questão existente
     */
    public boolean atualizarQuestao(Integer id, String enunciado, String alternativa,
                                    String respostaCorreta, Integer idMateria) {
        Optional<Questao> questaoExistente = questaoRepository.buscarPorId(id);
        if (!questaoExistente.isPresent()) {
            throw new IllegalArgumentException("Questão não encontrada!");
        }

        if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("Enunciado não pode ser vazio!");
        }

        if (alternativa == null || alternativa.trim().isEmpty()) {
            throw new IllegalArgumentException("Alternativas não podem ser vazias!");
        }

        if (respostaCorreta == null || !respostaCorreta.matches("[A-E]")) {
            throw new IllegalArgumentException("Resposta correta deve ser A, B, C, D ou E!");
        }

        if (idMateria == null || idMateria <= 0) {
            throw new IllegalArgumentException("ID da matéria inválido!");
        }

        if (!materiaRepository.buscarPorId(idMateria).isPresent()) {
            throw new IllegalArgumentException("Matéria não encontrada!");
        }

        Questao questao = questaoExistente.get();
        questao.setEnunciado(enunciado.trim());
        questao.setAlternativa(alternativa.trim());
        questao.setRespostaCorreta(respostaCorreta.toUpperCase());
        questao.setIdMateria(idMateria);

        return questaoRepository.atualizar(questao);
    }

    /**
     * Deleta uma questão do sistema
     */
    public boolean deletarQuestao(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }

        Optional<Questao> questao = questaoRepository.buscarPorId(id);
        if (!questao.isPresent()) {
            throw new IllegalArgumentException("Questão não encontrada!");
        }

        try {
            return questaoRepository.deletar(id);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Não é possível deletar a questão pois existem respostas vinculadas a ela!"
            );
        }
    }

    /**
     * Conta o total de questões cadastradas
     */
    public int contarQuestoes() {
        return questaoRepository.contarTodas();
    }

    /**
     * Conta quantas questões existem para uma matéria
     */
    public int contarQuestoesPorMateria(Integer idMateria) {
        if (idMateria == null || idMateria <= 0) {
            return 0;
        }
        return questaoRepository.contarPorMateria(idMateria);
    }

    /**
     * Verifica se uma questão existe
     */
    public boolean questaoExiste(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return questaoRepository.buscarPorId(id).isPresent();
    }

    /**
     * Verifica se existem questões suficientes no sistema
     */
    public boolean existemQuestoesSuficientes(int quantidadeDesejada) {
        return questaoRepository.contarTodas() >= quantidadeDesejada;
    }
}


