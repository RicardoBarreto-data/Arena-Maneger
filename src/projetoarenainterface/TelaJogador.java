package projetoarenainterface;

import projetoarenaconexao.ConexaoMySQL;
import projetoarenamaneger.Sessao;
import projetoarenamaneger.Usuario;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TelaJogador extends JFrame {
    public TelaJogador() {
        setTitle("Painel do Jogador - eSports Arena");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        JPanel menuLateral = new JPanel();
        menuLateral.setLayout(null);
        menuLateral.setBounds(0, 0, 250, 600);
        add(menuLateral);
        JLabel fotoPerfil = new JLabel("Foto");
        fotoPerfil.setBounds(20, 20, 80, 80);
        menuLateral.add(fotoPerfil);
        JLabel nomeUsuario = new JLabel(Sessao.getUsuario().getNome());
        nomeUsuario.setBounds(110, 30, 100, 20);
        menuLateral.add(nomeUsuario);
        JLabel tipoUsuario = new JLabel("Jogador");
        tipoUsuario.setBounds(110, 50, 100, 20);
        menuLateral.add(tipoUsuario);
        int y = 120;
        int altura = 40;
        JButton btnPerfil = new JButton("Perfil");
        btnPerfil.setBounds(20, y, 200, altura);
        menuLateral.add(btnPerfil);
        JButton btnInscricoes = new JButton("Minhas Inscrições");
        btnInscricoes.setBounds(20, y += 50, 200, altura);
        menuLateral.add(btnInscricoes);
        JButton btnRanking = new JButton("Ranking");
        btnRanking.setBounds(20, y += 50, 200, altura);
        menuLateral.add(btnRanking);
        JButton btnResultados = new JButton("Resultados");
        btnResultados.setBounds(20, y += 50, 200, altura);
        menuLateral.add(btnResultados);
        JButton btnNotificacoes = new JButton("Notificações");
        btnNotificacoes.setBounds(20, y += 50, 200, altura);
        menuLateral.add(btnNotificacoes);
        JButton btnSair = new JButton("Sair");
        btnSair.setBounds(20, y += 50, 200, altura);
        menuLateral.add(btnSair);
        JLabel tituloDashboard = new JLabel("Dashboard");
        tituloDashboard.setBounds(270, 20, 200, 30);
        add(tituloDashboard);
        JLabel lblProximos = new JLabel("Próximos Torneios");
        lblProximos.setBounds(270, 60, 200, 20);
        add(lblProximos);
        JTextArea areaProximos = new JTextArea();
        areaProximos.setBounds(270, 85, 200, 100);
        areaProximos.setEditable(false);
        add(areaProximos);
        JLabel lblEstatisticas = new JLabel("Estatísticas Pessoais");
        lblEstatisticas.setBounds(490, 60, 200, 20);
        add(lblEstatisticas);
        JTextArea areaEstatisticas = new JTextArea();
        areaEstatisticas.setBounds(490, 85, 300, 100);
        areaEstatisticas.setEditable(false);
        add(areaEstatisticas);
        JLabel lblStatus = new JLabel("Status de Torneios");
        lblStatus.setBounds(270, 200, 200, 20);
        add(lblStatus);
        JTextArea areaStatus = new JTextArea();
        areaStatus.setBounds(270, 225, 520, 150);
        areaStatus.setEditable(false);
        add(areaStatus);
        btnPerfil.addActionListener(e -> new TelaPerfilUsuario().setVisible(true));
        btnInscricoes.addActionListener(e -> new TelaInscricaoTorneios().setVisible(true));
        btnRanking.addActionListener(e -> new TelaRanking().setVisible(true));
        btnResultados.addActionListener(e -> new TelaResultadosTorneios().setVisible(true));
        btnNotificacoes.addActionListener(e -> new TelaNotificacoes().setVisible(true));
        btnSair.addActionListener(e -> {
            dispose();
            new TelaLogin().setVisible(true);
        });
        preencherProximosTorneios(areaProximos);
        preencherEstatisticas(areaEstatisticas);
        preencherStatusTorneios(areaStatus);
    }
    private void preencherProximosTorneios(JTextArea area) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = """
                SELECT t.nome, t.data_inicio
                FROM Torneio t
                JOIN Time ti ON ti.id_torneio = t.id_torneio
                JOIN Jogador j ON j.id_time = ti.id_time
                WHERE j.id_usuario = ? AND t.data_inicio >= CURDATE()
                ORDER BY t.data_inicio ASC
                LIMIT 5
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Sessao.getUsuario().getId());

            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString("nome"))
                  .append(" (")
                  .append(rs.getDate("data_inicio"))
                  .append(")\n");
            }
            area.setText(sb.toString().isEmpty() ? "Nenhum torneio futuro." : sb.toString());
        } catch (Exception e) {
            area.setText("Erro ao carregar dados.");
        }
    }
    private void preencherEstatisticas(JTextArea area) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = """
                SELECT
                  COUNT(CASE WHEN p.resultado LIKE CONCAT('%', j.nome, '%') THEN 1 END) AS vitorias,
                  COUNT(CASE WHEN p.status = 'concluida' THEN 1 END) AS partidas,
                  MIN(r.classificacao) AS melhor_colocacao
                FROM Jogador j
                JOIN Participacao_partida pp ON j.id_jogador = pp.id_jogador
                JOIN Partida p ON p.id_partida = pp.id_partida
                LEFT JOIN Ranking r ON j.id_ranking = r.id_ranking
                WHERE j.id_usuario = ?
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Sessao.getUsuario().getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int vitorias = rs.getInt("vitorias");
                int partidas = rs.getInt("partidas");
                int derrotas = partidas - vitorias;
                int colocacao = rs.getInt("melhor_colocacao");
                area.setText(
                    "Partidas jogadas: " + partidas + "\n" +
                    "Vitórias: " + vitorias + "\n" +
                    "Derrotas: " + derrotas + "\n" +
                    "Melhor colocação: " + (colocacao == 0 ? "N/A" : colocacao + "º lugar")
                );
            }
        } catch (Exception e) {
            area.setText("Erro ao buscar estatísticas.");
        }
    }
    private void preencherStatusTorneios(JTextArea area) {
    try (Connection conn = ConexaoMySQL.conectar()) {
        String sql = """
            SELECT t.nome AS torneio,
                   (SELECT MIN(p.data)
                    FROM Partida p
                    WHERE p.id_torneio = t.id_torneio AND p.status = 'agendada') AS proxima_partida
            FROM Torneio t
            JOIN Inscricao i ON i.id_torneio = t.id_torneio
            WHERE i.id_jogador = (
                SELECT j.id_jogador FROM Jogador j WHERE j.id_usuario = ?
            )
            GROUP BY t.nome, t.id_torneio
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, Sessao.getUsuario().getId());
        ResultSet rs = stmt.executeQuery();
        StringBuilder sb = new StringBuilder();
        int total = 0;
        while (rs.next()) {
            total++;
            String nomeTorneio = rs.getString("torneio");
            String proxima = rs.getString("proxima_partida");
            sb.append("- ").append(nomeTorneio).append("\n");
            sb.append("  Próxima partida: ").append(proxima == null ? "Nenhuma agendada" : proxima).append("\n\n");
        }
        if (total == 0) {
            area.setText("Você não está participando de nenhum torneio.");
        } else {
            area.setText("Você está participando de " + total + " torneio(s):\n\n" + sb.toString());
        }
    } catch (Exception e) {
        area.setText("Erro ao verificar status dos torneios.");
        e.printStackTrace();
    }
  }
}