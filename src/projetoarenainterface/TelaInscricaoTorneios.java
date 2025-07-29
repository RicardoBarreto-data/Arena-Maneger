package projetoarenainterface;

import projetoarenaconexao.ConexaoMySQL;
import projetoarenamaneger.Sessao;
import projetoarenamaneger.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TelaInscricaoTorneios extends JFrame {

    private JPanel painelTorneios;
    private JTextField campoJogo;
    private JTextField campoModalidade;
    public TelaInscricaoTorneios() {
        setTitle("Inscrição em Torneios");
        setSize(920, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        JLabel titulo = new JLabel("Todos os Torneios");
        titulo.setBounds(360, 20, 300, 30);
        add(titulo);
        painelTorneios = new JPanel(null);
        painelTorneios.setPreferredSize(new Dimension(850, 1000));
        JScrollPane scrollPane = new JScrollPane(painelTorneios);
        scrollPane.setBounds(30, 60, 680, 450);
        add(scrollPane);
        JPanel painelFiltro = new JPanel();
        painelFiltro.setLayout(null);
        painelFiltro.setBounds(730, 60, 150, 250);
        painelFiltro.setBorder(BorderFactory.createTitledBorder("Filtros"));
        add(painelFiltro);
        JLabel lblJogo = new JLabel("Jogo:");
        lblJogo.setBounds(10, 20, 130, 20);
        painelFiltro.add(lblJogo);
        campoJogo = new JTextField();
        campoJogo.setBounds(10, 40, 130, 25);
        painelFiltro.add(campoJogo);
        JLabel lblModalidade = new JLabel("Modalidade:");
        lblModalidade.setBounds(10, 75, 130, 20);
        painelFiltro.add(lblModalidade);
        campoModalidade = new JTextField();
        campoModalidade.setBounds(10, 95, 130, 25);
        painelFiltro.add(campoModalidade);
        JButton btnFiltrar = new JButton("Aplicar Filtro");
        btnFiltrar.setBounds(10, 140, 130, 30);
        painelFiltro.add(btnFiltrar);
        btnFiltrar.addActionListener(e -> carregarTorneiosComFiltro());
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBounds(10, 190, 130, 30);
        painelFiltro.add(btnVoltar);
        btnVoltar.addActionListener(e -> {
            dispose();
        });
        carregarTorneiosComFiltro();
    }

    private void carregarTorneiosComFiltro() {
        painelTorneios.removeAll();
        Usuario usuario = Sessao.getUsuario();
        if (usuario == null) return;
        int idJogador = buscarIdJogador(usuario.getId());
        if (idJogador == -1) return;

        try (Connection conn = ConexaoMySQL.conectar()) {
            String filtroJogo = campoJogo.getText().trim();
            String filtroModalidade = campoModalidade.getText().trim();
            String sql = """
                SELECT t.* FROM Torneio t
                WHERE NOT EXISTS (
                    SELECT 1 FROM Partida p
                    WHERE p.id_torneio = t.id_torneio AND p.status = 'concluida'
                )
            """;
            if (!filtroJogo.isEmpty()) sql += " AND t.jogo LIKE ?";
            if (!filtroModalidade.isEmpty()) sql += " AND t.modalidade LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            int index = 1;
            if (!filtroJogo.isEmpty()) stmt.setString(index++, "%" + filtroJogo + "%");
            if (!filtroModalidade.isEmpty()) stmt.setString(index, "%" + filtroModalidade + "%");
            ResultSet rs = stmt.executeQuery();
            int x = 10, y = 10;
            while (rs.next()) {
                int idTorneio = rs.getInt("id_torneio");
                String nome = rs.getString("nome");
                String jogo = rs.getString("jogo");
                String dataInicio = rs.getString("data_inicio");
                String modalidade = rs.getString("modalidade");
                JPanel card = new JPanel(null);
                card.setBounds(x, y, 300, 110);
                card.setBorder(BorderFactory.createTitledBorder(nome));
                JTextArea info = new JTextArea("Jogo: " + jogo +
                        "\nData: " + dataInicio +
                        "\nModalidade: " + modalidade);
                info.setBounds(10, 20, 270, 40);
                info.setEditable(false);
                card.add(info);
                JButton btnInscrever = new JButton("Inscrever");
                JButton btnCancelar = new JButton("Cancelar Inscrição");
                btnInscrever.setBounds(160, 70, 120, 25);
                btnCancelar.setBounds(20, 70, 130, 25);
                boolean inscrito = verificarInscricao(conn, idTorneio, idJogador);
                btnInscrever.setEnabled(!inscrito);
                btnCancelar.setEnabled(inscrito);
               final int finalIdTorneio = idTorneio;
                btnInscrever.addActionListener(e -> {
                    realizarInscricao(finalIdTorneio, idJogador);
                    carregarTorneiosComFiltro();
                });
                btnCancelar.addActionListener(e -> {
                    cancelarInscricao(finalIdTorneio, idJogador);
                    carregarTorneiosComFiltro();
                });
                card.add(btnInscrever);
                card.add(btnCancelar);
                painelTorneios.add(card);

                if (x == 10) {
                    x = 340;
                } else {
                    x = 10;
                    y += 130;
                }
            }
            painelTorneios.setPreferredSize(new Dimension(850, y + 150));
            painelTorneios.revalidate();
            painelTorneios.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar torneios: " + e.getMessage());
        }
    }
    private int buscarIdJogador(int idUsuario) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = "SELECT id_jogador FROM Jogador WHERE id_usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_jogador");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    private boolean verificarInscricao(Connection conn, int idTorneio, int idJogador) throws SQLException {
        String sql = "SELECT 1 FROM Inscricao WHERE id_torneio = ? AND id_jogador = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idTorneio);
        stmt.setInt(2, idJogador);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
    private void realizarInscricao(int idTorneio, int idJogador) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = "INSERT INTO Inscricao (id_torneio, id_jogador) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idTorneio);
            stmt.setInt(2, idJogador);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Inscrição realizada com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao inscrever: " + e.getMessage());
        }
    }
    private void cancelarInscricao(int idTorneio, int idJogador) {
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = "DELETE FROM Inscricao WHERE id_torneio = ? AND id_jogador = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idTorneio);
            stmt.setInt(2, idJogador);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Inscrição cancelada com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao cancelar: " + e.getMessage());
        }
    }
}