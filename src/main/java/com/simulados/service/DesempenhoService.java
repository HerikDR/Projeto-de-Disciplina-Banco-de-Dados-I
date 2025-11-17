package com.simulados.service;

import com.simulados.model.Desempenho;
import com.simulados.repository.*;

import java.util.*;

/**
 * Service com lógica de negócio para operações relacionadas a Desempenho
 * Inclui análises estatísticas e cálculos de performance
 */
public class DesempenhoService {

    private final DesempenhoRepository desempenhoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final MateriaRepository materiaRepository;
    private final RespostaUsuarioRepository respostaRepository;
    private final QuestaoRepository questaoRepository;

    // Construtor - instancia os repositories
    public DesempenhoService() {
        this.desempenhoRepository = new DesempenhoRepositoryImpl();
        this.usuarioRepository = new UsuarioRepositoryImpl();
        this.cursoRepository = new CursoRepositoryImpl();
        this.materiaRepository = new MateriaRepositoryImpl();
        this.respostaRepository = new RespostaUsuarioRepositoryImpl();
        this.questaoRepository = new QuestaoRepositoryImpl();
    }

    /**
     * Registra um desempenho no sistema
     */
    public Desempenho registrarDesempenho(Integer idUsuario, Integer idCurso, Integer idMateria) {
        // Validações
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (usuarioRepository.buscarPorId(idUsuario).isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        if (idCurso == null || idCurso <= 0) {
            throw new IllegalArgumentException("ID do curso inválido!");
        }

        if (cursoRepository.buscarPorId(idCurso).isEmpty()) {
            throw new IllegalArgumentException("Curso não encontrado!");
        }

        if (idMateria == null || idMateria <= 0) {
            throw new IllegalArgumentException("ID da matéria inválido!");
        }

        if (materiaRepository.buscarPorId(idMateria).isEmpty()) {
            throw new IllegalArgumentException("Matéria não encontrada!");
        }

        // Verifica se já existe um desempenho com essa combinação
        Optional<Desempenho> desempenhoExistente =
                desempenhoRepository.buscarDesempenho(idUsuario, idCurso, idMateria);

        if (desempenhoExistente.isPresent()) {
            return desempenhoExistente.get();
        }

        // Cria novo desempenho
        Desempenho novoDesempenho = new Desempenho(idUsuario, idCurso, idMateria);
        return desempenhoRepository.salvar(novoDesempenho);
    }

    /**
     * Calcula estatísticas gerais de um usuário
     * Retorna total de simulados, questões respondidas, taxa de acerto geral, etc.
     */
    public Map<String, Object> calcularEstatisticasGerais(Integer idUsuario) {
        // Validação
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (usuarioRepository.buscarPorId(idUsuario).isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        // Busca todas as respostas do usuário
        List<com.simulados.model.RespostaUsuario> respostas = respostaRepository.buscarPorUsuario(idUsuario);

        if (respostas.isEmpty()) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("totalQuestoesRespondidas", 0);
            resultado.put("acertos", 0);
            resultado.put("erros", 0);
            resultado.put("taxaAcerto", "0.00%");
            resultado.put("notaMedia", "0.00");
            return resultado;
        }

        // Calcula acertos e erros
        int acertos = 0;
        int erros = 0;

        for (com.simulados.model.RespostaUsuario resposta : respostas) {
            Optional<com.simulados.model.Questao> questaoOpt =
                    questaoRepository.buscarPorId(resposta.getIdQuestao());

            if (questaoOpt.isPresent()) {
                com.simulados.model.Questao questao = questaoOpt.get();
                if (questao.getRespostaCorreta().equals(resposta.getRespostaFornecida())) {
                    acertos++;
                } else {
                    erros++;
                }
            }
        }

        int total = acertos + erros;
        double taxaAcerto = (total > 0) ? ((double) acertos / total) * 100 : 0;
        double notaMedia = (total > 0) ? ((double) acertos / total) * 10 : 0;

        // Monta resultado
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalQuestoesRespondidas", total);
        resultado.put("acertos", acertos);
        resultado.put("erros", erros);
        resultado.put("taxaAcerto", String.format("%.2f", taxaAcerto) + "%");
        resultado.put("notaMedia", String.format("%.2f", notaMedia));

        return resultado;
    }

    /**
     * Calcula desempenho de um usuário por matéria
     * Retorna estatísticas separadas para cada matéria
     */
    public Map<Integer, Map<String, Object>> calcularDesempenhoPorMateria(Integer idUsuario) {
        // Validação
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        List<com.simulados.model.RespostaUsuario> respostas = respostaRepository.buscarPorUsuario(idUsuario);
        Map<Integer, Map<String, Object>> desempenhoPorMateria = new HashMap<>();

        for (com.simulados.model.RespostaUsuario resposta : respostas) {
            Optional<com.simulados.model.Questao> questaoOpt =
                    questaoRepository.buscarPorId(resposta.getIdQuestao());

            if (questaoOpt.isPresent()) {
                com.simulados.model.Questao questao = questaoOpt.get();
                Integer idMateria = questao.getIdMateria();
                boolean acertou = questao.getRespostaCorreta().equals(resposta.getRespostaFornecida());

                // Inicializa a matéria se não existir
                desempenhoPorMateria.putIfAbsent(idMateria, new HashMap<>());
                Map<String, Object> stats = desempenhoPorMateria.get(idMateria);

                int acertos = (int) stats.getOrDefault("acertos", 0);
                int erros = (int) stats.getOrDefault("erros", 0);

                if (acertou) {
                    acertos++;
                } else {
                    erros++;
                }

                int total = acertos + erros;
                double taxaAcerto = ((double) acertos / total) * 100;

                stats.put("acertos", acertos);
                stats.put("erros", erros);
                stats.put("total", total);
                stats.put("taxaAcerto", String.format("%.2f", taxaAcerto) + "%");
            }
        }

        return desempenhoPorMateria;
    }

    /**
     * Busca todos os desempenhos cadastrados
     */
    public List<Desempenho> listarTodosDesempenhos() {
        return desempenhoRepository.buscarTodos();
    }

    /**
     * Busca desempenho por ID
     */
    public Optional<Desempenho> buscarDesempenhoPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }
        return desempenhoRepository.buscarPorId(id);
    }

    /**
     * Busca todos os desempenhos de um usuário
     */
    public List<Desempenho> listarDesempenhosPorUsuario(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }
        return desempenhoRepository.buscarPorUsuario(idUsuario);
    }

    /**
     * Busca desempenhos por curso
     */
    public List<Desempenho> listarDesempenhosPorCurso(Integer idCurso) {
        if (idCurso == null || idCurso <= 0) {
            throw new IllegalArgumentException("ID do curso inválido!");
        }
        return desempenhoRepository.buscarPorCurso(idCurso);
    }

    /**
     * Busca desempenhos por matéria
     */
    public List<Desempenho> listarDesempenhosPorMateria(Integer idMateria) {
        if (idMateria == null || idMateria <= 0) {
            throw new IllegalArgumentException("ID da matéria inválido!");
        }
        return desempenhoRepository.buscarPorMateria(idMateria);
    }

    /**
     * Deleta um desempenho
     */
    public boolean deletarDesempenho(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }

        Optional<Desempenho> desempenho = desempenhoRepository.buscarPorId(id);
        if (desempenho.isEmpty()) {
            throw new IllegalArgumentException("Desempenho não encontrado!");
        }

        return desempenhoRepository.deletar(id);
    }

    /**
     * Deleta todos os desempenhos de um usuário
     */
    public boolean deletarDesempenhosPorUsuario(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }
        return desempenhoRepository.deletarPorUsuario(idUsuario);
    }

    /**
     * Identifica pontos fortes (matérias com melhor desempenho)
     */
    public List<Map<String, Object>> identificarPontosFortes(Integer idUsuario, int limite) {
        Map<Integer, Map<String, Object>> desempenho = calcularDesempenhoPorMateria(idUsuario);

        List<Map<String, Object>> materias = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, Object>> entry : desempenho.entrySet()) {
            Map<String, Object> info = new HashMap<>();
            info.put("idMateria", entry.getKey());
            info.putAll(entry.getValue());
            materias.add(info);
        }

        // Ordena por taxa de acerto (decrescente)
        materias.sort((a, b) -> {
            String taxaA = (String) a.get("taxaAcerto");
            String taxaB = (String) b.get("taxaAcerto");
            double valorA = Double.parseDouble(taxaA.replace("%", ""));
            double valorB = Double.parseDouble(taxaB.replace("%", ""));
            return Double.compare(valorB, valorA);
        });

        return materias.subList(0, Math.min(limite, materias.size()));
    }

    /**
     * Identifica pontos fracos (matérias com pior desempenho)
     */
    public List<Map<String, Object>> identificarPontosFracos(Integer idUsuario, int limite) {
        Map<Integer, Map<String, Object>> desempenho = calcularDesempenhoPorMateria(idUsuario);

        List<Map<String, Object>> materias = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, Object>> entry : desempenho.entrySet()) {
            Map<String, Object> info = new HashMap<>();
            info.put("idMateria", entry.getKey());
            info.putAll(entry.getValue());
            materias.add(info);
        }

        // Ordena por taxa de acerto (crescente)
        materias.sort((a, b) -> {
            String taxaA = (String) a.get("taxaAcerto");
            String taxaB = (String) b.get("taxaAcerto");
            double valorA = Double.parseDouble(taxaA.replace("%", ""));
            double valorB = Double.parseDouble(taxaB.replace("%", ""));
            return Double.compare(valorA, valorB);
        });

        return materias.subList(0, Math.min(limite, materias.size()));
    }
}

