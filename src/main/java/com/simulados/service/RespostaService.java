package com.simulados.service;

import com.simulados.model.Questao;
import com.simulados.model.RespostaUsuario;
import com.simulados.repository.*;

import java.util.*;

/**
 * Service com lógica de negócio para operações relacionadas a RespostaUsuario
 * Inclui lógica de correção de simulados e cálculo de desempenho
 */
public class RespostaService {

    private final RespostaUsuarioRepository respostaRepository;
    private final SimuladoRepository simuladoRepository;
    private final QuestaoRepository questaoRepository;
    private final UsuarioRepository usuarioRepository;

    // Construtor - instancia os repositories
    public RespostaService() {
        this.respostaRepository = new RespostaUsuarioRepositoryImpl();
        this.simuladoRepository = new SimuladoRepositoryImpl();
        this.questaoRepository = new QuestaoRepositoryImpl();
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    /**
     * Registra a resposta de um usuário para uma questão de um simulado
     */
    public RespostaUsuario registrarResposta(Integer idSimulado, Integer idQuestao,
                                             Integer idUsuario, String respostaFornecida) {
        // Validações
        if (idSimulado == null || idSimulado <= 0) {
            throw new IllegalArgumentException("ID do simulado inválido!");
        }

        if (idQuestao == null || idQuestao <= 0) {
            throw new IllegalArgumentException("ID da questão inválido!");
        }

        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (respostaFornecida == null || !respostaFornecida.matches("[A-E]")) {
            throw new IllegalArgumentException("Resposta deve ser A, B, C, D ou E!");
        }

        // Verifica se o simulado existe
        if (simuladoRepository.buscarPorId(idSimulado).isEmpty()) {
            throw new IllegalArgumentException("Simulado não encontrado!");
        }

        // Verifica se a questão existe
        if (questaoRepository.buscarPorId(idQuestao).isEmpty()) {
            throw new IllegalArgumentException("Questão não encontrada!");
        }

        // Verifica se o usuário existe
        if (usuarioRepository.buscarPorId(idUsuario).isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        // Verifica se já existe uma resposta para essa questão nesse simulado
        Optional<RespostaUsuario> respostaExistente =
                respostaRepository.buscarResposta(idSimulado, idQuestao, idUsuario);

        if (respostaExistente.isPresent()) {
            // Atualiza a resposta existente
            RespostaUsuario resposta = respostaExistente.get();
            resposta.setRespostaFornecida(respostaFornecida.toUpperCase());
            respostaRepository.atualizar(resposta);
            return resposta;
        }

        // Cria uma nova resposta
        RespostaUsuario novaResposta = new RespostaUsuario(idSimulado, idQuestao,
                idUsuario, respostaFornecida.toUpperCase());
        return respostaRepository.salvar(novaResposta);
    }

    /**
     * Corrige um simulado e retorna o resultado completo
     * Retorna mapa com: acertos, erros, nota, percentual, detalhes por questão
     */
    public Map<String, Object> corrigirSimulado(Integer idSimulado) {
        // Validação
        if (idSimulado == null || idSimulado <= 0) {
            throw new IllegalArgumentException("ID do simulado inválido!");
        }

        if (simuladoRepository.buscarPorId(idSimulado).isEmpty()) {
            throw new IllegalArgumentException("Simulado não encontrado!");
        }

        // Busca todas as respostas do simulado
        List<RespostaUsuario> respostas = respostaRepository.buscarPorSimulado(idSimulado);

        if (respostas.isEmpty()) {
            throw new IllegalStateException("O simulado não possui respostas registradas!");
        }

        // Corrige cada resposta
        int acertos = 0;
        int erros = 0;
        List<Map<String, Object>> detalhesQuestoes = new ArrayList<>();

        for (RespostaUsuario resposta : respostas) {
            // Busca a questão correspondente
            Optional<Questao> questaoOpt = questaoRepository.buscarPorId(resposta.getIdQuestao());

            if (questaoOpt.isPresent()) {
                Questao questao = questaoOpt.get();
                boolean acertou = questao.getRespostaCorreta().equals(resposta.getRespostaFornecida());

                if (acertou) {
                    acertos++;
                } else {
                    erros++;
                }

                // Monta detalhes da questão
                Map<String, Object> detalhe = new HashMap<>();
                detalhe.put("idQuestao", questao.getIdQuestao());
                detalhe.put("enunciado", questao.getEnunciado());
                detalhe.put("respostaUsuario", resposta.getRespostaFornecida());
                detalhe.put("respostaCorreta", questao.getRespostaCorreta());
                detalhe.put("acertou", acertou);
                detalhe.put("idMateria", questao.getIdMateria());

                detalhesQuestoes.add(detalhe);
            }
        }

        // Calcula estatísticas
        int totalQuestoes = acertos + erros;
        double percentual = (totalQuestoes > 0) ? ((double) acertos / totalQuestoes) * 100 : 0;
        double nota = (totalQuestoes > 0) ? ((double) acertos / totalQuestoes) * 10 : 0;

        // Monta o resultado
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("idSimulado", idSimulado);
        resultado.put("totalQuestoes", totalQuestoes);
        resultado.put("acertos", acertos);
        resultado.put("erros", erros);
        resultado.put("percentual", String.format("%.2f", percentual) + "%");
        resultado.put("nota", String.format("%.2f", nota));
        resultado.put("detalhesQuestoes", detalhesQuestoes);

        return resultado;
    }

    /**
     * Calcula desempenho por matéria em um simulado
     * Retorna mapa com acertos e erros separados por matéria
     */
    public Map<Integer, Map<String, Integer>> calcularDesempenhoPorMateria(Integer idSimulado) {
        // Validação
        if (idSimulado == null || idSimulado <= 0) {
            throw new IllegalArgumentException("ID do simulado inválido!");
        }

        List<RespostaUsuario> respostas = respostaRepository.buscarPorSimulado(idSimulado);
        Map<Integer, Map<String, Integer>> desempenhoPorMateria = new HashMap<>();

        for (RespostaUsuario resposta : respostas) {
            Optional<Questao> questaoOpt = questaoRepository.buscarPorId(resposta.getIdQuestao());

            if (questaoOpt.isPresent()) {
                Questao questao = questaoOpt.get();
                Integer idMateria = questao.getIdMateria();
                boolean acertou = questao.getRespostaCorreta().equals(resposta.getRespostaFornecida());

                // Inicializa a matéria se não existir
                desempenhoPorMateria.putIfAbsent(idMateria, new HashMap<>());
                Map<String, Integer> stats = desempenhoPorMateria.get(idMateria);

                stats.put("acertos", stats.getOrDefault("acertos", 0) + (acertou ? 1 : 0));
                stats.put("erros", stats.getOrDefault("erros", 0) + (acertou ? 0 : 1));
                stats.put("total", stats.getOrDefault("total", 0) + 1);
            }
        }

        return desempenhoPorMateria;
    }

    /**
     * Busca todas as respostas de um simulado
     */
    public List<RespostaUsuario> listarRespostasPorSimulado(Integer idSimulado) {
        if (idSimulado == null || idSimulado <= 0) {
            throw new IllegalArgumentException("ID do simulado inválido!");
        }
        return respostaRepository.buscarPorSimulado(idSimulado);
    }

    /**
     * Busca todas as respostas de um usuário
     */
    public List<RespostaUsuario> listarRespostasPorUsuario(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }
        return respostaRepository.buscarPorUsuario(idUsuario);
    }

    /**
     * Busca uma resposta específica
     */
    public Optional<RespostaUsuario> buscarResposta(Integer idSimulado, Integer idQuestao, Integer idUsuario) {
        if (idSimulado == null || idSimulado <= 0 || idQuestao == null || idQuestao <= 0 ||
                idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("IDs inválidos!");
        }
        return respostaRepository.buscarResposta(idSimulado, idQuestao, idUsuario);
    }

    /**
     * Deleta uma resposta específica
     */
    public boolean deletarResposta(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }

        Optional<RespostaUsuario> resposta = respostaRepository.buscarPorId(id);
        if (resposta.isEmpty()) {
            throw new IllegalArgumentException("Resposta não encontrada!");
        }

        return respostaRepository.deletar(id);
    }

    /**
     * Deleta todas as respostas de um simulado
     * Útil para resetar um simulado
     */
    public boolean deletarRespostasPorSimulado(Integer idSimulado) {
        if (idSimulado == null || idSimulado <= 0) {
            throw new IllegalArgumentException("ID do simulado inválido!");
        }
        return respostaRepository.deletarPorSimulado(idSimulado);
    }

    /**
     * Conta quantas respostas foram registradas em um simulado
     */
    public int contarRespostasPorSimulado(Integer idSimulado) {
        if (idSimulado == null || idSimulado <= 0) {
            return 0;
        }
        return respostaRepository.contarRespostasPorSimulado(idSimulado);
    }

    /**
     * Verifica se um simulado foi completamente respondido
     * (todas as questões têm resposta)
     */
    public boolean simuladoCompleto(Integer idSimulado, int totalQuestoes) {
        int respostasRegistradas = contarRespostasPorSimulado(idSimulado);
        return respostasRegistradas == totalQuestoes;
    }
}
