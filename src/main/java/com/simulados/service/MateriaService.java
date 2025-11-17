package com.simulados.service;

import com.simulados.model.Materia;
import com.simulados.repository.MateriaRepository;
import com.simulados.repository.MateriaRepositoryImpl;

import java.util.List;
import java.util.Optional;

/**
 * Service com lógica de negócio para operações relacionadas a Materia
 */
public class MateriaService {

    private final MateriaRepository materiaRepository;

    // Construtor - instancia o repository
    public MateriaService() {
        this.materiaRepository = new MateriaRepositoryImpl();
    }

    /**
     * Cadastra uma nova matéria no sistema
     * Valida se o nome já existe antes de salvar
     */
    public Materia cadastrarMateria(String nome) {
        // Validação: verifica se o nome está vazio
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da matéria não pode ser vazio!");
        }

        // Validação: verifica se já existe uma matéria com esse nome
        Optional<Materia> materiaExistente = materiaRepository.buscarPorNome(nome.trim());
        if (materiaExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma matéria com esse nome!");
        }

        // Cria e salva a matéria
        Materia novaMateria = new Materia(nome.trim());
        return materiaRepository.salvar(novaMateria);
    }

    /**
     * Busca todas as matérias cadastradas
     * Retorna ordenado por nome
     */
    public List<Materia> listarTodasMaterias() {
        return materiaRepository.buscarTodas();
    }

    /**
     * Busca uma matéria por ID
     */
    public Optional<Materia> buscarMateriaPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }
        return materiaRepository.buscarPorId(id);
    }

    /**
     * Busca uma matéria por nome
     */
    public Optional<Materia> buscarMateriaPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio!");
        }
        return materiaRepository.buscarPorNome(nome.trim());
    }

    /**
     * Atualiza o nome de uma matéria
     * Verifica se o novo nome não está sendo usado por outra matéria
     */
    public boolean atualizarMateria(Integer id, String novoNome) {
        // Verifica se a matéria existe
        Optional<Materia> materiaExistente = materiaRepository.buscarPorId(id);
        if (materiaExistente.isEmpty()) {
            throw new IllegalArgumentException("Matéria não encontrada!");
        }

        // Validação: nome não pode ser vazio
        if (novoNome == null || novoNome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da matéria não pode ser vazio!");
        }

        // Verifica se o novo nome já está em uso por outra matéria
        Optional<Materia> materiaComNome = materiaRepository.buscarPorNome(novoNome.trim());
        if (materiaComNome.isPresent() && !materiaComNome.get().getIdMateria().equals(id)) {
            throw new IllegalArgumentException("Já existe outra matéria com esse nome!");
        }

        // Atualiza a matéria
        Materia materia = materiaExistente.get();
        materia.setNome(novoNome.trim());

        return materiaRepository.atualizar(materia);
    }

    /**
     * Deleta uma matéria do sistema
     * ATENÇÃO: Só permite deletar se não houver questões vinculadas (RESTRICT)
     */
    public boolean deletarMateria(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }

        // Verifica se a matéria existe
        Optional<Materia> materia = materiaRepository.buscarPorId(id);
        if (materia.isEmpty()) {
            throw new IllegalArgumentException("Matéria não encontrada!");
        }

        try {
            return materiaRepository.deletar(id);
        } catch (Exception e) {
            // Se houver questões vinculadas, o banco dará erro por causa do RESTRICT
            throw new IllegalStateException(
                    "Não é possível deletar a matéria pois existem questões cadastradas para ela!"
            );
        }
    }

    /**
     * Conta o total de matérias cadastradas
     */
    public int contarMaterias() {
        return materiaRepository.contarTodas();
    }

    /**
     * Verifica se uma matéria existe pelo ID
     */
    public boolean materiaExiste(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return materiaRepository.buscarPorId(id).isPresent();
    }

    /**
     * Verifica se existem matérias cadastradas no sistema
     * Útil para validar antes de criar simulados
     */
    public boolean existemMaterias() {
        return contarMaterias() > 0;
    }
}

