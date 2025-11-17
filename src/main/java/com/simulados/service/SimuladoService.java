package com.simulados.service;

import com.simulados.model.Questao;
import com.simulados.model.Realiza;
import com.simulados.model.Simulado;
import com.simulados.repository.*;

import java.util.*;

/**
 * Service com lógica de negócio para operações relacionadas a Simulado
 * Inclui lógica para gerar simulados balanceados com questões aleatórias
 */
public class SimuladoService {

    private final SimuladoRepository simuladoRepository;
    private final UsuarioRepository usuarioRepository;
    private final QuestaoRepository questaoRepository;
    private final RealizaRepository realizaRepository;

    // Construtor - instancia os repositories
    public SimuladoService() {
        this.simuladoRepository = new SimuladoRepositoryImpl();
        this.usuarioRepository = new UsuarioRepositoryImpl();
        this.questaoRepository = new QuestaoRepositoryImpl();
        this.realizaRepository = new RealizaRepositoryImpl();
    }

    /**
     * Cria um novo simulado para um usuário
     * Registra o relacionamento na tabela Realiza
     */
    public Simulado criarSimulado(Integer idUsuario) {
        // Validação: verifica se o usuário existe
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (usuarioRepository.buscarPorId(idUsuario).isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        // Cria o simulado
        Simulado novoSimulado = new Simulado(idUsuario);
        Simulado simuladoSalvo = simuladoRepository.salvar(novoSimulado);

        // Registra o relacionamento na tabela Realiza
        if (simuladoSalvo != null) {
            Realiza realiza = new Realiza(idUsuario, simuladoSalvo.getIdSimulado());
            realizaRepository.salvar(realiza);
        }

        return simuladoSalvo;
    }

    /**
     * Gera um simulado balanceado com N questões aleatórias
     * Retorna o ID do simulado criado e a lista de questões selecionadas
     */
    public Map<String, Object> gerarSimuladoAleatorio(Integer idUsuario, int quantidadeQuestoes) {
        // Validação
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (usuarioRepository.buscarPorId(idUsuario).isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        if (quantidadeQuestoes <= 0) {
            throw new IllegalArgumentException("Quantidade de questões deve ser maior que zero!");
        }

        // Verifica se existem questões suficientes
        int totalQuestoes = questaoRepository.contarTodas();
        if (totalQuestoes < quantidadeQuestoes) {
            throw new IllegalStateException(
                    "Não há questões suficientes! Existem apenas " + totalQuestoes +
                            " questões cadastradas."
            );
        }

        // Cria o simulado
        Simulado simulado = criarSimulado(idUsuario);

        // Busca questões aleatórias
        List<Questao> questoes = questaoRepository.buscarQuestoesAleatorias(quantidadeQuestoes);

        // Monta o resultado
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("simulado", simulado);
        resultado.put("questoes", questoes);
        resultado.put("totalQuestoes", questoes.size());

        return resultado;
    }

    /**
     * Gera um simulado balanceado por matérias específicas
     * Permite especificar quantas questões de cada matéria
     */
    public Map<String, Object> gerarSimuladoPorMaterias(Integer idUsuario,
                                                        Map<Integer, Integer> materiaQuantidade) {
        // Validação
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (usuarioRepository.buscarPorId(idUsuario).isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        if (materiaQuantidade == null || materiaQuantidade.isEmpty()) {
            throw new IllegalArgumentException("Deve especificar pelo menos uma matéria!");
        }

        // Cria o simulado
        Simulado simulado = criarSimulado(idUsuario);

        // Busca questões para cada matéria
        List<Questao> todasQuestoes = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : materiaQuantidade.entrySet()) {
            Integer idMateria = entry.getKey();
            Integer quantidade = entry.getValue();

            // Verifica se há questões suficientes para essa matéria
            int totalPorMateria = questaoRepository.contarPorMateria(idMateria);
            if (totalPorMateria < quantidade) {
                throw new IllegalStateException(
                        "Não há questões suficientes para a matéria ID " + idMateria +
                                ". Existem apenas " + totalPorMateria + " questões."
                );
            }

            // Busca questões aleatórias da matéria
            List<Questao> questoesDaMateria = questaoRepository
                    .buscarQuestoesAleatoriasPorMateria(idMateria, quantidade);
            todasQuestoes.addAll(questoesDaMateria);
        }

        // Embaralha a ordem das questões
        Collections.shuffle(todasQuestoes);

        // Monta o resultado
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("simulado", simulado);
        resultado.put("questoes", todasQuestoes);
        resultado.put("totalQuestoes", todasQuestoes.size());

        return resultado;
    }

    /**
     * Busca todos os simulados
     */
    public List<Simulado> listarTodosSimulados() {
        return simuladoRepository.buscarTodos();
    }

    /**
     * Busca um simulado por ID
     */
    public Optional<Simulado> buscarSimuladoPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }
        return simuladoRepository.buscarPorId(id);
    }

    /**
     * Busca todos os simulados de um usuário
     */
    public List<Simulado> listarSimuladosPorUsuario(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (usuarioRepository.buscarPorId(idUsuario).isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        return simuladoRepository.buscarPorUsuario(idUsuario);
    }

    /**
     * Busca os últimos N simulados de um usuário
     */
    public List<Simulado> buscarUltimosSimulados(Integer idUsuario, int limite) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (limite <= 0) {
            throw new IllegalArgumentException("Limite deve ser maior que zero!");
        }

        return simuladoRepository.buscarUltimosSimulados(idUsuario, limite);
    }

    /**
     * Deleta um simulado do sistema
     * ATENÇÃO: Isso também deletará respostas relacionadas (CASCADE)
     */
    public boolean deletarSimulado(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }

        Optional<Simulado> simulado = simuladoRepository.buscarPorId(id);
        if (simulado.isEmpty()) {
            throw new IllegalArgumentException("Simulado não encontrado!");
        }

        // Deleta o relacionamento na tabela Realiza
        realizaRepository.deletarPorSimulado(id);

        // Deleta o simulado
        return simuladoRepository.deletar(id);
    }

    /**
     * Conta o total de simulados cadastrados
     */
    public int contarSimulados() {
        return simuladoRepository.contarTodos();
    }

    /**
     * Conta quantos simulados um usuário realizou
     */
    public int contarSimuladosPorUsuario(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            return 0;
        }
        return simuladoRepository.contarPorUsuario(idUsuario);
    }

    /**
     * Verifica se um simulado existe
     */
    public boolean simuladoExiste(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return simuladoRepository.buscarPorId(id).isPresent();
    }

    /**
     * Verifica se um simulado pertence a um usuário
     */
    public boolean simuladoPertenceAoUsuario(Integer idSimulado, Integer idUsuario) {
        Optional<Simulado> simulado = simuladoRepository.buscarPorId(idSimulado);
        return simulado.isPresent() && simulado.get().getIdUsuario().equals(idUsuario);
    }
}
