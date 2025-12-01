// desempenho.js - gerenciamento da página de desempenho com Chart.js
const API_URL = 'http://localhost:8080/api';
let usuarioLogado = null;
let chartDesempenhoPorMateria = null;
let chartEvolucao = null;
let chartPizza = null;
let chartRadar = null;
let chartHeatmap = null;  // ✅ SUBSTITUÍDO: chartRanking → chartHeatmap

// inicializar ao carregar a página
window.addEventListener('DOMContentLoaded', () => {
    inicializar();
});

// função de inicialização
function inicializar() {
    // verificar se usuário está logado
    const usuarioStr = sessionStorage.getItem('usuarioLogado');
    if (!usuarioStr) {
        alert('você precisa estar logado para acessar esta página');
        window.location.href = 'index.html';
        return;
    }

    usuarioLogado = JSON.parse(usuarioStr);
    console.log('usuário logado:', usuarioLogado);

    // carregar dados de desempenho
    carregarDesempenho();
}

// função principal para carregar todos os dados
async function carregarDesempenho() {
    try {
        const idUsuario = usuarioLogado.idUsuario;

        // buscar histórico de simulados do usuário
        const historicoSimulados = await buscarHistoricoSimulados(idUsuario);

        console.log('histórico de simulados:', historicoSimulados);

        // verificar se tem dados
        if (historicoSimulados.length === 0) {
            mostrarEstadoVazio();
            return;
        }

        // calcular desempenho por matéria manualmente
        const desempenhoPorMateria = calcularDesempenhoPorMateria(historicoSimulados);

        // calcular estatísticas gerais
        const estatisticasGerais = calcularEstatisticasGerais(historicoSimulados);

        // exibir dados na página
        exibirEstatisticasGerais(estatisticasGerais);
        exibirDesempenhoPorMateriaChart(desempenhoPorMateria);
        exibirPizzaChart(estatisticasGerais);
        exibirRadarChart(desempenhoPorMateria);
        exibirHeatmapChart(historicoSimulados);  // ✅ SUBSTITUÍDO: exibirRankingChart → exibirHeatmapChart
        exibirEvolucaoChart(historicoSimulados);
        exibirHistoricoSimulados(historicoSimulados);

        // esconder loading e mostrar conteúdo
        document.getElementById('loadingContainer').style.display = 'none';
        document.getElementById('desempenhoContainer').style.display = 'block';

    } catch (error) {
        console.error('erro ao carregar desempenho:', error);
        mostrarErro('erro ao carregar dados de desempenho: ' + error.message);
    }
}

// função para buscar histórico de simulados via API
async function buscarHistoricoSimulados(idUsuario) {
    try {
        const response = await fetch(`${API_URL}/simulados/usuario/${idUsuario}`);
        const data = await response.json();

        console.log('resposta da API simulados:', data);

        if (data.sucesso && data.simulados) {
            // buscar estatísticas de cada simulado
            const simuladosComEstatisticas = [];

            for (const simulado of data.simulados) {
                try {
                    // buscar resultado/correção do simulado
                    const resultadoResponse = await fetch(`${API_URL}/respostas/corrigir/${simulado.idSimulado}`);
                    const resultadoData = await resultadoResponse.json();

                    console.log(`resultado simulado ${simulado.idSimulado}:`, resultadoData);

                    if (resultadoData.sucesso) {
                        // converter valores para números e garantir que existam
                        const acertos = parseInt(resultadoData.acertos) || 0;
                        const erros = parseInt(resultadoData.erros) || 0;
                        const totalQuestoes = parseInt(resultadoData.totalQuestoes) || 0;
                        const percentualAcerto = parseFloat(resultadoData.percentualAcerto) || 0;

                        // calcular nota (0-10) baseado no percentual
                        const nota = totalQuestoes > 0 ? (acertos / totalQuestoes) * 10 : 0;

                        simuladosComEstatisticas.push({
                            ...simulado,
                            acertos: acertos,
                            erros: erros,
                            totalQuestoes: totalQuestoes,
                            nota: nota,
                            percentualAcerto: percentualAcerto,
                            detalhes: resultadoData.detalhes || []
                        });
                    }
                } catch (err) {
                    console.error(`erro ao buscar resultado do simulado ${simulado.idSimulado}:`, err);
                }
            }

            return simuladosComEstatisticas;
        }

        return [];
    } catch (error) {
        console.error('erro ao buscar histórico de simulados:', error);
        return [];
    }
}

