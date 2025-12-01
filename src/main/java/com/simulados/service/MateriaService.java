package com.simulados.service;

import com.simulados.model.Materia;
import com.simulados.repository.MateriaRepository;
import com.simulados.repository.MateriaRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MateriaService {

    private final MateriaRepository materiaRepository;

    public MateriaService() throws SQLException {
        this.materiaRepository = new MateriaRepositoryImpl();
    }

    /**
     * Cadastra uma nova matéria no sistema
     */
    public Materia cadastrarMateria(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da matéria não pode ser vazio!");
        }

        Optional<Materia> materiaExistente = materiaRepository.buscarPorNome(nome.trim());
        if (materiaExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma matéria com esse nome!");
        }

        Materia novaMateria = new Materia(nome.trim());
        return materiaRepository.salvar(novaMateria);
    }

    /**
     * Busca todas as matérias cadastradas
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
     */
    public boolean atualizarMateria(Integer id, String novoNome) {
        Optional<Materia> materiaExistente = materiaRepository.buscarPorId(id);
        if (!materiaExistente.isPresent()) {
            throw new IllegalArgumentException("Matéria não encontrada!");
        }

        if (novoNome == null || novoNome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da matéria não pode ser vazio!");
        }

        Optional<Materia> materiaComNome = materiaRepository.buscarPorNome(novoNome.trim());
        if (materiaComNome.isPresent() && !materiaComNome.get().getIdMateria().equals(id)) {
            throw new IllegalArgumentException("Já existe outra matéria com esse nome!");
        }

        Materia materia = materiaExistente.get();
        materia.setNome(novoNome.trim());

        return materiaRepository.atualizar(materia);
    }

    /**
     * Deleta uma matéria do sistema
     */
    public boolean deletarMateria(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }

        Optional<Materia> materia = materiaRepository.buscarPorId(id);
        if (!materia.isPresent()) {
            throw new IllegalArgumentException("Matéria não encontrada!");
        }

        try {
            return materiaRepository.deletar(id);
        } catch (Exception e) {
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
     */
    public boolean existemMaterias() {
        return contarMaterias() > 0;
    }
}


