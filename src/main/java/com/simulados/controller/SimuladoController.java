package com.simulados.controller;

import com.simulados.model.Simulado;
import com.simulados.service.SimuladoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST para gerenciar operações de Simulado
 * Base URL: /api/simulados
 */
@RestController
@RequestMapping("/api/simulados")
@CrossOrigin(origins = "*")
public class SimuladoController {

    private final SimuladoService simuladoService;

    // Construtor - instancia o service
    public SimuladoController() {
        this.simuladoService = new SimuladoService();
    }

    /**
     * POST /api/simulados/criar
     * Cria um novo simulado vazio para um usuário
     */
    @PostMapping("/criar")
    public ResponseEntity<?> criarSimulado(@RequestBody Map<String, Object> dados) {
        try {
            Integer idUsuario = (Integer) dados.get("idUsuario");

            Simulado simulado = simuladoService.criarSimulado(idUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Simulado criado com sucesso!");
            response.put("simulado", simulado);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao criar simulado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/simulados/gerar-aleatorio
     * Gera um simulado com N questões aleatórias
     * Body: { "idUsuario": 1, "quantidadeQuestoes": 10 }
     */
    @PostMapping("/gerar-aleatorio")
    public ResponseEntity<?> gerarSimuladoAleatorio(@RequestBody Map<String, Object> dados) {
        try {
            Integer idUsuario = (Integer) dados.get("idUsuario");
            Integer quantidadeQuestoes = (Integer) dados.get("quantidadeQuestoes");

            Map<String, Object> resultado = simuladoService.gerarSimuladoAleatorio(idUsuario, quantidadeQuestoes);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Simulado gerado com sucesso!");
            response.putAll(resultado);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao gerar simulado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/simulados/gerar-por-materias
     * Gera um simulado balanceado por matérias
     * Body: { "idUsuario": 1, "materias": { "1": 5, "2": 3 } }
     * Onde a chave é o idMateria e o valor é a quantidade de questões
     */
    @PostMapping("/gerar-por-materias")
    public ResponseEntity<?> gerarSimuladoPorMaterias(@RequestBody Map<String, Object> dados) {
        try {
            Integer idUsuario = (Integer) dados.get("idUsuario");

            @SuppressWarnings("unchecked")
            Map<String, Integer> materiasString = (Map<String, Integer>) dados.get("materias");

            // Converte as chaves de String para Integer
            Map<Integer, Integer> materias = new HashMap<>();
            for (Map.Entry<String, Integer> entry : materiasString.entrySet()) {
                materias.put(Integer.parseInt(entry.getKey()), entry.getValue());
            }

            Map<String, Object> resultado = simuladoService.gerarSimuladoPorMaterias(idUsuario, materias);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Simulado gerado com sucesso!");
            response.putAll(resultado);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao gerar simulado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/simulados
     * Lista todos os simulados
     */
    @GetMapping
    public ResponseEntity<?> listarSimulados() {
        try {
            List<Simulado> simulados = simuladoService.listarTodosSimulados();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", simulados.size());
            response.put("simulados", simulados);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar simulados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/simulados/{id}
     * Busca um simulado por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarSimuladoPorId(@PathVariable Integer id) {
        try {
            Optional<Simulado> simulado = simuladoService.buscarSimuladoPorId(id);

            if (simulado.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("simulado", simulado.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Simulado não encontrado!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar simulado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/simulados/usuario/{idUsuario}
     * Lista todos os simulados de um usuário
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> listarSimuladosPorUsuario(@PathVariable Integer idUsuario) {
        try {
            List<Simulado> simulados = simuladoService.listarSimuladosPorUsuario(idUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", simulados.size());
            response.put("simulados", simulados);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar simulados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/simulados/usuario/{idUsuario}/ultimos/{limite}
     * Busca os últimos N simulados de um usuário
     */
    @GetMapping("/usuario/{idUsuario}/ultimos/{limite}")
    public ResponseEntity<?> buscarUltimosSimulados(@PathVariable Integer idUsuario,
                                                    @PathVariable int limite) {
        try {
            List<Simulado> simulados = simuladoService.buscarUltimosSimulados(idUsuario, limite);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", simulados.size());
            response.put("simulados", simulados);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar simulados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/simulados/{id}
     * Deleta um simulado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarSimulado(@PathVariable Integer id) {
        try {
            boolean deletado = simuladoService.deletarSimulado(id);

            if (deletado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Simulado deletado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao deletar simulado!");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao deletar simulado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/simulados/total
     * Retorna o total de simulados cadastrados
     */
    @GetMapping("/total")
    public ResponseEntity<?> contarSimulados() {
        try {
            int total = simuladoService.contarSimulados();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", total);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao contar simulados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

