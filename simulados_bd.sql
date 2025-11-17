-- ==================================================================
-- SISTEMA DE SIMULADOS
-- ==================================================================

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

-- ==================================================================
-- TABELA: Materia
-- ==================================================================
CREATE TABLE Materia (
    id_materia SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    
    CONSTRAINT ck_nome_materia_not_empty CHECK (CHAR_LENGTH(nome) > 0)
);

-- ==================================================================
-- TABELA: Curso
-- ==================================================================
CREATE TABLE Curso (
    id_curso SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    nome_curso VARCHAR(150) NOT NULL,
    
    CONSTRAINT fk_curso_usuario FOREIGN KEY (id_usuario) 
        REFERENCES Usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ck_nome_curso_not_empty CHECK (CHAR_LENGTH(nome_curso) > 0)
);

-- ==================================================================
-- TABELA: Simulado
-- ==================================================================
CREATE TABLE Simulado (
    id_simulado SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    data_realizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_simulado_usuario FOREIGN KEY (id_usuario) 
        REFERENCES Usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==================================================================
-- TABELA: Questao
-- ==================================================================
CREATE TABLE Questao (
    id_questao SERIAL PRIMARY KEY,
    enunciado TEXT NOT NULL,
    alternativa TEXT NOT NULL,
    resposta_correta VARCHAR(1) NOT NULL,
    id_materia INT NOT NULL,
    
    CONSTRAINT fk_questao_materia FOREIGN KEY (id_materia) 
        REFERENCES Materia(id_materia) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_enunciado_not_empty CHECK (CHAR_LENGTH(enunciado) > 0),
    CONSTRAINT ck_resposta_correta CHECK (resposta_correta IN ('A', 'B', 'C', 'D', 'E'))
);

-- ==================================================================
-- TABELA: Resposta_usuario
-- ==================================================================
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

-- ==================================================================
-- TABELA: Desempenho
-- ==================================================================
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

-- ==================================================================
-- TABELA: Realiza (Relacao N:N entre Usuario e Simulado)
-- ==================================================================
CREATE TABLE Realiza (
    id_usuario INT NOT NULL,
    id_simulado INT NOT NULL,
    
    PRIMARY KEY (id_usuario, id_simulado),
    CONSTRAINT fk_realiza_usuario FOREIGN KEY (id_usuario) 
        REFERENCES Usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_realiza_simulado FOREIGN KEY (id_simulado) 
        REFERENCES Simulado(id_simulado) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==================================================================
-- ÍNDICES PARA PERFORMANCE
-- ==================================================================
CREATE INDEX idx_questao_materia ON Questao(id_materia);
CREATE INDEX idx_resposta_simulado ON Resposta_usuario(id_simulado);
CREATE INDEX idx_resposta_usuario ON Resposta_usuario(id_usuario);
CREATE INDEX idx_simulado_usuario ON Simulado(id_usuario);
CREATE INDEX idx_curso_usuario ON Curso(id_usuario);

-- ==================================================================
-- VIEWS ÚTEIS
-- ==================================================================

-- View para ver questões com nome da matéria
CREATE VIEW vw_questoes_completas AS
SELECT 
    q.id_questao,
    q.enunciado,
    q.alternativa,
    q.resposta_correta,
    m.nome AS nome_materia,
    m.id_materia
FROM Questao q
INNER JOIN Materia m ON q.id_materia = m.id_materia;

-- View para ver desempenho dos usuários
CREATE VIEW vw_desempenho_usuarios AS
SELECT 
    u.id_usuario,
    u.nome AS nome_usuario,
    s.id_simulado,
    s.data_realizacao,
    COUNT(r.id_resposta) AS total_questoes,
    SUM(CASE WHEN r.resposta_fornecida = q.resposta_correta THEN 1 ELSE 0 END) AS acertos,
    ROUND(
        (SUM(CASE WHEN r.resposta_fornecida = q.resposta_correta THEN 1 ELSE 0 END)::NUMERIC / 
        COUNT(r.id_resposta)::NUMERIC) * 100, 
        2
    ) AS percentual_acerto
FROM Usuario u
INNER JOIN Simulado s ON u.id_usuario = s.id_usuario
INNER JOIN Resposta_usuario r ON s.id_simulado = r.id_simulado
INNER JOIN Questao q ON r.id_questao = q.id_questao
GROUP BY u.id_usuario, u.nome, s.id_simulado, s.data_realizacao;

-- ==================================================================
-- INSERT DE DADOS / QUESTÕES DE TESTE
-- ==================================================================

-- ==================================================================
-- 1. INSERT DAS MATÉRIAS
-- ==================================================================

INSERT INTO Materia (nome) VALUES 
('Matemática'),
('Português'),
('História'),
('Geografia'),
('Ciências'),
('Inglês'),
('Física'),
('Química');

-- ==================================================================
-- 2. INSERT QUESTÕES DE MATEMÁTICA
-- ==================================================================

INSERT INTO Questao (enunciado, alternativa, resposta_correta, id_materia) VALUES
('Quanto é 15 + 27?', E'A) 40\nB) 41\nC) 42\nD) 43\nE) 44', 'C', 1),
('Quanto é 8 × 7?', E'A) 54\nB) 55\nC) 56\nD) 57\nE) 58', 'C', 1),
('Qual é a raiz quadrada de 144?', E'A) 10\nB) 11\nC) 12\nD) 13\nE) 14', 'C', 1),
('Quanto é 100 ÷ 4?', E'A) 20\nB) 22\nC) 24\nD) 25\nE) 30', 'D', 1),
('Qual o resultado de 2³ (2 elevado a 3)?', E'A) 4\nB) 6\nC) 8\nD) 9\nE) 10', 'C', 1);

-- ==================================================================
-- 3. INSERT QUESTÕES DE PORTUGUÊS
-- ==================================================================

INSERT INTO Questao (enunciado, alternativa, resposta_correta, id_materia) VALUES
('Qual é o plural de "cidadão"?', E'A) Cidadãos\nB) Cidadões\nC) Cidadães\nD) Cidadans\nE) Cidadãoes', 'A', 2),
('Qual palavra está correta?', E'A) Excessão\nB) Exceção\nC) Escessão\nD) Eceção\nE) Exeção', 'B', 2),
('Identifique o sujeito: "O menino estudou muito"', E'A) estudou\nB) muito\nC) O menino\nD) menino\nE) não há sujeito', 'C', 2),
('Qual é o sinônimo de "feliz"?', E'A) Triste\nB) Contente\nC) Bravo\nD) Nervoso\nE) Calmo', 'B', 2),
('Complete: "Se eu ____ rico, viajaria o mundo"', E'A) ser\nB) sou\nC) fosse\nD) seria\nE) fui', 'C', 2);

-- ==================================================================
-- 4. INSERT QUESTÕES DE HISTÓRIA
-- ==================================================================

INSERT INTO Questao (enunciado, alternativa, resposta_correta, id_materia) VALUES
('Quem descobriu o Brasil?', E'A) Cristóvão Colombo\nB) Pedro Álvares Cabral\nC) Vasco da Gama\nD) Fernando de Magalhães\nE) Américo Vespúcio', 'B', 3),
('Em que ano foi proclamada a Independência do Brasil?', E'A) 1800\nB) 1822\nC) 1850\nD) 1889\nE) 1900', 'B', 3),
('Quem foi o primeiro presidente do Brasil?', E'A) Getúlio Vargas\nB) Juscelino Kubitschek\nC) Deodoro da Fonseca\nD) Dom Pedro II\nE) Tancredo Neves', 'C', 3),
('A Revolução Francesa ocorreu em que ano?', E'A) 1789\nB) 1792\nC) 1800\nD) 1815\nE) 1850', 'A', 3),
('Qual civilização construiu as pirâmides?', E'A) Romana\nB) Grega\nC) Egípcia\nD) Maia\nE) Inca', 'C', 3);

-- ==================================================================
-- 5. INSERT QUESTÕES DE GEOGRAFIA
-- ==================================================================

INSERT INTO Questao (enunciado, alternativa, resposta_correta, id_materia) VALUES
('Qual é a capital do Brasil?', E'A) São Paulo\nB) Rio de Janeiro\nC) Brasília\nD) Salvador\nE) Belo Horizonte', 'C', 4),
('Qual o maior país do mundo em área territorial?', E'A) China\nB) Estados Unidos\nC) Canadá\nD) Brasil\nE) Rússia', 'E', 4),
('Quantos continentes existem na Terra?', E'A) 4\nB) 5\nC) 6\nD) 7\nE) 8', 'C', 4),
('Qual é o rio mais extenso do Brasil?', E'A) São Francisco\nB) Paraná\nC) Tietê\nD) Amazonas\nE) Araguaia', 'D', 4),
('Qual país tem formato de "bota"?', E'A) Espanha\nB) Grécia\nC) Itália\nD) Portugal\nE) França', 'C', 4);

-- ==================================================================
-- 6. INSERT QUESTÕES DE CIÊNCIAS
-- ==================================================================

INSERT INTO Questao (enunciado, alternativa, resposta_correta, id_materia) VALUES
('Qual é o planeta mais próximo do Sol?', E'A) Vênus\nB) Terra\nC) Mercúrio\nD) Marte\nE) Júpiter', 'C', 5),
('Quantos ossos tem o corpo humano adulto?', E'A) 186\nB) 196\nC) 206\nD) 216\nE) 226', 'C', 5),
('Qual gás as plantas absorvem da atmosfera?', E'A) Oxigênio\nB) Nitrogênio\nC) Hidrogênio\nD) Gás carbônico\nE) Hélio', 'D', 5),
('Qual é o maior órgão do corpo humano?', E'A) Fígado\nB) Coração\nC) Pulmão\nD) Pele\nE) Cérebro', 'D', 5),
('A água ferve a quantos graus Celsius ao nível do mar?', E'A) 90°C\nB) 95°C\nC) 100°C\nD) 105°C\nE) 110°C', 'C', 5);

-- ==================================================================
-- 7. INSERT QUESTÕES DE INGLÊS
-- ==================================================================

INSERT INTO Questao (enunciado, alternativa, resposta_correta, id_materia) VALUES
('Como se diz "cachorro" em inglês?', E'A) Cat\nB) Dog\nC) Bird\nD) Fish\nE) Mouse', 'B', 6),
('Qual é a tradução de "good morning"?', E'A) Boa tarde\nB) Boa noite\nC) Bom dia\nD) Até logo\nE) Olá', 'C', 6),
('Complete: "I ___ a student"', E'A) is\nB) am\nC) are\nD) be\nE) was', 'B', 6),
('Qual número corresponde a "twelve"?', E'A) 10\nB) 11\nC) 12\nD) 13\nE) 14', 'C', 6),
('Como se diz "vermelho" em inglês?', E'A) Blue\nB) Green\nC) Yellow\nD) Red\nE) Black', 'D', 6);

-- ==================================================================
-- 8. INSERT QUESTÕES DE FÍSICA
-- ==================================================================

INSERT INTO Questao (enunciado, alternativa, resposta_correta, id_materia) VALUES
('Qual a velocidade da luz no vácuo?', E'A) 200.000 km/s\nB) 250.000 km/s\nC) 300.000 km/s\nD) 350.000 km/s\nE) 400.000 km/s', 'C', 7),
('Qual a unidade de medida de força no SI?', E'A) Joule\nB) Watt\nC) Newton\nD) Pascal\nE) Volt', 'C', 7),
('Quem formulou a Lei da Gravitação Universal?', E'A) Einstein\nB) Galileu\nC) Newton\nD) Tesla\nE) Kepler', 'C', 7),
('A fórmula da energia cinética é:', E'A) E = mc²\nB) E = mv²/2\nC) E = mgh\nD) E = F×d\nE) E = P×t', 'B', 7),
('Qual fenômeno explica o arco-íris?', E'A) Reflexão\nB) Refração\nC) Difração\nD) Interferência\nE) Polarização', 'B', 7);

-- ==================================================================
-- 9. INSERT QUESTÕES DE QUÍMICA
-- ==================================================================

INSERT INTO Questao (enunciado, alternativa, resposta_correta, id_materia) VALUES
('Qual é o símbolo químico da água?', E'A) H2O\nB) CO2\nC) O2\nD) H2\nE) NaCl', 'A', 8),
('Qual elemento tem símbolo "Fe"?', E'A) Fósforo\nB) Flúor\nC) Ferro\nD) Frâncio\nE) Fermio', 'C', 8),
('Quantos elementos tem a Tabela Periódica?', E'A) 100\nB) 108\nC) 118\nD) 128\nE) 138', 'C', 8),
('O que é pH?', E'A) Potencial hidrogeniônico\nB) Peso hidroxila\nC) Produto hidratante\nD) Pressão hidráulica\nE) Partícula de hidrogênio', 'A', 8),
('Qual gás é mais abundante na atmosfera?', E'A) Oxigênio\nB) Nitrogênio\nC) Gás carbônico\nD) Hidrogênio\nE) Hélio', 'B', 8);

-- ==================================================================
-- VERIFICAÇÃO
-- ==================================================================

-- Conta quantas questões foram inseridas por matéria
SELECT 
    m.nome AS materia,
    COUNT(q.id_questao) AS total_questoes
FROM Materia m
LEFT JOIN Questao q ON m.id_materia = q.id_materia
GROUP BY m.nome
ORDER BY m.nome;

-- Total geral
SELECT COUNT(*) AS total_questoes FROM Questao;