package com.simulados.controller;

import com.simulados.model.Simulado;
import com.simulados.model.Usuario;
import com.simulados.service.SimuladoService;
import com.simulados.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/simulados")
@CrossOrigin(origins = "*")
public class SimuladoController {

    private SimuladoService simuladoService = new SimuladoService();
    private UsuarioService usuarioService = new UsuarioService();

    /**
     * POST /api/simulados/gerar-aleatorio
     * Gera um simulado aleatório com questões distribuídas entre as matérias
     * Body OPÇÃO 1: { "idUsuario": 1, "quantidadeQuestoes": 10 }
     * Body OPÇÃO 2: { "idUsuario": 1, "materias": { "1": 5, "2": 3 } }
     */
    @PostMapping("/gerar-aleatorio")
    public ResponseEntity<Map<String, Object>> gerarSimuladoAleatorio(@RequestBody Map<String, Object> dados) {
        try {
            Integer idUsuario = (Integer) dados.get("idUsuario");

            // verificar se o usuário é ALUNO
            Usuario usuario = usuarioService.buscarPorId(idUsuario);
            if (usuario.isAdmin()) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Administradores não podem realizar simulados");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Map<Integer, Integer> materias = new HashMap<>();

            // verifica se foi enviado "materias" ou "quantidadeQuestoes"
            if (dados.containsKey("materias")) {
                // OPÇÃO 1: matérias específicas
                @SuppressWarnings("unchecked")
                Map<String, Object> materiasRaw = (Map<String, Object>) dados.get("materias");

                if (materiasRaw == null || materiasRaw.isEmpty()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("sucesso", false);
                    response.put("mensagem", "É necessário selecionar pelo menos uma matéria");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }

                for (Map.Entry<String, Object> entry : materiasRaw.entrySet()) {
                    Integer idMateria = Integer.parseInt(entry.getKey());
                    Integer quantidade = ((Number) entry.getValue()).intValue();
                    materias.put(idMateria, quantidade);
                }

            } else if (dados.containsKey("quantidadeQuestoes")) {
                // OPÇÃO 2: quantidade total, distribuir automaticamente
                Integer quantidadeTotal = (Integer) dados.get("quantidadeQuestoes");

                if (quantidadeTotal <= 0) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("sucesso", false);
                    response.put("mensagem", "Quantidade de questões deve ser maior que zero");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }

                // buscar todas as matérias e distribuir igualmente
                materias = simuladoService.distribuirQuestoesPorMaterias(quantidadeTotal);

            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "É necessário informar 'quantidadeQuestoes' ou 'materias'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Map<String, Object> resultado = simuladoService.gerarSimuladoAleatorio(idUsuario, materias);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Simulado gerado com sucesso!");
            response.putAll(resultado);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao gerar simulado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/simulados/criar
     * Cria um novo simulado
     * Body: { "idUsuario": 1 }
     */
    @PostMapping("/criar")
    public ResponseEntity<Map<String, Object>> criarSimulado(@RequestBody Map<String, Object> dados) {
        try {
            Integer idUsuario = (Integer) dados.get("idUsuario");

            // verificar se o usuário é ALUNO
            Usuario usuario = usuarioService.buscarPorId(idUsuario);
            if (usuario.isAdmin()) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Administradores não podem realizar simulados");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Simulado simulado = new Simulado();
            simulado.setIdUsuario(idUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Simulado criado com sucesso!");
            response.put("simulado", simulado);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao criar simulado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/simulados/usuario/{idUsuario}
     * Lista todos os simulados de um usuário
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Map<String, Object>> buscarPorUsuario(@PathVariable int idUsuario) {
        try {
            List<Simulado> simulados = simuladoService.buscarPorUsuario(idUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("simulados", simulados);
            response.put("total", simulados.size());

            return ResponseEntity.ok(response);

        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar simulados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/simulados/{idSimulado}/questoes
     * Retorna todas as questões de um simulado
     */
    @GetMapping("/{idSimulado}/questoes")
    public ResponseEntity<Map<String, Object>> buscarQuestoesDoSimulado(@PathVariable int idSimulado) {
        try {
            List<Map<String, Object>> questoes = simuladoService.buscarQuestoesDoSimulado(idSimulado);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("questoes", questoes);
            response.put("total", questoes.size());

            return ResponseEntity.ok(response);

        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar questões: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/simulados/{id}
     * Busca um simulado por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable int id) {
        try {
            Simulado simulado = simuladoService.buscarPorId(id);
            if (simulado == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Simulado não encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("simulado", simulado);
            return ResponseEntity.ok(response);

        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar simulado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/simulados/{id}
     * Deleta um simulado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable int id) {
        try {
            simuladoService.deletar(id);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Simulado deletado com sucesso!");

            return ResponseEntity.ok(response);

        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao deletar simulado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}


