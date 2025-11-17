package com.simulados.controller;

import com.simulados.model.Questao;
import com.simulados.service.QuestaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST para gerenciar operações de Questao
 * Base URL: /api/questoes
 */
@RestController
@RequestMapping("/api/questoes")
@CrossOrigin(origins = "*")
public class QuestaoController {

    private final QuestaoService questaoService;

    // Construtor - instancia o service
    public QuestaoController() {
        this.questaoService = new QuestaoService();
    }

    /**
     * POST /api/questoes
     * Cadastra uma nova questão
     */
    @PostMapping
    public ResponseEntity<?> cadastrarQuestao(@RequestBody Map<String, Object> dados) {
        try {
            String enunciado = (String) dados.get("enunciado");
            String alternativa = (String) dados.get("alternativa");
            String respostaCorreta = (String) dados.get("respostaCorreta");
            Integer idMateria = (Integer) dados.get("idMateria");

            Questao questao = questaoService.cadastrarQuestao(enunciado, alternativa, respostaCorreta, idMateria);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Questão cadastrada com sucesso!");
            response.put("questao", questao);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao cadastrar questão: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/questoes
     * Lista todas as questões
     */
    @GetMapping
    public ResponseEntity<?> listarQuestoes() {
        try {
            List<Questao> questoes = questaoService.listarTodasQuestoes();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", questoes.size());
            response.put("questoes", questoes);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar questões: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/questoes/{id}
     * Busca uma questão por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarQuestaoPorId(@PathVariable Integer id) {
        try {
            Optional<Questao> questao = questaoService.buscarQuestaoPorId(id);

            if (questao.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("questao", questao.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Questão não encontrada!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar questão: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/questoes/materia/{idMateria}
     * Lista todas as questões de uma matéria
     */
    @GetMapping("/materia/{idMateria}")
    public ResponseEntity<?> listarQuestoesPorMateria(@PathVariable Integer idMateria) {
        try {
            List<Questao> questoes = questaoService.listarQuestoesPorMateria(idMateria);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", questoes.size());
            response.put("questoes", questoes);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar questões: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/questoes/aleatorias/{quantidade}
     * Busca N questões aleatórias
     */
    @GetMapping("/aleatorias/{quantidade}")
    public ResponseEntity<?> buscarQuestoesAleatorias(@PathVariable int quantidade) {
        try {
            List<Questao> questoes = questaoService.buscarQuestoesAleatorias(quantidade);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", questoes.size());
            response.put("questoes", questoes);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar questões: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/questoes/materia/{idMateria}/aleatorias/{quantidade}
     * Busca N questões aleatórias de uma matéria específica
     */
    @GetMapping("/materia/{idMateria}/aleatorias/{quantidade}")
    public ResponseEntity<?> buscarQuestoesAleatoriasPorMateria(@PathVariable Integer idMateria,
                                                                @PathVariable int quantidade) {
        try {
            List<Questao> questoes = questaoService.buscarQuestoesAleatoriasPorMateria(idMateria, quantidade);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", questoes.size());
            response.put("questoes", questoes);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar questões: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/questoes/{id}
     * Atualiza uma questão
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarQuestao(@PathVariable Integer id,
                                              @RequestBody Map<String, Object> dados) {
        try {
            String enunciado = (String) dados.get("enunciado");
            String alternativa = (String) dados.get("alternativa");
            String respostaCorreta = (String) dados.get("respostaCorreta");
            Integer idMateria = (Integer) dados.get("idMateria");

            boolean atualizado = questaoService.atualizarQuestao(id, enunciado, alternativa, respostaCorreta, idMateria);

            if (atualizado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Questão atualizada com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao atualizar questão!");
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
            response.put("mensagem", "Erro ao atualizar questão: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/questoes/{id}
     * Deleta uma questão
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarQuestao(@PathVariable Integer id) {
        try {
            boolean deletado = questaoService.deletarQuestao(id);

            if (deletado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Questão deletada com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao deletar questão!");
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
            response.put("mensagem", "Erro ao deletar questão: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/questoes/total
     * Retorna o total de questões cadastradas
     */
    @GetMapping("/total")
    public ResponseEntity<?> contarQuestoes() {
        try {
            int total = questaoService.contarQuestoes();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", total);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao contar questões: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

