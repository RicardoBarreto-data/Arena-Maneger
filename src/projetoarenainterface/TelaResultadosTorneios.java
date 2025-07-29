package projetoarenainterface;

import projetoarenaconexao.ConexaoMySQL;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TelaResultadosTorneios extends JFrame {

    private JTextArea detalhesArea;
    private JTextArea partidasArea;
    private Map<JButton, Integer> mapaBotoes = new HashMap<>();

    public TelaResultadosTorneios() {
        setTitle("Resultados dos Torneios");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        JLabel titulo = new JLabel("Resultados");
        titulo.setBounds(30, 20, 150, 30);
        add(titulo);
        JLabel detalhesLabel = new JLabel("Detalhes do Torneio");
        detalhesLabel.setBounds(650, 20, 200, 25);
        add(detalhesLabel);
        detalhesArea = new JTextArea("Selecione um torneio para ver detalhes.");
        detalhesArea.setBounds(650, 50, 300, 160);
        detalhesArea.setEditable(false);
        add(detalhesArea);
        JLabel partidasLabel = new JLabel("Partidas com Resultado");
        partidasLabel.setBounds(650, 220, 200, 25);
        add(partidasLabel);
        partidasArea = new JTextArea("Clique em um torneio para ver suas partidas concluídas.");
        partidasArea.setBounds(650, 250, 300, 200);
        partidasArea.setEditable(false);
        add(partidasArea);
        JButton btnRetornar = new JButton("Retornar");
        btnRetornar.setBounds(780, 460, 100, 30);
        add(btnRetornar);
        btnRetornar.addActionListener(e -> dispose());
        carregarTorneiosComResultados();
    }

    private void carregarTorneiosComResultados() {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = """
                SELECT DISTINCT t.id_torneio, t.nome
                FROM Torneio t
                JOIN Partida p ON t.id_torneio = p.id_torneio
                WHERE p.resultado IS NOT NULL AND p.resultado <> ''
                ORDER BY t.nome
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int x = 30, y = 60;
            while (rs.next()) {
                int idTorneio = rs.getInt("id_torneio");
                String nome = rs.getString("nome");

                JPanel card = new JPanel();
                card.setLayout(null);
                card.setBounds(x, y, 250, 80);
                card.setBorder(BorderFactory.createEtchedBorder());
                JTextArea info = new JTextArea("Nome: " + nome + "\nClique em 'Ver Partidas' ➜");
                info.setBounds(5, 5, 170, 60);
                info.setEditable(false);
                card.add(info);
                JButton btn = new JButton("Ver Partidas");
                btn.setBounds(180, 25, 60, 30);
                mapaBotoes.put(btn, idTorneio);
                card.add(btn);
                btn.addActionListener((ActionEvent e) -> mostrarDetalhesETodasPartidas(idTorneio));
                add(card);
                if (x == 30) {
                    x = 300;
                } else {
                    x = 30;
                    y += 90;
               }
            }
            repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar torneios: " + ex.getMessage());
        }
    }
    private void mostrarDetalhesETodasPartidas(int idTorneio) {
        mostrarDetalhesDoTorneio(idTorneio);
        mostrarPartidasConcluidas(idTorneio);
    }
    private void mostrarDetalhesDoTorneio(int id) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = "SELECT * FROM Torneio WHERE id_torneio = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String detalhes = "Nome: " + rs.getString("nome") +
                        "\nJogo: " + rs.getString("jogo") +
                        "\nInício: " + rs.getString("data_inicio") +
                        "\nTérmino: " + rs.getString("data_termino") +
                        "\nModalidade: " + rs.getString("modalidade") +
                        "\nParticipantes: " + rs.getInt("participantes_min") + " - " + rs.getInt("participantes_max") +
                        "\nPlataforma: " + rs.getString("plataforma");
                detalhesArea.setText(detalhes);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao exibir detalhes: " + ex.getMessage());
        }
    }
    private void mostrarPartidasConcluidas(int idTorneio) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = """
                SELECT p.data, p.resultado, t1.nome_time AS time1, t2.nome_time AS time2
                FROM Partida p
                JOIN Time t1 ON p.id_time1 = t1.id_time
                JOIN Time t2 ON p.id_time2 = t2.id_time
                WHERE p.id_torneio = ? AND p.resultado IS NOT NULL AND p.resultado <> ''
                ORDER BY p.data
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idTorneio);
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                String data = rs.getString("data");
                String time1 = rs.getString("time1");
                String time2 = rs.getString("time2");
                String resultado = rs.getString("resultado");
                sb.append("🕒 ").append(data)
                        .append("\n").append(time1).append(" vs ").append(time2)
                        .append("\nResultado: ").append(resultado)
                        .append("\n--------------------------\n");
            }
            partidasArea.setText(sb.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao exibir partidas: " + ex.getMessage());
        }
    }
}
