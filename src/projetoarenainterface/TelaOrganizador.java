package projetoarenainterface;

import projetoarenaconexao.ConexaoMySQL;
import projetoarenamaneger.Sessao;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TelaOrganizador extends JFrame {

    public TelaOrganizador() {
        setTitle("Painel do Organizador - eSports Arena");
        setSize(950, 600);
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
        nomeUsuario.setBounds(110, 30, 120, 20);
        menuLateral.add(nomeUsuario);
        JLabel tipoUsuario = new JLabel("Organizador");
        tipoUsuario.setBounds(110, 50, 100, 20);
        menuLateral.add(tipoUsuario);
        int y = 120;
        int altura = 40;
        JButton btnCriarTorneio = new JButton("Criar Torneio");
        btnCriarTorneio.setBounds(20, y, 200, altura);
        menuLateral.add(btnCriarTorneio);
        JButton btnGerenciarTorneios = new JButton("Gerenciar Torneios");
        btnGerenciarTorneios.setBounds(20, y += 50, 200, altura);
        menuLateral.add(btnGerenciarTorneios);
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
        JLabel visaoGeral = new JLabel("Visão Geral");
        visaoGeral.setBounds(270, 60, 150, 20);
        add(visaoGeral);
        JTextArea txtVisaoGeral = new JTextArea();
        txtVisaoGeral.setBounds(270, 85, 500, 70);
        txtVisaoGeral.setEditable(false);
        txtVisaoGeral.setLineWrap(true);
        txtVisaoGeral.setWrapStyleWord(true);
        add(txtVisaoGeral);
        JLabel statusInscricoes = new JLabel("Status de Inscrições");
        statusInscricoes.setBounds(270, 165, 200, 20);
        add(statusInscricoes);
        JTextArea txtStatus = new JTextArea();
        txtStatus.setBounds(270, 190, 200, 100);
        txtStatus.setEditable(false);
        txtStatus.setLineWrap(true);
        txtStatus.setWrapStyleWord(true);
        add(txtStatus);
        JLabel andamento = new JLabel("Torneios em andamento");
        andamento.setBounds(480, 165, 250, 20);
        add(andamento);
        JTextField[] camposTorneios = new JTextField[4];
        int tx = 480, ty = 190;
        for (int i = 0; i < 4; i++) {
            camposTorneios[i] = new JTextField();
            camposTorneios[i].setBounds(tx, ty, 160, 30);
            camposTorneios[i].setEditable(false);
            add(camposTorneios[i]);
            if (i % 2 == 1) {
                tx = 480;
                ty += 40;
            } else {
                tx += 170;
            }
        }
        btnCriarTorneio.addActionListener(e -> new TelaCriarTorneio().setVisible(true));
        btnGerenciarTorneios.addActionListener(e -> new TelaEditarTorneio().setVisible(true));
        btnRanking.addActionListener(e -> new TelaRanking().setVisible(true));
        btnResultados.addActionListener(e -> new TelaResultadosTorneios().setVisible(true));
        btnNotificacoes.addActionListener(e -> new TelaNotificacoes().setVisible(true));
        btnSair.addActionListener(e -> {
            dispose();
            new TelaLogin().setVisible(true);
        });
        preencherVisaoGeral(txtVisaoGeral);
        preencherStatusInscricoes(txtStatus);
        preencherTorneiosAndamento(camposTorneios);
    }

    private void preencherVisaoGeral(JTextArea txt) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = """
                SELECT
                  (SELECT COUNT(*) FROM Torneio WHERE id_organizador = ?) AS meusTorneios,
                  (SELECT COUNT(*) FROM Partida p
                    JOIN Torneio t ON p.id_torneio = t.id_torneio
                    WHERE t.id_organizador = ?) AS minhasPartidas,
                  (SELECT COUNT(*) FROM Time ti
                    JOIN Torneio to2 ON ti.id_torneio = to2.id_torneio
                    WHERE to2.id_organizador = ?) AS meusTimes,
                  (SELECT COUNT(*) FROM Torneio WHERE id_organizador = ? AND data_edicao IS NOT NULL) AS torneiosEditados
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            int id = Sessao.getUsuario().getId();
            stmt.setInt(1, id);
            stmt.setInt(2, id);
            stmt.setInt(3, id);
            stmt.setInt(4, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txt.setText(
                    "Torneios criados: " + rs.getInt("meusTorneios") + "\n" +
                    "Partidas criadas: " + rs.getInt("minhasPartidas") + "\n" +
                    "Times participantes: " + rs.getInt("meusTimes") + "\n" +
                    "Torneios editados: " + rs.getInt("torneiosEditados")
                );
            }
        } catch (Exception e) {
            txt.setText("Erro ao carregar dados.");
        }
    }
    private void preencherStatusInscricoes(JTextArea txt) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = """
                SELECT DISTINCT T.nome, I.data_inscricao
                FROM Inscricao I
                JOIN Torneio T ON I.id_torneio = T.id_torneio
                WHERE T.id_organizador = ?
                ORDER BY I.data_inscricao DESC
                LIMIT 3
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Sessao.getUsuario().getId());
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Inscrição em: ")
                  .append(rs.getString("nome"))
                  .append(" (")
                  .append(rs.getDate("data_inscricao"))
                  .append(")\n");
            }
            txt.setText(sb.length() > 0 ? sb.toString() : "Nenhuma inscrição aberta.");
        } catch (Exception e) {
            txt.setText("Erro ao buscar inscrições.");
        }
    }
    private void preencherTorneiosAndamento(JTextField[] campos) {
    try (Connection conn = ConexaoMySQL.conectar()) {
        String sql = """
            SELECT DISTINCT t.nome
            FROM Torneio t
            JOIN Partida p ON p.id_torneio = t.id_torneio
            WHERE t.id_organizador = ?
              AND p.status = 'em andamento'
            LIMIT 4
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, Sessao.getUsuario().getId());
        ResultSet rs = stmt.executeQuery();
        int i = 0;
        while (rs.next() && i < campos.length) {
            campos[i].setText(rs.getString("nome"));
            i++;
        }
        while (i < campos.length) {
            campos[i++].setText("");
        }
    } catch (Exception e) {
        for (JTextField campo : campos) {
            campo.setText("Erro");
        }
        e.printStackTrace();
    }
  }
}


