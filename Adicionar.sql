INSERT INTO Questao (enunciado, alternativa, resposta_correta, id_materia)
VALUES (
    'Com base nos conhecimentos sobre produção agrícola no Brasil e o uso de defensivos agrícolas, considere as afirmativas a seguir.
I. Designar os agrotóxicos como defensivos agrícolas é um artifício retórico para dissimular a natureza nociva desses produtos e sugere que os agrotóxicos supostamente protegem os cultivos sem efeitos nocivos sobre a saúde humana e o meio ambiente.
II. Há a falsa ideia de que algumas medidas preventivas eliminariam os riscos de intoxicação humana e ambiental, cuja responsabilidade é transferida para as vítimas, sob a alegação de que estas não adotam os procedimentos de segurança recomendados.
III. A aplicação de inseticidas pode levar, nas populações de insetos-praga, ao surgimento de organismos resistentes, que, com o passar do tempo, se tornam a maioria dos indivíduos da população.
IV. Os agrotóxicos inseticidas são inertes para outros insetos, como polinizadores e predadores naturais, contribuindo com o equilíbrio ecológico nas plantações e em seu entorno.
Assinale a alternativa correta.',
    
    E'A) Somente as afirmativas I e II são corretas.\nB) Somente as afirmativas I e IV são corretas.\nC) Somente as afirmativas III e IV são corretas.\nD) Somente as afirmativas I, II e III são corretas.\nE) Somente as afirmativas II, III e IV são corretas.',
    
    'D',
    
    5  -- ID
);

SELECT 
    q.id_questao,
    q.enunciado,
    q.alternativa,
    q.resposta_correta,
    m.nome AS nome_materia
FROM 
    Questao q
    INNER JOIN Materia m ON q.id_materia = m.id_materia
WHERE 
    q.enunciado LIKE '%defensivos agrícolas%'
ORDER BY 
    q.id_questao DESC
LIMIT 1;
