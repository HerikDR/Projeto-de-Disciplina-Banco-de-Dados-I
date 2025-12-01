package com.simulados.controller;

import com.simulados.model.RespostaUsuario;
import com.simulados.service.RespostaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciar operações de RespostaUsuario
 * Base URL: /api/respostas
 */
@RestController
@RequestMapping("/api/respostas")
@CrossOrigin(origins = "*")
public class RespostaController {

    private final RespostaService respostaService;

    // construtor - instancia o service
    public RespostaController() throws SQLException {
        this.respostaService = new RespostaService();
    }

    /**
     * POST /api/respostas/registrar
     * registra a resposta de um usuário para uma questão
     * Body: { "idSimulado": 1, "idQuestao": 5, "idUsuario": 1, "respostaFornecida": "A" }
     */
    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrarResposta(@RequestBody Map<String, Object> dados) {
        try {
            Integer idSimulado = (Integer) dados.get("idSimulado");
            Integer idQuestao = (Integer) dados.get("idQuestao");
            Integer idUsuario = (Integer) dados.get("idUsuario");
            String respostaFornecida = (String) dados.get("respostaFornecida");

            RespostaUsuario resposta = respostaService.registrarResposta(
                    idSimulado, idQuestao, idUsuario, respostaFornecida
            );

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Resposta registrada com sucesso!");
            response.put("resposta", resposta);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao registrar resposta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/respostas/corrigir/{idSimulado}
     * ⚠️ CORRIGIDO: MUDADO DE POST PARA GET
     * corrige um simulado e retorna o resultado completo
     * retorna: acertos, erros, nota, percentual, detalhes por questão
     */
    @GetMapping("/corrigir/{idSimulado}")
    public ResponseEntity<Map<String, Object>> corrigirSimulado(@PathVariable Integer idSimulado) {
        try {
            Map<String, Object> resultado = respostaService.corrigirSimulado(idSimulado);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Simulado corrigido com sucesso!");
            response.putAll(resultado);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao corrigir simulado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/respostas/desempenho-materia/{idSimulado}
     * calcula desempenho por matéria em um simulado
     */
    @GetMapping("/desempenho-materia/{idSimulado}")
    public ResponseEntity<Map<String, Object>> calcularDesempenhoPorMateria(@PathVariable Integer idSimulado) {
        try {
            Map<Integer, Map<String, Integer>> desempenho =
                    respostaService.calcularDesempenhoPorMateria(idSimulado);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("desempenhoPorMateria", desempenho);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao calcular desempenho: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/respostas/simulado/{idSimulado}
     * lista todas as respostas de um simulado
     */
    @GetMapping("/simulado/{idSimulado}")
    public ResponseEntity<Map<String, Object>> listarRespostasPorSimulado(@PathVariable Integer idSimulado) {
        try {
            List<RespostaUsuario> respostas = respostaService.listarRespostasPorSimulado(idSimulado);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", respostas.size());
            response.put("respostas", respostas);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar respostas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/respostas/usuario/{idUsuario}
     * lista todas as respostas de um usuário
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Map<String, Object>> listarRespostasPorUsuario(@PathVariable Integer idUsuario) {
        try {
            List<RespostaUsuario> respostas = respostaService.listarRespostasPorUsuario(idUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", respostas.size());
            response.put("respostas", respostas);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar respostas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/respostas/{id}
     * deleta uma resposta específica
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletarResposta(@PathVariable Integer id) {
        try {
            boolean deletado = respostaService.deletarResposta(id);

            if (deletado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Resposta deletada com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao deletar resposta!");
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
            response.put("mensagem", "Erro ao deletar resposta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/respostas/simulado/{idSimulado}
     * deleta todas as respostas de um simulado (reset)
     */
    @DeleteMapping("/simulado/{idSimulado}")
    public ResponseEntity<Map<String, Object>> deletarRespostasPorSimulado(@PathVariable Integer idSimulado) {
        try {
            boolean deletado = respostaService.deletarRespostasPorSimulado(idSimulado);

            if (deletado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Respostas deletadas com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao deletar respostas!");
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
            response.put("mensagem", "Erro ao deletar respostas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/respostas/simulado/{idSimulado}/total
     * conta quantas respostas foram registradas em um simulado
     */
    @GetMapping("/simulado/{idSimulado}/total")
    public ResponseEntity<Map<String, Object>> contarRespostasPorSimulado(@PathVariable Integer idSimulado) {
        try {
            int total = respostaService.contarRespostasPorSimulado(idSimulado);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", total);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao contar respostas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}