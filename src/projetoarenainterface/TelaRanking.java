package projetoarenainterface;

import projetoarenaconexao.ConexaoMySQL;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

public class TelaRanking extends JFrame {

    private JTextArea areaRanking;
    private JComboBox<String> tipoRanking;
    private JList<String> listaJogos, listaTempo;
    private JComboBox<String> ordenado;
    public TelaRanking() {
        setTitle("Ranking");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        JLabel lblTitulo = new JLabel("Rankings");
        lblTitulo.setBounds(30, 20, 200, 25);
        add(lblTitulo);
        areaRanking = new JTextArea();
        areaRanking.setBounds(30, 60, 400, 350);
        areaRanking.setEditable(false);
        add(areaRanking);
        JLabel lblFiltros = new JLabel("Filtros para Ranking");
        lblFiltros.setBounds(500, 20, 200, 25);
        add(lblFiltros);
        JLabel lblTipo = new JLabel("Tipo de ranking");
        lblTipo.setBounds(500, 50, 200, 20);
        add(lblTipo);
        tipoRanking = new JComboBox<>(new String[]{"Ranking de Jogadores", "Ranking de Times"});
        tipoRanking.setBounds(500, 70, 200, 25);
        add(tipoRanking);
        JLabel lblJogo = new JLabel("Jogo");
        lblJogo.setBounds(500, 100, 200, 20);
        add(lblJogo);
        listaJogos = new JList<>(carregarJogos());
        JScrollPane scrollJogos = new JScrollPane(listaJogos);
        scrollJogos.setBounds(500, 120, 200, 80);
        add(scrollJogos);
        JLabel lblTempo = new JLabel("Período de Tempo");
        lblTempo.setBounds(500, 210, 200, 20);
        add(lblTempo);
        listaTempo = new JList<>(new String[]{
                "Todos os tempos", "Último ano", "Último mês", "Últimos 7 dias"
        });
        JScrollPane scrollTempo = new JScrollPane(listaTempo);
        scrollTempo.setBounds(500, 230, 200, 70);
        add(scrollTempo);
        JLabel lblOrdenado = new JLabel("Ordenado por");
        lblOrdenado.setBounds(500, 310, 200, 20);
        add(lblOrdenado);
        ordenado = new JComboBox<>(new String[]{
                "Maior pontuação", "Menor pontuação", "Maior classificação"
        });
        ordenado.setBounds(500, 330, 200, 25);
        add(ordenado);
        JButton btnAplicar = new JButton("Aplicar Filtros");
        btnAplicar.setBounds(500, 370, 200, 25);
        add(btnAplicar);
        JButton btnRetornar = new JButton("Retornar");
        btnRetornar.setBounds(600, 410, 100, 30);
        add(btnRetornar);
        btnAplicar.addActionListener(e -> aplicarFiltros());
        btnRetornar.addActionListener(e -> dispose());

        aplicarFiltros();
    }
    private String[] carregarJogos() {
        ArrayList<String> jogos = new ArrayList<>();
        try (Connection conn = ConexaoMySQL.conectar()) {
            String sql = "SELECT DISTINCT jogo FROM Torneio";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                jogos.add(rs.getString("jogo"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar jogos: " + ex.getMessage());
        }
        return jogos.toArray(new String[0]);
    }
    private void aplicarFiltros() {
        String tipo = tipoRanking.getSelectedItem().toString();
        String jogo = listaJogos.getSelectedValue();
        String tempo = listaTempo.getSelectedValue();
        String ordem = ordenado.getSelectedItem().toString();
        StringBuilder sql = new StringBuilder();
        if (tipo.equals("Ranking de Jogadores")) {
            sql.append("SELECT j.nome AS nome, r.pontuacao FROM Ranking r ")
               .append("JOIN Jogador j ON j.id_ranking = r.id_ranking ")
               .append("JOIN Torneio t ON r.id_torneio = t.id_torneio ");
        } else {
            sql.append("SELECT tm.nome_time AS nome, r.pontuacao FROM Ranking r ")
               .append("JOIN Time tm ON tm.id_time = r.id_time ")
               .append("JOIN Torneio t ON r.id_torneio = t.id_torneio ");
        }
        ArrayList<String> condicoes = new ArrayList<>();
        if (jogo != null) {
            condicoes.add("t.jogo = '" + jogo + "'");
        }
        if (tempo != null && !tempo.equals("Todos os tempos")) {
            String cond = switch (tempo) {
                case "Último ano" -> "t.data_inicio >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";
                case "Último mês" -> "t.data_inicio >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
                case "Últimos 7 dias" -> "t.data_inicio >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
                default -> null;
            };
            if (cond != null) condicoes.add(cond);
        }
        if (!condicoes.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", condicoes));
        }
        switch (ordem) {
            case "Maior pontuação" -> sql.append(" ORDER BY r.pontuacao DESC");
            case "Menor pontuação" -> sql.append(" ORDER BY r.pontuacao ASC");
            case "Maior classificação" -> sql.append(" ORDER BY r.classificacao ASC");
        }
        try (Connection conn = ConexaoMySQL.conectar()) {
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            ResultSet rs = stmt.executeQuery();
            StringBuilder resultado = new StringBuilder();
            int posicao = 1;
            while (rs.next()) {
                resultado.append(posicao++).append("º - ")
                         .append(rs.getString("nome"))
                         .append(" (")
                         .append(rs.getInt("pontuacao"))
                         .append(" pts)\n");
            }
            areaRanking.setText(resultado.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar ranking: " + ex.getMessage());
        }
    }
}