// função para calcular desempenho por matéria manualmente
function calcularDesempenhoPorMateria(historicoSimulados) {
    const materiaStats = {};

    // processar cada simulado
    historicoSimulados.forEach(simulado => {
        if (simulado.detalhes && Array.isArray(simulado.detalhes)) {
            simulado.detalhes.forEach(detalhe => {
                const nomeMateria = detalhe.nomeMateria || 'sem matéria';

                if (!materiaStats[nomeMateria]) {
                    materiaStats[nomeMateria] = {
                        nomeMateria,
                        totalQuestoes: 0,
                        acertos: 0,
                        erros: 0
                    };
                }

                materiaStats[nomeMateria].totalQuestoes++;
                if (detalhe.acertou) {
                    materiaStats[nomeMateria].acertos++;
                } else {
                    materiaStats[nomeMateria].erros++;
                }
            });
        }
    });

    // calcular percentuais
    const materias = Object.values(materiaStats).map(materia => ({
        ...materia,
        percentualAcerto: materia.totalQuestoes > 0
            ? ((materia.acertos / materia.totalQuestoes) * 100)
            : 0
    }));

    return materias;
}

// função para calcular estatísticas gerais
function calcularEstatisticasGerais(historicoSimulados) {
    let totalQuestoes = 0;
    let totalAcertos = 0;
    let totalErros = 0;
    let somaNotas = 0;

    historicoSimulados.forEach(simulado => {
        const questoes = parseInt(simulado.totalQuestoes) || 0;
        const acertos = parseInt(simulado.acertos) || 0;
        const erros = parseInt(simulado.erros) || 0;
        const nota = parseFloat(simulado.nota) || 0;

        totalQuestoes += questoes;
        totalAcertos += acertos;
        totalErros += erros;
        somaNotas += nota;
    });

    const mediaGeral = historicoSimulados.length > 0 ? somaNotas / historicoSimulados.length : 0;
    const taxaAcerto = totalQuestoes > 0 ? (totalAcertos / totalQuestoes) * 100 : 0;

    return {
        totalSimulados: historicoSimulados.length,
        totalQuestoes,
        totalAcertos,
        totalErros,
        mediaGeral: mediaGeral.toFixed(1),
        taxaAcerto: taxaAcerto.toFixed(1)
    };
}

// função para exibir estatísticas gerais
function exibirEstatisticasGerais(stats) {
    document.getElementById('totalSimulados').textContent = stats.totalSimulados;
    document.getElementById('mediaGeral').textContent = stats.mediaGeral;
    document.getElementById('taxaAcerto').textContent = stats.taxaAcerto + '%';
    document.getElementById('totalQuestoes').textContent = stats.totalQuestoes;
}

// função para exibir gráfico de desempenho por matéria (Chart.js)
function exibirDesempenhoPorMateriaChart(materias) {
    // destruir gráfico anterior se existir
    if (chartDesempenhoPorMateria) {
        chartDesempenhoPorMateria.destroy();
    }

    if (!materias || materias.length === 0) {
        document.getElementById('chartDesempenhoPorMateria').innerHTML =
            '<p style="text-align:center;color:#666;padding:2rem;">nenhum dado disponível</p>';
        return;
    }

    // ordenar por percentual (maior para menor)
    materias.sort((a, b) => b.percentualAcerto - a.percentualAcerto);

    const ctx = document.getElementById('chartDesempenhoPorMateria').getContext('2d');

    chartDesempenhoPorMateria = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: materias.map(m => m.nomeMateria),
            datasets: [{
                label: 'percentual de acerto (%)',
                data: materias.map(m => parseFloat(m.percentualAcerto)),
                backgroundColor: materias.map(m => {
                    const perc = parseFloat(m.percentualAcerto);
                    if (perc >= 70) return '#2ecc71';
                    if (perc >= 50) return '#95a5a6';
                    return '#e74c3c';
                }),
                borderColor: '#333',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const index = context.dataIndex;
                            const materia = materias[index];
                            return [
                                `acertos: ${materia.acertos}/${materia.totalQuestoes}`,
                                `percentual: ${materia.percentualAcerto.toFixed(1)}%`
                            ];
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        callback: function(value) {
                            return value + '%';
                        }
                    }
                }
            }
        }
    });
}

// gráfico de Pizza - Acertos vs Erros
function exibirPizzaChart(stats) {
    if (chartPizza) {
        chartPizza.destroy();
    }

    const ctx = document.getElementById('chartPizza').getContext('2d');

    chartPizza = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Acertos', 'Erros'],
            datasets: [{
                data: [stats.totalAcertos, stats.totalErros],
                backgroundColor: ['#2ecc71', '#e74c3c'],
                borderColor: '#fff',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        font: {
                            size: 13
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = stats.totalQuestoes;
                            const percent = ((value / total) * 100).toFixed(1);
                            return `${label}: ${value} questões (${percent}%)`;
                        }
                    }
                }
            }
        }
    });
}

