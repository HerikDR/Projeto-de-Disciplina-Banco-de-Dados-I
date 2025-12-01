package com.simulados.controller;

import com.simulados.model.Materia;
import com.simulados.service.MateriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST para gerenciar operações de Materia
 * Base URL: /api/materias
 */
@RestController
@RequestMapping("/api/materias")
@CrossOrigin(origins = "*")
public class MateriaController {

    private final MateriaService materiaService;

    // Construtor - instancia o service
    public MateriaController() throws SQLException {
        this.materiaService = new MateriaService();
    }

    /**
     * POST /api/materias
     * Cadastra uma nova matéria
     */
    @PostMapping
    public ResponseEntity<?> cadastrarMateria(@RequestBody Map<String, String> dados) {
        try {
            String nome = dados.get("nome");

            Materia materia = materiaService.cadastrarMateria(nome);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Matéria cadastrada com sucesso!");
            response.put("materia", materia);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao cadastrar matéria: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/materias
     * Lista todas as matérias
     */
    @GetMapping
    public ResponseEntity<?> listarMaterias() {
        try {
            List<Materia> materias = materiaService.listarTodasMaterias();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", materias.size());
            response.put("materias", materias);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar matérias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/materias/{id}
     * Busca uma matéria por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarMateriaPorId(@PathVariable Integer id) {
        try {
            Optional<Materia> materia = materiaService.buscarMateriaPorId(id);

            if (materia.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("materia", materia.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Matéria não encontrada!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar matéria: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/materias/nome/{nome}
     * Busca uma matéria por nome
     */
    @GetMapping("/nome/{nome}")
    public ResponseEntity<?> buscarMateriaPorNome(@PathVariable String nome) {
        try {
            Optional<Materia> materia = materiaService.buscarMateriaPorNome(nome);

            if (materia.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("materia", materia.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Matéria não encontrada!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar matéria: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/materias/{id}
     * Atualiza uma matéria
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarMateria(@PathVariable Integer id,
                                              @RequestBody Map<String, String> dados) {
        try {
            String novoNome = dados.get("nome");

            boolean atualizado = materiaService.atualizarMateria(id, novoNome);

            if (atualizado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Matéria atualizada com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao atualizar matéria!");
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
            response.put("mensagem", "Erro ao atualizar matéria: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/materias/{id}
     * Deleta uma matéria
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarMateria(@PathVariable Integer id) {
        try {
            boolean deletado = materiaService.deletarMateria(id);

            if (deletado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Matéria deletada com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao deletar matéria!");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao deletar matéria: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/materias/total
     * Retorna o total de matérias cadastradas
     */
    @GetMapping("/total")
    public ResponseEntity<?> contarMaterias() {
        try {
            int total = materiaService.contarMaterias();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", total);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao contar matérias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
