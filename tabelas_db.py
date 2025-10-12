import psycopg2
from psycopg2 import sql

DB_CONFIG = {
    'host': 'localhost',
    'user': 'postgres',    
    'password': 'root123',   
    'port': '5432'
}

SCRIPT_SQL = """
-- ==================================================================
-- TABELA: Usuario
-- ==================================================================
CREATE TABLE Usuario (
    id_usuario SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT ck_email CHECK (email LIKE '%@%.%'),
    CONSTRAINT ck_senha_length CHECK (CHAR_LENGTH(senha) >= 6)
);

-- ===================================================================
-- TABELA: Materia
-- ===================================================================
CREATE TABLE Materia (
    id_materia SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    
    CONSTRAINT ck_nome_materia_not_empty CHECK (CHAR_LENGTH(nome) > 0)
);

-- ===================================================================
-- TABELA: Curso
-- ===================================================================
CREATE TABLE Curso (
    id_curso SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    nome_curso VARCHAR(150) NOT NULL,
    
    CONSTRAINT fk_curso_usuario FOREIGN KEY (id_usuario) 
        REFERENCES Usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ck_nome_curso_not_empty CHECK (CHAR_LENGTH(nome_curso) > 0)
);

-- ===================================================================
-- TABELA: Simulado
-- ===================================================================
CREATE TABLE Simulado (
    id_simulado SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    data_realizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_simulado_usuario FOREIGN KEY (id_usuario) 
        REFERENCES Usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ===================================================================
-- TABELA: Questao
-- ===================================================================
CREATE TABLE Questao (
    id_questao SERIAL PRIMARY KEY,
    enunciado TEXT NOT NULL,
    alternativa VARCHAR(255) NOT NULL,
    resposta_correta VARCHAR(1) NOT NULL,
    id_materia INT NOT NULL,
    
    CONSTRAINT fk_questao_materia FOREIGN KEY (id_materia) 
        REFERENCES Materia(id_materia) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_enunciado_not_empty CHECK (CHAR_LENGTH(enunciado) > 0),
    CONSTRAINT ck_resposta_correta CHECK (resposta_correta IN ('A', 'B', 'C', 'D', 'E'))
);

-- ===================================================================
-- TABELA: Resposta_usuario
-- ===================================================================
CREATE TABLE Resposta_usuario (
    id_resposta SERIAL PRIMARY KEY,
    id_simulado INT NOT NULL,
    id_questao INT NOT NULL,
    id_usuario INT NOT NULL,
    resposta_fornecida VARCHAR(1) NOT NULL,
    data_resposta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_resposta_simulado FOREIGN KEY (id_simulado) 
        REFERENCES Simulado(id_simulado) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_resposta_questao FOREIGN KEY (id_questao) 
        REFERENCES Questao(id_questao) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_resposta_usuario FOREIGN KEY (id_usuario) 
        REFERENCES Usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ck_resposta_fornecida CHECK (resposta_fornecida IN ('A', 'B', 'C', 'D', 'E', 'N')),
    CONSTRAINT uk_resposta_unica UNIQUE (id_simulado, id_questao, id_usuario)
);

-- ===================================================================
-- TABELA: Desempenho
-- ===================================================================
CREATE TABLE Desempenho (
    id_desempenho SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_curso INT NOT NULL,
    id_materia INT NOT NULL,
    
    CONSTRAINT fk_desempenho_usuario FOREIGN KEY (id_usuario) 
        REFERENCES Usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_desempenho_curso FOREIGN KEY (id_curso) 
        REFERENCES Curso(id_curso) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_desempenho_materia FOREIGN KEY (id_materia) 
        REFERENCES Materia(id_materia) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_desempenho_unico UNIQUE (id_usuario, id_curso, id_materia)
);

-- ===================================================================
-- TABELA: Realiza (Relacao N:N entre Usuario e Simulado)
-- ===================================================================
CREATE TABLE Realiza (
    id_usuario INT NOT NULL,
    id_simulado INT NOT NULL,
    
    PRIMARY KEY (id_usuario, id_simulado),
    CONSTRAINT fk_realiza_usuario FOREIGN KEY (id_usuario) 
        REFERENCES Usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_realiza_simulado FOREIGN KEY (id_simulado) 
        REFERENCES Simulado(id_simulado) ON DELETE CASCADE ON UPDATE CASCADE
);
"""

