package com.simulados.controller;

import com.simulados.model.Desempenho;
import com.simulados.service.DesempenhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST para gerenciar operações de Desempenho
 * Base URL: /api/desempenho
 */
@RestController
@RequestMapping("/api/desempenho")
@CrossOrigin(origins = "*")
public class DesempenhoController {

    private final DesempenhoService desempenhoService;

    // Construtor - instancia o service
    public DesempenhoController() {
        this.desempenhoService = new DesempenhoService();
    }

    /**
     * POST /api/desempenho/registrar
     * Registra um desempenho
     * Body: { "idUsuario": 1, "idCurso": 1, "idMateria": 1 }
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarDesempenho(@RequestBody Map<String, Object> dados) {
        try {
            Integer idUsuario = (Integer) dados.get("idUsuario");
            Integer idCurso = (Integer) dados.get("idCurso");
            Integer idMateria = (Integer) dados.get("idMateria");

            Desempenho desempenho = desempenhoService.registrarDesempenho(idUsuario, idCurso, idMateria);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Desempenho registrado com sucesso!");
            response.put("desempenho", desempenho);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao registrar desempenho: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/desempenho/estatisticas/{idUsuario}
     * Calcula estatísticas gerais de um usuário
     * Retorna: total de questões, acertos, erros, taxa de acerto, nota média
     */
    @GetMapping("/estatisticas/{idUsuario}")
    public ResponseEntity<?> calcularEstatisticasGerais(@PathVariable Integer idUsuario) {
        try {
            Map<String, Object> estatisticas = desempenhoService.calcularEstatisticasGerais(idUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.putAll(estatisticas);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao calcular estatísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/desempenho/por-materia/{idUsuario}
     * Calcula desempenho de um usuário separado por matéria
     */
    @GetMapping("/por-materia/{idUsuario}")
    public ResponseEntity<?> calcularDesempenhoPorMateria(@PathVariable Integer idUsuario) {
        try {
            Map<Integer, Map<String, Object>> desempenho =
                    desempenhoService.calcularDesempenhoPorMateria(idUsuario);

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
     * GET /api/desempenho/pontos-fortes/{idUsuario}/{limite}
     * Identifica as matérias com melhor desempenho do usuário
     */
    @GetMapping("/pontos-fortes/{idUsuario}/{limite}")
    public ResponseEntity<?> identificarPontosFortes(@PathVariable Integer idUsuario,
                                                     @PathVariable int limite) {
        try {
            List<Map<String, Object>> pontosFortes =
                    desempenhoService.identificarPontosFortes(idUsuario, limite);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("pontosFortes", pontosFortes);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao identificar pontos fortes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/desempenho/pontos-fracos/{idUsuario}/{limite}
     * Identifica as matérias com pior desempenho do usuário (para focar estudos)
     */
    @GetMapping("/pontos-fracos/{idUsuario}/{limite}")
    public ResponseEntity<?> identificarPontosFracos(@PathVariable Integer idUsuario,
                                                     @PathVariable int limite) {
        try {
            List<Map<String, Object>> pontosFracos =
                    desempenhoService.identificarPontosFracos(idUsuario, limite);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("pontosFracos", pontosFracos);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao identificar pontos fracos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/desempenho
     * Lista todos os desempenhos
     */
    @GetMapping
    public ResponseEntity<?> listarDesempenhos() {
        try {
            List<Desempenho> desempenhos = desempenhoService.listarTodosDesempenhos();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", desempenhos.size());
            response.put("desempenhos", desempenhos);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar desempenhos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/desempenho/{id}
     * Busca um desempenho por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarDesempenhoPorId(@PathVariable Integer id) {
        try {
            Optional<Desempenho> desempenho = desempenhoService.buscarDesempenhoPorId(id);

            if (desempenho.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("desempenho", desempenho.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Desempenho não encontrado!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar desempenho: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/desempenho/usuario/{idUsuario}
     * Lista todos os desempenhos de um usuário
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> listarDesempenhosPorUsuario(@PathVariable Integer idUsuario) {
        try {
            List<Desempenho> desempenhos = desempenhoService.listarDesempenhosPorUsuario(idUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", desempenhos.size());
            response.put("desempenhos", desempenhos);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar desempenhos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/desempenho/{id}
     * Deleta um desempenho
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarDesempenho(@PathVariable Integer id) {
        try {
            boolean deletado = desempenhoService.deletarDesempenho(id);

            if (deletado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Desempenho deletado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao deletar desempenho!");
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
            response.put("mensagem", "Erro ao deletar desempenho: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