// gráfico Radar - Habilidades por Matéria
function exibirRadarChart(materias) {
    if (chartRadar) {
        chartRadar.destroy();
    }

    if (!materias || materias.length === 0) {
        document.getElementById('chartRadar').innerHTML =
            '<p style="text-align:center;color:#666;padding:2rem;">nenhum dado disponível</p>';
        return;
    }

    const ctx = document.getElementById('chartRadar').getContext('2d');

    chartRadar = new Chart(ctx, {
        type: 'radar',
        data: {
            labels: materias.map(m => m.nomeMateria),
            datasets: [{
                label: 'desempenho (%)',
                data: materias.map(m => parseFloat(m.percentualAcerto)),
                backgroundColor: 'rgba(51, 51, 51, 0.2)',
                borderColor: '#333',
                borderWidth: 2,
                pointBackgroundColor: '#333',
                pointBorderColor: '#fff',
                pointHoverBackgroundColor: '#fff',
                pointHoverBorderColor: '#333',
                pointRadius: 4,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return `${context.parsed.r.toFixed(1)}%`;
                        }
                    }
                }
            },
            scales: {
                r: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        stepSize: 20,
                        callback: function(value) {
                            return value + '%';
                        }
                    }
                }
            }
        }
    });
}

// ✅ NOVO! Gráfico Heatmap - Simulados x Matérias
function exibirHeatmapChart(historicoSimulados) {
    if (chartHeatmap) {
        chartHeatmap.destroy();
    }

    if (!historicoSimulados || historicoSimulados.length === 0) {
        document.getElementById('chartHeatmap').innerHTML =
            '<p style="text-align:center;color:#666;padding:2rem;">nenhum dado disponível</p>';
        return;
    }

    // ordenar simulados por data
    const simuladosOrdenados = [...historicoSimulados].sort((a, b) =>
        new Date(a.dataRealizacao) - new Date(b.dataRealizacao)
    );

    // extrair todas as matérias únicas
    const materiasSet = new Set();
    simuladosOrdenados.forEach(simulado => {
        if (simulado.detalhes && Array.isArray(simulado.detalhes)) {
            simulado.detalhes.forEach(detalhe => {
                if (detalhe.nomeMateria) {
                    materiasSet.add(detalhe.nomeMateria);
                }
            });
        }
    });
    const materias = Array.from(materiasSet).sort();

    // criar matriz de dados para heatmap
    const dadosMatriz = [];

    simuladosOrdenados.forEach((simulado, indexSimulado) => {
        // calcular desempenho por matéria neste simulado
        const desempenhoPorMateria = {};

        if (simulado.detalhes && Array.isArray(simulado.detalhes)) {
            simulado.detalhes.forEach(detalhe => {
                const nomeMateria = detalhe.nomeMateria || 'sem matéria';

                if (!desempenhoPorMateria[nomeMateria]) {
                    desempenhoPorMateria[nomeMateria] = {
                        acertos: 0,
                        total: 0
                    };
                }

                desempenhoPorMateria[nomeMateria].total++;
                if (detalhe.acertou) {
                    desempenhoPorMateria[nomeMateria].acertos++;
                }
            });
        }

        // adicionar dados na matriz
        materias.forEach((materia, indexMateria) => {
            const stats = desempenhoPorMateria[materia];
            const percentual = stats ? (stats.acertos / stats.total) * 100 : null;

            if (percentual !== null) {
                dadosMatriz.push({
                    x: materia,
                    y: `sim ${indexSimulado + 1}`,
                    v: percentual
                });
            }
        });
    });

    const ctx = document.getElementById('chartHeatmap').getContext('2d');

    chartHeatmap = new Chart(ctx, {
        type: 'matrix',
        data: {
            datasets: [{
                label: 'desempenho (%)',
                data: dadosMatriz,
                backgroundColor(context) {
                    const value = context.dataset.data[context.dataIndex].v;
                    if (value >= 70) return '#2ecc71';  // verde
                    if (value >= 50) return '#f39c12';  // amarelo
                    if (value >= 30) return '#e67e22';  // laranja
                    return '#e74c3c';  // vermelho
                },
                borderColor: '#fff',
                borderWidth: 2,
                width: ({ chart }) => (chart.chartArea || {}).width / materias.length - 2,
                height: ({ chart }) => (chart.chartArea || {}).height / simuladosOrdenados.length - 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        title() {
                            return '';
                        },
                        label(context) {
                            const data = context.dataset.data[context.dataIndex];
                            return [
                                `matéria: ${data.x}`,
                                `simulado: ${data.y}`,
                                `desempenho: ${data.v.toFixed(1)}%`
                            ];
                        }
                    }
                }
            },
            scales: {
                x: {
                    type: 'category',
                    labels: materias,
                    ticks: {
                        display: true
                    },
                    grid: {
                        display: false
                    }
                },
                y: {
                    type: 'category',
                    labels: simuladosOrdenados.map((s, i) => `sim ${i + 1}`),
                    offset: true,
                    ticks: {
                        display: true
                    },
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
}

// função para exibir gráfico de evolução (Chart.js)
function exibirEvolucaoChart(historicoSimulados) {
    if (chartEvolucao) {
        chartEvolucao.destroy();
    }

    if (!historicoSimulados || historicoSimulados.length === 0) {
        document.getElementById('chartEvolucao').innerHTML =
            '<p style="text-align:center;color:#666;padding:2rem;">nenhum dado disponível</p>';
        return;
    }

    const simuladosOrdenados = [...historicoSimulados].sort((a, b) => {
        return new Date(a.dataRealizacao) - new Date(b.dataRealizacao);
    });

    const ctx = document.getElementById('chartEvolucao').getContext('2d');

    chartEvolucao = new Chart(ctx, {
        type: 'line',
        data: {
            labels: simuladosOrdenados.map((s, i) => `simulado ${i + 1}`),
            datasets: [{
                label: 'nota',
                data: simuladosOrdenados.map(s => parseFloat(s.nota) || 0),
                borderColor: '#333',
                backgroundColor: 'rgba(51, 51, 51, 0.1)',
                tension: 0.3,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 10,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

// função para exibir histórico de simulados
function exibirHistoricoSimulados(historicoSimulados) {
    const container = document.getElementById('historicoSimulados');

    if (!historicoSimulados || historicoSimulados.length === 0) {
        container.innerHTML = '<p>nenhum simulado realizado</p>';
        return;
    }

    const simuladosOrdenados = [...historicoSimulados].sort((a, b) => {
        return new Date(b.dataRealizacao) - new Date(a.dataRealizacao);
    });

    let html = `
        <table>
            <thead>
                <tr>
                    <th>simulado</th>
                    <th>data</th>
                    <th>questões</th>
                    <th>acertos</th>
                    <th>erros</th>
                    <th>nota</th>
                    <th>ações</th>
                </tr>
            </thead>
            <tbody>
    `;

    simuladosOrdenados.forEach(simulado => {
        const data = new Date(simulado.dataRealizacao).toLocaleDateString('pt-BR');
        const hora = new Date(simulado.dataRealizacao).toLocaleTimeString('pt-BR', {hour: '2-digit', minute: '2-digit'});
        const notaNum = parseFloat(simulado.nota) || 0;
        const notaClasse = notaNum >= 7 ? 'nota-alta' : notaNum >= 5 ? 'nota-media' : 'nota-baixa';

        html += `
            <tr>
                <td>simulado #${simulado.idSimulado}</td>
                <td>${data} ${hora}</td>
                <td>${simulado.totalQuestoes || 0}</td>
                <td style="color:#2ecc71;font-weight:bold">${simulado.acertos || 0}</td>
                <td style="color:#e74c3c;font-weight:bold">${simulado.erros || 0}</td>
                <td><span class="badge ${notaClasse}">${notaNum.toFixed(1)}</span></td>
                <td>
                    <button onclick="verResultado(${simulado.idSimulado})" class="btn-ver">
                        ver resultado
                    </button>
                </td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

// função para ver resultado de um simulado
function verResultado(idSimulado) {
    window.location.href = `resultado.html?idSimulado=${idSimulado}`;
}

// função para mostrar estado vazio
function mostrarEstadoVazio() {
    document.getElementById('loadingContainer').style.display = 'none';
    document.getElementById('estadoVazio').style.display = 'block';
}

// função para mostrar erro
function mostrarErro(mensagem) {
    document.getElementById('loadingContainer').innerHTML = `
        <div style="text-align:center;padding:2rem;">
            <p style="color:#e74c3c;font-size:1.2rem;">${mensagem}</p>
            <button onclick="window.location.reload()" 
                    style="margin-top:1rem;padding:0.5rem 1rem;background:#333;color:white;border:none;border-radius:4px;cursor:pointer;">
                tentar novamente
            </button>
        </div>
    `;
}

// função para voltar para home
function voltarParaHome() {
    window.location.href = 'home.html';
}

// função para novo simulado
function novoSimulado() {
    window.location.href = 'simulado.html';
}
