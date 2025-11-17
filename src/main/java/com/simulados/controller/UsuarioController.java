package com.simulados.controller;

import com.simulados.model.Usuario;
import com.simulados.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST para gerenciar operações de Usuario
 * Base URL: /api/usuarios
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem (frontend)
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Construtor - instancia o service
    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }

    /**
     * POST /api/usuarios/cadastrar
     * Cadastra um novo usuário
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Map<String, String> dados) {
        try {
            String nome = dados.get("nome");
            String email = dados.get("email");
            String senha = dados.get("senha");

            Usuario usuario = usuarioService.cadastrarUsuario(nome, email, senha);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Usuário cadastrado com sucesso!");
            response.put("usuario", usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao cadastrar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/usuarios/login
     * Faz login do usuário
     */
    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody Map<String, String> dados) {
        try {
            String email = dados.get("email");
            String senha = dados.get("senha");

            Optional<Usuario> usuario = usuarioService.fazerLogin(email, senha);

            if (usuario.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Login realizado com sucesso!");
                response.put("usuario", usuario.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Email ou senha incorretos!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao fazer login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/usuarios
     * Lista todos os usuários
     */
    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.listarTodosUsuarios();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", usuarios.size());
            response.put("usuarios", usuarios);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar usuários: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/usuarios/{id}
     * Busca um usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuarioPorId(@PathVariable Integer id) {
        try {
            Optional<Usuario> usuario = usuarioService.buscarUsuarioPorId(id);

            if (usuario.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("usuario", usuario.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Usuário não encontrado!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/usuarios/{id}
     * Atualiza os dados de um usuário
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Integer id,
                                              @RequestBody Map<String, String> dados) {
        try {
            String nome = dados.get("nome");
            String email = dados.get("email");
            String senha = dados.get("senha");

            boolean atualizado = usuarioService.atualizarUsuario(id, nome, email, senha);

            if (atualizado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Usuário atualizado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao atualizar usuário!");
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
            response.put("mensagem", "Erro ao atualizar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/usuarios/{id}
     * Deleta um usuário
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Integer id) {
        try {
            boolean deletado = usuarioService.deletarUsuario(id);

            if (deletado) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Usuário deletado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao deletar usuário!");
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
            response.put("mensagem", "Erro ao deletar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/usuarios/total
     * Retorna o total de usuários cadastrados
     */
    @GetMapping("/total")
    public ResponseEntity<?> contarUsuarios() {
        try {
            int total = usuarioService.contarUsuarios();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("total", total);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao contar usuários: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

