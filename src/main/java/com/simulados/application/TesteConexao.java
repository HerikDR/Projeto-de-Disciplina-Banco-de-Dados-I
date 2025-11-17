package com.simulados.application;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TesteConexao {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║   TESTE DE CONEXÃO COM POSTGRESQL - SIMULADOS    ║");
        System.out.println("╚═══════════════════════════════════════════════════╝\n");

        try {
            // Obtém a instância da conexão
            DatabaseConnection db = DatabaseConnection.getInstance();
            Connection conn = db.getConnection();

            // Testa a conexão
            if (db.testarConexao()) {
                System.out.println("✓ Status: CONECTADO");
                System.out.println("✓ Banco de dados: PostgreSQL");
                System.out.println("✓ Catálogo: " + conn.getCatalog());
                System.out.println("✓ URL: " + conn.getMetaData().getURL());
                System.out.println("✓ Usuário: " + conn.getMetaData().getUserName());

                // Testa uma query simples para verificar se o banco está respondendo
                System.out.println("\n--- Testando Query no Banco ---");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT version();");

                if (rs.next()) {
                    System.out.println("✓ Versão do PostgreSQL: " + rs.getString(1));
                }

                // Verifica se as tabelas existem
                System.out.println("\n--- Verificando Tabelas do Sistema ---");
                String[] tabelas = {"usuario", "materia", "curso", "simulado", "questao",
                        "resposta_usuario", "desempenho", "realiza"};

                for (String tabela : tabelas) {
                    ResultSet rsTabela = conn.getMetaData().getTables(null, null, tabela, null);
                    if (rsTabela.next()) {
                        System.out.println("✓ Tabela '" + tabela + "' encontrada");
                    } else {
                        System.out.println("✗ Tabela '" + tabela + "' NÃO encontrada");
                    }
                }

                rs.close();
                stmt.close();

            } else {
                System.out.println("✗ Status: FALHA NA CONEXÃO");
            }

            // Fecha a conexão ao final
            System.out.println("\n--- Finalizando ---");
            db.fecharConexao();

        } catch (Exception e) {
            System.err.println("✗ Erro ao testar conexão: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║              FIM DO TESTE DE CONEXÃO              ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
    }
}
