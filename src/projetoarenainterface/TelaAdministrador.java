package projetoarenainterface;

import projetoarenaconexao.ConexaoMySQL;
import projetoarenamaneger.Sessao;
import projetoarenamaneger.Usuario;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TelaAdministrador extends JFrame {

    public TelaAdministrador() {
        setTitle("Painel do Administrador - eSports Arena");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        JPanel menuLateral = new JPanel();
        menuLateral.setLayout(null);
        menuLateral.setBounds(0, 0, 250, 650);
        add(menuLateral);
        JLabel fotoPerfil = new JLabel("Foto");
        fotoPerfil.setBounds(20, 20, 80, 80);
        menuLateral.add(fotoPerfil);
        JLabel nomeUsuario = new JLabel("Nome");
        nomeUsuario.setBounds(110, 30, 120, 20);
        menuLateral.add(nomeUsuario);
        JLabel tipoUsuario = new JLabel("Administrador");
        tipoUsuario.setBounds(110, 50, 120, 20);
        menuLateral.add(tipoUsuario);
        Usuario usuario = Sessao.getUsuario();
        nomeUsuario.setText(usuario.getNome());
        int y = 120;
        int altura = 40;
        JButton btnCriarTorneio = new JButton("Criar Torneio");
        btnCriarTorneio.setBounds(20, y, 200, altura);
        menuLateral.add(btnCriarTorneio);
        JButton btnGerenciarJogadores = new JButton("Gerenciar Jogadores");
        btnGerenciarJogadores.setBounds(20, y += 50, 200, altura);
        menuLateral.add(btnGerenciarJogadores);
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
        JLabel atualizacoes = new JLabel("Atualizações");
        atualizacoes.setBounds(270, 60, 150, 20);
        add(atualizacoes);
        JTextArea box1 = new JTextArea();
        box1.setBounds(270, 90, 150, 60);
        box1.setEditable(false);
        box1.setLineWrap(true);
        box1.setWrapStyleWord(true);
        add(box1);
        JTextArea box2 = new JTextArea();
        box2.setBounds(430, 90, 150, 60);
        box2.setEditable(false);
        box2.setLineWrap(true);
        box2.setWrapStyleWord(true);
        add(box2);
        JTextArea box3 = new JTextArea();
        box3.setBounds(590, 90, 150, 60);
        box3.setEditable(false);
        box3.setLineWrap(true);
        box3.setWrapStyleWord(true);
        add(box3);
        JLabel ultimosTorneios = new JLabel("Últimos torneios criados");
        ultimosTorneios.setBounds(270, 160, 200, 20);
        add(ultimosTorneios);
        JTextArea box4 = new JTextArea();
        box4.setBounds(270, 190, 300, 60);
        box4.setEditable(false);
        box4.setLineWrap(true);
        box4.setWrapStyleWord(true);
        add(box4);
        JTextArea box5 = new JTextArea();
        box5.setBounds(580, 190, 300, 60);
        box5.setEditable(false);
        box5.setLineWrap(true);
        box5.setWrapStyleWord(true);
        add(box5);
        JLabel estatisticas = new JLabel("Estatísticas gerais");
        estatisticas.setBounds(270, 270, 200, 20);
        add(estatisticas);
        JTextArea box6 = new JTextArea();
        box6.setBounds(270, 300, 610, 200);
        box6.setEditable(false);
        box6.setLineWrap(true);
        box6.setWrapStyleWord(true);
        add(box6);
        btnCriarTorneio.addActionListener(e -> new TelaCriarTorneio().setVisible(true));
        btnGerenciarJogadores.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidade em desenvolvimento."));
        btnGerenciarTorneios.addActionListener(e -> new TelaEditarTorneio().setVisible(true));
        btnRanking.addActionListener(e -> new TelaRanking().setVisible(true));
        btnResultados.addActionListener(e -> new TelaResultadosTorneios().setVisible(true));
        btnNotificacoes.addActionListener(e -> new TelaNotificacoes().setVisible(true));
        btnSair.addActionListener(e -> {
            dispose();
            new TelaLogin().setVisible(true);
        });

        preencherAtualizacoes(box1, box2, box3);
        preencherUltimosTorneios(box4, box5);
        preencherEstatisticas(box6);
    }

    private void preencherAtualizacoes(JTextArea b1, JTextArea b2, JTextArea b3) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            JTextArea[] caixas = {b1, b2, b3};
            int i = 0;
            String sqlInscricao = """
                SELECT J.nome AS jogador, I.data_inscricao
                FROM Inscricao I
                JOIN Jogador J ON I.id_jogador = J.id_jogador
                ORDER BY I.data_inscricao DESC
                LIMIT 2
            """;
            PreparedStatement stmt1 = conn.prepareStatement(sqlInscricao);
            ResultSet rs1 = stmt1.executeQuery();

            while (rs1.next() && i < caixas.length) {
                caixas[i].setText(
                    "Jogador inscrito: " + rs1.getString("jogador") + "\n" +
                    "Data: " + rs1.getString("data_inscricao")
                );
                i++;
            }
            String sqlEdicao = """
                SELECT nome, data_edicao
                FROM Torneio
                WHERE data_edicao IS NOT NULL
                ORDER BY data_edicao DESC
                LIMIT 1
            """;
            PreparedStatement stmt2 = conn.prepareStatement(sqlEdicao);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next() && i < caixas.length) {
                caixas[i].setText(
                    "Torneio editado: " + rs2.getString("nome") + "\n" +
                    "Data: " + rs2.getString("data_edicao")
                );
            }
        } catch (Exception e) {
            b1.setText("Erro");
            b2.setText("ao");
            b3.setText("carregar.");
            e.printStackTrace();
        }
    }
    private void preencherUltimosTorneios(JTextArea b4, JTextArea b5) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = "SELECT nome, jogo, data_inicio FROM Torneio ORDER BY data_inicio DESC LIMIT 2";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            JTextArea[] caixas = {b4, b5};
            int i = 0;
            while (rs.next() && i < caixas.length) {
                caixas[i].setText(
                    "Nome: " + rs.getString("nome") + "\n" +
                    "Jogo: " + rs.getString("jogo") + "\n" +
                    "Data: " + rs.getString("data_inicio")
                );
                i++;
            }
        } catch (Exception e) {
            b4.setText("Erro");
            b5.setText("ao carregar.");
        }
    }
    private void preencherEstatisticas(JTextArea box6) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = """
                SELECT
                    (SELECT COUNT(*) FROM Usuario) AS totalUsuarios,
                    (SELECT COUNT(*) FROM Torneio) AS totalTorneios,
                    (SELECT COUNT(*) FROM Partida) AS totalPartidas,
                    (SELECT COUNT(*) FROM Time) AS totalTimes,
                    (SELECT COUNT(*) FROM Jogador) AS totalJogadores
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                box6.setText(
                    "Total de usuários: " + rs.getInt("totalUsuarios") + "\n" +
                    "Total de torneios: " + rs.getInt("totalTorneios") + "\n" +
                    "Total de partidas: " + rs.getInt("totalPartidas") + "\n" +
                    "Total de times: " + rs.getInt("totalTimes") + "\n" +
                    "Total de jogadores: " + rs.getInt("totalJogadores")
                );
            }
        } catch (Exception e) {
            box6.setText("Erro ao carregar estatísticas.");
        }
    }
}