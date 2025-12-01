package com.simulados.controller;

import com.simulados.model.Usuario;
import com.simulados.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private UsuarioService usuarioService = new UsuarioService();

    /**
     * POST /api/usuarios/cadastrar
     * Cadastra um novo usuário
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<Map<String, Object>> cadastrarUsuario(@RequestBody Map<String, String> dados) {
        try {
            String nome = dados.get("nome");
            String email = dados.get("email");
            String senha = dados.get("senha");
            String tipoUsuario = dados.get("tipoUsuario");

            // se não informar o tipo, assume ALUNO por padrão
            if (tipoUsuario == null || tipoUsuario.trim().isEmpty()) {
                tipoUsuario = Usuario.TIPO_ALUNO;
            }

            Usuario usuario = new Usuario(nome, email, senha, tipoUsuario);
            usuarioService.cadastrarUsuario(usuario);

            // ⚠️ LOG DE DEBUG - REMOVER DEPOIS
            System.out.println("===== DEBUG CADASTRO =====");
            System.out.println("ID do usuário: " + usuario.getIdUsuario());
            System.out.println("Nome: " + usuario.getNome());
            System.out.println("Email: " + usuario.getEmail());
            System.out.println("Tipo: " + usuario.getTipoUsuario());
            System.out.println("==========================");

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Usuário cadastrado com sucesso!");
            response.put("usuario", converterUsuarioParaMap(usuario));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao cadastrar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/usuarios/login
     * Realiza login do usuário
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> dados) {
        try {
            String email = dados.get("email");
            String senha = dados.get("senha");
            Usuario usuario = usuarioService.login(email, senha);

            // ⚠️ LOG DE DEBUG - REMOVER DEPOIS
            System.out.println("===== DEBUG LOGIN =====");
            System.out.println("ID do usuário: " + usuario.getIdUsuario());
            System.out.println("Nome: " + usuario.getNome());
            System.out.println("Email: " + usuario.getEmail());
            System.out.println("Tipo: " + usuario.getTipoUsuario());
            System.out.println("=======================");

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Login realizado com sucesso!");
            response.put("usuario", converterUsuarioParaMap(usuario));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao realizar login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/usuarios
     * Lista todos os usuários
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodos() {
        try {
            List<Usuario> usuarios = usuarioService.buscarTodos();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("usuarios", usuarios);
            response.put("total", usuarios.size());

            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar usuários: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/usuarios/alunos
     * Lista apenas usuários do tipo ALUNO
     */
    @GetMapping("/alunos")
    public ResponseEntity<Map<String, Object>> listarAlunos() {
        try {
            List<Usuario> alunos = usuarioService.buscarAlunos();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("alunos", alunos);
            response.put("total", alunos.size());

            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar alunos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/usuarios/admins
     * Lista apenas usuários do tipo ADMIN
     */
    @GetMapping("/admins")
    public ResponseEntity<Map<String, Object>> listarAdmins() {
        try {
            List<Usuario> admins = usuarioService.buscarAdmins();

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("admins", admins);
            response.put("total", admins.size());

            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao listar administradores: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/usuarios/{id}
     * Busca usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable int id) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("usuario", converterUsuarioParaMap(usuario));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao buscar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/usuarios/{id}
     * Atualiza dados do usuário
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable int id,
                                                         @RequestBody Map<String, String> dados) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);

            if (dados.containsKey("nome")) {
                usuario.setNome(dados.get("nome"));
            }

            if (dados.containsKey("email")) {
                usuario.setEmail(dados.get("email"));
            }

            if (dados.containsKey("senha")) {
                usuario.setSenha(dados.get("senha"));
            }

            if (dados.containsKey("tipoUsuario")) {
                usuario.setTipoUsuario(dados.get("tipoUsuario"));
            }

            usuarioService.atualizarUsuario(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Usuário atualizado com sucesso!");
            response.put("usuario", converterUsuarioParaMap(usuario));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (SQLException e) {
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
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable int id) {
        try {
            usuarioService.deletarUsuario(id);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Usuário deletado com sucesso!");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao deletar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Método auxiliar para converter Usuario em Map
     * Garante que o idUsuario seja enviado corretamente para o frontend
     */
    private Map<String, Object> converterUsuarioParaMap(Usuario usuario) {
        Map<String, Object> map = new HashMap<>();
        map.put("idUsuario", usuario.getIdUsuario());
        map.put("nome", usuario.getNome());
        map.put("email", usuario.getEmail());
        map.put("tipoUsuario", usuario.getTipoUsuario());
        map.put("isAdmin", usuario.isAdmin());
        if (usuario.getDataCadastro() != null) {
            map.put("dataCadastro", usuario.getDataCadastro().toString());
        }
        return map;
    }
}
