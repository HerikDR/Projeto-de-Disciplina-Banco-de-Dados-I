package com.simulados.controller;

import com.simulados.model.Curso;
import com.simulados.service.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST para gerenciar operações de Curso
 * Base URL: /api/cursos
 */
@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
public class CursoController {

    private final CursoService cursoService;

    // Construtor - instancia o service
    public CursoController() {
        this.cursoService = new CursoService();
    }

    /**
     * POST /api/cursos
     * Cadastra um novo curso
     */
    @PostMapping
    public ResponseEntity<?> cadastrarCurso(@RequestBody Map<String, Object> dados) {
        try {
            Integer idUsuario = (Integer) dados.get("idUsuario");
            String nomeCurso = (String) dados.get("nomeCurso");

            Curso curso = cursoService.cadastrarCurso(idUsuario, nomeCurso);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Curso cadastrado com sucesso!");
            response.put("curso", curso);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao cadastrar curso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/cursos
     * Lista todos os cursos
     */
    @GetMapping
    public ResponseEntity<?> listarCursos() {
        try {
            List<Curso> cursos = cursoService.listarTodosCursos();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", cursos.size());
            response.put("cursos", cursos);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar cursos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/cursos/{id}
     * Busca um curso por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCursoPorId(@PathVariable Integer id) {
        try {
            Optional<Curso> curso = cursoService.buscarCursoPorId(id);

            if (curso.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("curso", curso.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Curso não encontrado!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar curso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/cursos/usuario/{idUsuario}
     * Lista todos os cursos de um usuário
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> listarCursosPorUsuario(@PathVariable Integer idUsuario) {
        try {
            List<Curso> cursos = cursoService.listarCursosPorUsuario(idUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", cursos.size());
            response.put("cursos", cursos);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar cursos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/cursos/{id}
     * Atualiza um curso
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCurso(@PathVariable Integer id,
                                            @RequestBody Map<String, Object> dados) {
        try {
            Integer idUsuario = (Integer) dados.get("idUsuario");
            String nomeCurso = (String) dados.get("nomeCurso");

            boolean atualizado = cursoService.atualizarCurso(id, idUsuario, nomeCurso);

            if (atualizado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Curso atualizado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao atualizar curso!");
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
            response.put("mensagem", "Erro ao atualizar curso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/cursos/{id}
     * Deleta um curso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCurso(@PathVariable Integer id) {
        try {
            boolean deletado = cursoService.deletarCurso(id);

            if (deletado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Curso deletado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao deletar curso!");
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
            response.put("mensagem", "Erro ao deletar curso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/cursos/total
     * Retorna o total de cursos cadastrados
     */
    @GetMapping("/total")
    public ResponseEntity<?> contarCursos() {
        try {
            int total = cursoService.contarCursos();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", total);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao contar cursos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

