package com.simulados.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe Singleton para gerenciar conexões com o banco de dados PostgreSQL
 * Garante que apenas uma instância da conexão exista durante toda a execução
 */
public class DatabaseConnection {

    // Instância única (Singleton)
    private static DatabaseConnection instance;

    // Objeto de conexão
    private Connection connection;

    // ⚠️ ALTERE ESTAS CONFIGURAÇÕES PARA SEU BANCO DE DADOS ⚠️
    private static final String URL = "jdbc:postgresql://localhost:5432/simulados_bd";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "123456";
    private static final String DRIVER = "org.postgresql.Driver";

    /**
     * Construtor privado para implementar o padrão Singleton
     * Impede a criação de instâncias externas
     */
    private DatabaseConnection() {
        try {
            // Carrega o driver JDBC do PostgreSQL
            Class.forName(DRIVER);

            // Estabelece a conexão
            this.connection = DriverManager.getConnection(URL, USUARIO, SENHA);
            System.out.println("✓ Conexão com PostgreSQL estabelecida com sucesso!");

        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver JDBC do PostgreSQL não encontrado!");
            System.err.println("Erro: " + e.getMessage());
            throw new RuntimeException("Driver não encontrado", e);

        } catch (SQLException e) {
            System.err.println("✗ Erro ao conectar com o banco de dados PostgreSQL!");
            System.err.println("Verifique: URL, usuário e senha");
            System.err.println("Erro: " + e.getMessage());
            throw new RuntimeException("Falha na conexão", e);
        }
    }

    /**
     * Retorna a instância única da classe (Singleton)
     * Cria uma nova instância se não existir
     *
     * @return instância de DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null || !isConnectionValid()) {
            synchronized (DatabaseConnection.class) {
                if (instance == null || !isConnectionValid()) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Cria uma NOVA conexão (não usa Singleton)
     * Útil para transações isoladas
     * IMPORTANTE: Deve ser fechada com try-with-resources
     *
     * @return nova conexão JDBC
     * @throws SQLException se houver erro na conexão
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do PostgreSQL não encontrado", e);
        }
    }

    /**
     * Retorna o objeto Connection ativo (Singleton)
     *
     * @return conexão JDBC com o banco de dados
     */
    public Connection getConnectionInstance() {
        return connection;
    }

    /**
     * Verifica se a conexão está válida e ativa
     *
     * @return true se a conexão está válida, false caso contrário
     */
    private static boolean isConnectionValid() {
        try {
            return instance != null
                    && instance.connection != null
                    && !instance.connection.isClosed()
                    && instance.connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Fecha a conexão com o banco de dados
     * Deve ser chamado ao finalizar a aplicação
     */
    public void fecharConexao() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Conexão com PostgreSQL fechada com sucesso!");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao fechar a conexão com o banco de dados!");
            System.err.println("Erro: " + e.getMessage());
        }
    }

    /**
     * Testa a conexão com o banco de dados
     *
     * @return true se a conexão está funcionando, false caso contrário
     */
    public boolean testarConexao() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(3);
        } catch (SQLException e) {
            System.err.println("✗ Falha no teste de conexão!");
            System.err.println("Erro: " + e.getMessage());
            return false;
        }
    }
}

