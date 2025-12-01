package com.simulados.controller;

import com.simulados.model.Desempenho;
import com.simulados.service.DesempenhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/desempenho")
@CrossOrigin(origins = "*")
public class DesempenhoController {

    private DesempenhoService desempenhoService;

    public DesempenhoController() throws SQLException {
        this.desempenhoService = new DesempenhoService();
    }

    /**
     * POST /api/desempenho/registrar
     * Registra um desempenho
     */
    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrarDesempenho(@RequestBody Map<String, Object> dados) {
        try {
            Integer idUsuario = (Integer) dados.get("idUsuario");
            Integer idCurso = (Integer) dados.get("idCurso");
            Integer idMateria = (Integer) dados.get("idMateria");

            Desempenho desempenho = new Desempenho();
            desempenho.setIdUsuario(idUsuario);
            desempenho.setIdCurso(idCurso);
            desempenho.setIdMateria(idMateria);

            desempenhoService.salvarDesempenho(desempenho);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Desempenho registrado com sucesso!");
            response.put("desempenho", desempenho);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao registrar desempenho: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/desempenho/{id}
     * Busca um desempenho por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable int id) {
        try {
            Desempenho desempenho = desempenhoService.buscarPorId(id);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("desempenho", desempenho);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

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
    public ResponseEntity<Map<String, Object>> listarPorUsuario(@PathVariable int idUsuario) {
        try {
            List<Desempenho> desempenhos = desempenhoService.buscarPorUsuario(idUsuario);

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
     * GET /api/desempenho/curso/{idCurso}
     * Lista desempenhos por curso
     */
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<Map<String, Object>> listarPorCurso(@PathVariable int idCurso) {
        try {
            List<Desempenho> desempenhos = desempenhoService.buscarPorCurso(idCurso);

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
     * GET /api/desempenho/materia/{idMateria}
     * Lista desempenhos por matéria
     */
    @GetMapping("/materia/{idMateria}")
    public ResponseEntity<Map<String, Object>> listarPorMateria(@PathVariable int idMateria) {
        try {
            List<Desempenho> desempenhos = desempenhoService.buscarPorMateria(idMateria);

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
     * GET /api/desempenho
     * Lista todos os desempenhos
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodos() {
        try {
            List<Desempenho> desempenhos = desempenhoService.buscarTodos();

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
     * GET /api/desempenho/por-materia/{idUsuario}
     * Retorna desempenho agregado por matéria de um usuário
     */
    @GetMapping("/por-materia/{idUsuario}")
    public ResponseEntity<Map<String, Object>> buscarDesempenhoPorMateria(@PathVariable int idUsuario) {
        try {
            List<Map<String, Object>> desempenho = desempenhoService.buscarDesempenhoPorMateria(idUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("desempenhoPorMateria", desempenho);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar desempenho por matéria: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/desempenho/estatisticas/{idUsuario}/{idSimulado}
     * Retorna estatísticas de um simulado específico
     */
    @GetMapping("/estatisticas/{idUsuario}/{idSimulado}")
    public ResponseEntity<Map<String, Object>> buscarEstatisticasSimulado(
            @PathVariable int idUsuario,
            @PathVariable int idSimulado) {
        try {
            Map<String, Object> estatisticas = desempenhoService.buscarEstatisticasSimulado(idUsuario, idSimulado);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.putAll(estatisticas);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar estatísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/desempenho/estatisticas-materia/{idUsuario}/{idSimulado}
     * Retorna estatísticas por matéria de um simulado específico
     */
    @GetMapping("/estatisticas-materia/{idUsuario}/{idSimulado}")
    public ResponseEntity<Map<String, Object>> buscarEstatisticasPorMateriaDoSimulado(
            @PathVariable int idUsuario,
            @PathVariable int idSimulado) {
        try {
            List<Map<String, Object>> estatisticas = desempenhoService.buscarEstatisticasPorMateriaDoSimulado(idUsuario, idSimulado);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("estatisticasPorMateria", estatisticas);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar estatísticas por matéria: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/desempenho/{id}
     * Atualiza um desempenho
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(
            @PathVariable int id,
            @RequestBody Map<String, Object> dados) {
        try {
            Desempenho desempenho = desempenhoService.buscarPorId(id);

            if (dados.containsKey("idUsuario")) {
                desempenho.setIdUsuario((Integer) dados.get("idUsuario"));
            }

            if (dados.containsKey("idCurso")) {
                desempenho.setIdCurso((Integer) dados.get("idCurso"));
            }

            if (dados.containsKey("idMateria")) {
                desempenho.setIdMateria((Integer) dados.get("idMateria"));
            }

            desempenhoService.atualizarDesempenho(desempenho);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Desempenho atualizado com sucesso!");
            response.put("desempenho", desempenho);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao atualizar desempenho: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/desempenho/{id}
     * Deleta um desempenho
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable int id) {
        try {
            desempenhoService.deletarDesempenho(id);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Desempenho deletado com sucesso!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao deletar desempenho: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
