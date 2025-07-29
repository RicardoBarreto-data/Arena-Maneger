package projetoarenainterface;

import projetoarenaconexao.ConexaoMySQL;
import projetoarenamaneger.Sessao;
import projetoarenamaneger.Usuario;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class TelaPerfilUsuario extends JFrame {

    public TelaPerfilUsuario() {
        setTitle("Perfil do Usuário");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        JLabel fotoPerfil = new JLabel("Foto de Perfil");
        fotoPerfil.setBorder(BorderFactory.createEtchedBorder());
        fotoPerfil.setHorizontalAlignment(SwingConstants.CENTER);
        fotoPerfil.setBounds(50, 30, 150, 150);
        add(fotoPerfil);
        JLabel lblNome = new JLabel("Nome do Usuário:");
        lblNome.setBounds(250, 50, 150, 20);
        add(lblNome);
        JTextField txtNome = new JTextField();
        txtNome.setBounds(250, 70, 250, 25);
        txtNome.setEditable(false);
        add(txtNome);
        JLabel lblEmail = new JLabel("E-mail:");
        lblEmail.setBounds(250, 105, 150, 20);
        add(lblEmail);
        JTextField txtEmail = new JTextField();
        txtEmail.setBounds(250, 125, 250, 25);
        txtEmail.setEditable(false);
        add(txtEmail);
        JLabel lblHistorico = new JLabel("Histórico de Torneios");
        lblHistorico.setBounds(50, 200, 200, 20);
        add(lblHistorico);
        JTextArea areaHistorico = new JTextArea();
        areaHistorico.setBounds(50, 225, 300, 150);
        areaHistorico.setEditable(false);
        add(areaHistorico);
        JLabel lblEstatisticas = new JLabel("Estatísticas de Jogos");
        lblEstatisticas.setBounds(400, 200, 200, 20);
        add(lblEstatisticas);
        JTextArea areaEstatisticas = new JTextArea();
        areaEstatisticas.setBounds(400, 225, 300, 150);
        areaEstatisticas.setEditable(false);
        add(areaEstatisticas);
        JButton btnRetornar = new JButton("Retornar");
        btnRetornar.setBounds(600, 400, 100, 30);
        add(btnRetornar);
        btnRetornar.addActionListener(e -> dispose());
        carregarPerfil(txtNome, txtEmail, areaHistorico, areaEstatisticas);
    }

    private void carregarPerfil(JTextField txtNome, JTextField txtEmail,
                                JTextArea areaHistorico, JTextArea areaEstatisticas) {
        Usuario u = Sessao.getUsuario();
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Usuário não autenticado.");
            dispose();
            return;
        }
        txtNome.setText(u.getNome());
        txtEmail.setText(buscarEmailUsuario(u.getId()));

        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = "SELECT t.nome AS nome_torneio, t.jogo, t.data_inicio " +
                         "FROM Jogador j " +
                         "JOIN Time ti ON j.id_time = ti.id_time " +
                         "JOIN Torneio t ON ti.id_torneio = t.id_torneio " +
                         "WHERE j.id_usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, u.getId());
            ResultSet rs = stmt.executeQuery();

            StringBuilder historico = new StringBuilder();
            while (rs.next()) {
                historico.append("Torneio: ").append(rs.getString("nome_torneio"))
                         .append(" (").append(rs.getString("jogo")).append(") - Início: ")
                         .append(rs.getDate("data_inicio")).append("\n");
            }
            if (historico.length() == 0) {
                historico.append("Nenhum torneio encontrado.");
            }
            areaHistorico.setText(historico.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar perfil: " + ex.getMessage());
            ex.printStackTrace();
        }
        gerarEstatisticasAleatorias(areaEstatisticas);
    }
    private String buscarEmailUsuario(int id) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = "SELECT email FROM Usuario WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Email não encontrado";
    }

    private void gerarEstatisticasAleatorias(JTextArea areaEstatisticas) {
        Random rand = new Random();
        int vitorias = rand.nextInt(10);
        int derrotas = rand.nextInt(5);
        int participacoes = vitorias + derrotas + rand.nextInt(3);
        int melhorColocacao = 1 + rand.nextInt(4);

        areaEstatisticas.setText("Vitórias: " + vitorias +
                "\nDerrotas: " + derrotas +
                "\nParticipações: " + participacoes +
                "\nMelhor Colocação: " + melhorColocacao + "º");
    }
}