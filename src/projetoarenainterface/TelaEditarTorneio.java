package projetoarenainterface;

import projetoarenaconexao.ConexaoMySQL;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class TelaEditarTorneio extends JFrame {

    private ArrayList<Integer> idsTorneios = new ArrayList<>();
    private JTextField txtNome, txtPlataforma, txtInicio, txtFim, txtModalidade, txtMin, txtMax, txtJogo;
    private JTextArea txtDescricao, txtRegras;
    private int idTorneioSelecionado = -1;
    private JPanel painelLista;

    public TelaEditarTorneio() {
        setTitle("Editar Torneio");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        JLabel titulo = new JLabel("Editar Torneio");
        titulo.setBounds(520, 20, 200, 30);
        add(titulo);
        painelLista = new JPanel();
        painelLista.setLayout(null);
        painelLista.setPreferredSize(new java.awt.Dimension(200, 1000));
        JScrollPane scrollPane = new JScrollPane(painelLista);
        scrollPane.setBounds(0, 0, 200, 500);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);
        carregarBotoesTorneios();
        txtNome = criarCampo("Nome do Torneio:", 250, 70, 250);
        txtDescricao = criarArea("Descrição:", 250, 125, 250, 70);
        txtPlataforma = criarCampo("Plataforma (opcional):", 250, 225, 250);
        txtRegras = criarArea("Regras e regulamentos:", 250, 280, 250, 70);
        txtInicio = criarCampo("Data de início (YYYY-MM-DD):", 550, 70, 180);
        txtFim = criarCampo("Data de término (YYYY-MM-DD):", 550, 125, 180);
        txtModalidade = criarCampo("Modalidade:", 550, 180, 180);
        txtMin = criarCampo("Participantes (mín):", 550, 235, 60);
        txtMax = criarCampo("      (máx):", 645, 235, 60);
        txtJogo = criarCampo("Jogo:", 550, 290, 180);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(600, 400, 110, 30);
        add(btnCancelar);
        JButton btnSalvar = new JButton("Salvar Alterações");
        btnSalvar.setBounds(720, 400, 150, 30);
        add(btnSalvar);
        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvarAlteracoes());
    }
    private void carregarBotoesTorneios() {
        painelLista.removeAll();
        idsTorneios.clear();
        JLabel lblLista = new JLabel("Lista de Torneios Cadastrados");
        lblLista.setBounds(20, 20, 170, 20);
        painelLista.add(lblLista);
        int y = 50;
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = "SELECT id_torneio, nome FROM Torneio ORDER BY id_torneio DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_torneio");
                String nome = rs.getString("nome");
                JButton btn = new JButton(nome);
                btn.setBounds(20, y, 150, 50);
                painelLista.add(btn);
                idsTorneios.add(id);
                int idSelecionado = id;
                btn.addActionListener(e -> carregarTorneio(idSelecionado));
                y += 70;
            }

            painelLista.revalidate();
            painelLista.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar torneios: " + ex.getMessage());
        }
    }
    private JTextField criarCampo(String label, int x, int y, int largura) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(x, y, 200, 20);
        add(lbl);
        JTextField campo = new JTextField();
        campo.setBounds(x, y + 20, largura, 25);
        add(campo);
        return campo;
    }

    private JTextArea criarArea(String label, int x, int y, int largura, int altura) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(x, y, 200, 20);
        add(lbl);
        JTextArea area = new JTextArea();
        area.setBounds(x, y + 20, largura, altura);
        add(area);
        return area;
    }

    private void carregarTorneio(int id) {
        idTorneioSelecionado = id;

        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = "SELECT * FROM Torneio WHERE id_torneio = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtNome.setText(rs.getString("nome"));
                txtJogo.setText(rs.getString("jogo"));
                txtInicio.setText(rs.getString("data_inicio"));
                txtFim.setText(rs.getString("data_termino"));
                txtDescricao.setText(rs.getString("descricao"));
                txtRegras.setText(rs.getString("regras"));
                txtPlataforma.setText(rs.getString("plataforma"));
                txtModalidade.setText(rs.getString("modalidade"));
                txtMin.setText(String.valueOf(rs.getInt("participantes_min")));
                txtMax.setText(String.valueOf(rs.getInt("participantes_max")));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do torneio: " + ex.getMessage());
        }
    }

    private void salvarAlteracoes() {
        if (idTorneioSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um torneio primeiro.");
            return;
        }
        try (Connection conn = ConexaoMySQL.conectar()) {
    String sql = "UPDATE Torneio SET " +
            "nome = ?, " +
            "descricao = ?, " +
            "plataforma = ?, " +
            "data_inicio = ?, " +
            "data_termino = ?, " +
            "modalidade = ?, " +
            "participantes_min = ?, " +
            "participantes_max = ?, " +
            "jogo = ?, " +
            "data_edicao = NOW() " +
            "WHERE id_torneio = ?";

    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, txtNome.getText().trim());
    stmt.setString(2, txtDescricao.getText().trim());
    stmt.setString(3, txtPlataforma.getText().trim());
    String dataInicioStr = txtInicio.getText().trim();
    String dataFimStr = txtFim.getText().trim();

    if (dataInicioStr.isEmpty() || dataFimStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Datas de início e término não podem estar vazias.");
        return;
    }
    LocalDate dataInicio = LocalDate.parse(dataInicioStr);
    LocalDate dataFim = LocalDate.parse(dataFimStr);
    stmt.setDate(4, java.sql.Date.valueOf(dataInicio));
    stmt.setDate(5, java.sql.Date.valueOf(dataFim));
    stmt.setString(6, txtModalidade.getText().trim());
    stmt.setInt(7, Integer.parseInt(txtMin.getText().trim()));
    stmt.setInt(8, Integer.parseInt(txtMax.getText().trim()));
    stmt.setString(9, txtJogo.getText().trim());
    stmt.setInt(10, idTorneioSelecionado);
    stmt.executeUpdate();
    JOptionPane.showMessageDialog(this, "Torneio atualizado com sucesso!");
    carregarBotoesTorneios();
} catch (Exception ex) {
    JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
      }
   }
}