def conectar_servidor():
    """Conecta ao servidor PostgreSQL (sem banco de dados especifico)"""
    try:
        conn = psycopg2.connect(
            host=DB_CONFIG['host'],
            user=DB_CONFIG['user'],
            password=DB_CONFIG['password'],
            port=DB_CONFIG['port']
        )
        conn.autocommit = True
        return conn
    except Exception as e:
        print(f"Erro ao conectar ao servidor: {e}")
        return None

def criar_banco_de_dados(conn):
    """Cria o banco de dados simulados_db"""
    try:
        cursor = conn.cursor()
        cursor.execute("DROP DATABASE IF EXISTS simulados_db;")
        cursor.execute("CREATE DATABASE simulados_db;")
        cursor.close()
        print("Banco de dados 'simulados_db' criado com sucesso!")
        return True
    except Exception as e:
        print(f"Erro ao criar banco de dados: {e}")
        return False

def conectar_banco():
    """Conecta ao banco de dados simulados_db"""
    try:
        conn = psycopg2.connect(
            host=DB_CONFIG['host'],
            user=DB_CONFIG['user'],
            password=DB_CONFIG['password'],
            database='simulados_db',
            port=DB_CONFIG['port']
        )
        return conn
    except Exception as e:
        print(f"Erro ao conectar ao banco de dados: {e}")
        return None

def executar_script_sql(conn):
    """Executa o script SQL para criar tabelas e indices"""
    try:
        cursor = conn.cursor()
        comandos = SCRIPT_SQL.split(';')
        
        for comando in comandos:
            comando = comando.strip()
            if comando:
                try:
                    cursor.execute(comando)
                except psycopg2.Error as e:
                    print(f"Erro ao executar comando: {e}")
        
        conn.commit()
        cursor.close()
        print("Todas as tabelas e indices foram criados com sucesso!")
        return True
    except Exception as e:
        print(f"Erro ao executar script SQL: {e}")
        conn.rollback()
        return False

def listar_tabelas(conn):
    """Lista todas as tabelas criadas"""
    try:
        cursor = conn.cursor()
        cursor.execute("""
            SELECT table_name FROM information_schema.tables 
            WHERE table_schema = 'public'
        """)
        tabelas = cursor.fetchall()
        cursor.close()
        
        if tabelas:
            print("\nTabelas criadas:")
            for tabela in tabelas:
                print(f"   - {tabela[0]}")
        return True
    except Exception as e:
        print(f"Erro ao listar tabelas: {e}")
        return False

def main():
    print("=" * 60)
    print("CRIANDO BANCO DE DADOS - SISTEMA DE SIMULADOS")
    print("=" * 60)
    
    print("\n[1/4] Conectando ao servidor PostgreSQL...")
    conn_servidor = conectar_servidor()
    if not conn_servidor:
        return
    
    print("[2/4] Criando banco de dados...")
    if not criar_banco_de_dados(conn_servidor):
        conn_servidor.close()
        return
    conn_servidor.close()
    
    print("[3/4] Conectando ao banco de dados...")
    conn_banco = conectar_banco()
    if not conn_banco:
        return
    
    print("[4/4] Criando tabelas e indices...")
    if not executar_script_sql(conn_banco):
        conn_banco.close()
        return
    
    listar_tabelas(conn_banco)
    
    conn_banco.close()
    print("\n" + "=" * 60)
    print("Banco de dados criado com sucesso!")
    print("=" * 60)

if __name__ == "__main__":
    main()
