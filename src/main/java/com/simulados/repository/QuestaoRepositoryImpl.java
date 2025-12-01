package com.simulados.repository;

import com.simulados.application.DatabaseConnection;
import com.simulados.model.Questao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação Repository para operações CRUD da entidade Questao usando JDBC puro
 */
public class QuestaoRepositoryImpl implements QuestaoRepository {

    private final Connection connection;

    // Construtor - obtém conexão do DatabaseConnection
    public QuestaoRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Questao salvar(Questao questao) {
        String sql = "INSERT INTO questao (enunciado, alternativa, resposta_correta, id_materia) " +
                "VALUES (?, ?, ?, ?) RETURNING id_questao";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, questao.getEnunciado());
            stmt.setString(2, questao.getAlternativa());
            stmt.setString(3, questao.getRespostaCorreta());
            stmt.setInt(4, questao.getIdMateria());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                questao.setIdQuestao(rs.getInt("id_questao"));
                return questao;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar questão: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Questao> buscarTodas() {
        List<Questao> questoes = new ArrayList<>();
        String sql = "SELECT * FROM questao ORDER BY id_questao";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                questoes.add(mapearQuestao(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todas as questões: " + e.getMessage());
            e.printStackTrace();
        }

        return questoes;
    }

    @Override
    public Optional<Questao> buscarPorId(Integer id) {
        String sql = "SELECT * FROM questao WHERE id_questao = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearQuestao(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar questão por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Questao> buscarPorMateria(Integer idMateria) {
        List<Questao> questoes = new ArrayList<>();
        String sql = "SELECT * FROM questao WHERE id_materia = ? ORDER BY id_questao";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMateria);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questoes.add(mapearQuestao(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar questões por matéria: " + e.getMessage());
            e.printStackTrace();
        }

        return questoes;
    }

    @Override
    public List<Questao> buscarQuestoesAleatoriasPorMateria(Integer idMateria, int quantidade) {
        List<Questao> questoes = new ArrayList<>();
        // ORDER BY RANDOM() randomiza as questões no PostgreSQL
        String sql = "SELECT * FROM questao WHERE id_materia = ? ORDER BY RANDOM() LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMateria);
            stmt.setInt(2, quantidade);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questoes.add(mapearQuestao(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar questões aleatórias por matéria: " + e.getMessage());
            e.printStackTrace();
        }

        return questoes;
    }

    @Override
    public List<Questao> buscarQuestoesAleatorias(int quantidade) {
        List<Questao> questoes = new ArrayList<>();
        String sql = "SELECT * FROM questao ORDER BY RANDOM() LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantidade);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questoes.add(mapearQuestao(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar questões aleatórias: " + e.getMessage());
            e.printStackTrace();
        }

        return questoes;
    }

    @Override
    public boolean atualizar(Questao questao) {
        String sql = "UPDATE questao SET enunciado = ?, alternativa = ?, resposta_correta = ?, " +
                "id_materia = ? WHERE id_questao = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, questao.getEnunciado());
            stmt.setString(2, questao.getAlternativa());
            stmt.setString(3, questao.getRespostaCorreta());
            stmt.setInt(4, questao.getIdMateria());
            stmt.setInt(5, questao.getIdQuestao());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar questão: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM questao WHERE id_questao = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar questão: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int contarTodas() {
        String sql = "SELECT COUNT(*) as total FROM questao";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar questões: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int contarPorMateria(Integer idMateria) {
        String sql = "SELECT COUNT(*) as total FROM questao WHERE id_materia = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMateria);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar questões por matéria: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Método auxiliar para mapear ResultSet em objeto Questao
    private Questao mapearQuestao(ResultSet rs) throws SQLException {
        Questao questao = new Questao();
        questao.setIdQuestao(rs.getInt("id_questao"));
        questao.setEnunciado(rs.getString("enunciado"));
        questao.setAlternativa(rs.getString("alternativa"));
        questao.setRespostaCorreta(rs.getString("resposta_correta"));
        questao.setIdMateria(rs.getInt("id_materia"));
        return questao;
    }
}

