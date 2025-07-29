package projetoarenainterface;

import projetoarenaconexao.ConexaoMySQL;
import projetoarenamaneger.Sessao;
import projetoarenamaneger.Usuario;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TelaCriarTorneio extends JFrame {

    public TelaCriarTorneio() {
        setTitle("Criar Torneio");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        JLabel titulo = new JLabel("Criar Torneio");
        titulo.setBounds(330, 20, 200, 30);
        add(titulo);
        JLabel lblNome = new JLabel("Nome do Torneio:");
        lblNome.setBounds(50, 70, 150, 20);
        add(lblNome);
        JTextField txtNome = new JTextField();
        txtNome.setBounds(50, 90, 250, 25);
        add(txtNome);
        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setBounds(50, 125, 150, 20);
        add(lblDescricao);
        JTextArea txtDescricao = new JTextArea();
        txtDescricao.setBounds(50, 145, 250, 70);
        add(txtDescricao);
        JLabel lblPlataforma = new JLabel("Plataforma (opcional):");
        lblPlataforma.setBounds(50, 225, 200, 20);
        add(lblPlataforma);
        JTextField txtPlataforma = new JTextField();
        txtPlataforma.setBounds(50, 245, 250, 25);
        add(txtPlataforma);
        JLabel lblRegras = new JLabel("Regras e regulamentos:");
        lblRegras.setBounds(50, 280, 200, 20);
        add(lblRegras);
        JTextArea txtRegras = new JTextArea();
        txtRegras.setBounds(50, 300, 250, 70);
        add(txtRegras);
        JLabel lblInicio = new JLabel("Data de início (YYYY-MM-DD):");
        lblInicio.setBounds(350, 70, 200, 20);
        add(lblInicio);
        JTextField txtInicio = new JTextField();
        txtInicio.setBounds(350, 90, 180, 25);
        add(txtInicio);
        JLabel lblFim = new JLabel("Data de término (YYYY-MM-DD):");
        lblFim.setBounds(350, 125, 200, 20);
        add(lblFim);
        JTextField txtFim = new JTextField();
        txtFim.setBounds(350, 145, 180, 25);
        add(txtFim);
        JLabel lblModalidade = new JLabel("Modalidade:");
        lblModalidade.setBounds(350, 180, 150, 20);
        add(lblModalidade);
        JTextField txtModalidade = new JTextField();
        txtModalidade.setBounds(350, 200, 180, 25);
        add(txtModalidade);
        JLabel lblParticipantes = new JLabel("Número de participantes (mínimo e máximo):");
        lblParticipantes.setBounds(350, 235, 300, 20);
        add(lblParticipantes);
        JTextField txtMin = new JTextField();
        txtMin.setBounds(350, 255, 85, 25);
        add(txtMin);
        JTextField txtMax = new JTextField();
        txtMax.setBounds(445, 255, 85, 25);
        add(txtMax);
        JLabel lblJogo = new JLabel("Jogo:");
        lblJogo.setBounds(350, 290, 150, 20);
        add(lblJogo);
        JTextField txtJogo = new JTextField();
        txtJogo.setBounds(350, 310, 180, 25);
        add(txtJogo);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(500, 400, 100, 30);
        add(btnCancelar);
        JButton btnCriar = new JButton("Criar");
        btnCriar.setBounds(620, 400, 100, 30);
        add(btnCriar);
        btnCancelar.addActionListener(e -> dispose());
        btnCriar.addActionListener(e -> {
            String nome = txtNome.getText();
            String descricao = txtDescricao.getText();
            String plataforma = txtPlataforma.getText();
            String regras = txtRegras.getText();
            String dataInicio = txtInicio.getText();
            String dataFim = txtFim.getText();
            String modalidade = txtModalidade.getText();
            String jogo = txtJogo.getText();
            String min = txtMin.getText();
            String max = txtMax.getText();

            if (nome.isEmpty() || dataInicio.isEmpty() || regras.isEmpty() || jogo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha os campos obrigatórios.");
                return;
            }
            try (Connection conn = ConexaoMySQL.conectar()) {
                Usuario usuario = Sessao.getUsuario();
                if (usuario == null || !(usuario.getTipo().equals("organizador") || usuario.getTipo().equals("administrador"))) {
                    JOptionPane.showMessageDialog(this, "Apenas organizadores ou administradores podem criar torneios.");
                    return;
                }
                String sql = "INSERT INTO Torneio (nome, descricao, jogo, data_inicio, data_termino, modalidade, participantes_min, participantes_max, plataforma, regras, id_organizador) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nome);
                stmt.setString(2, descricao);
                stmt.setString(3, jogo);
                stmt.setDate(4, java.sql.Date.valueOf(dataInicio));
                stmt.setDate(5, dataFim.isEmpty() ? null : java.sql.Date.valueOf(dataFim));
                stmt.setString(6, modalidade);
                stmt.setInt(7, min.isEmpty() ? 0 : Integer.parseInt(min));
                stmt.setInt(8, max.isEmpty() ? 0 : Integer.parseInt(max));
                stmt.setString(9, plataforma.isEmpty() ? null : plataforma);
                stmt.setString(10, regras);
                stmt.setInt(11, usuario.getId());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Torneio cadastrado com sucesso!");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}
