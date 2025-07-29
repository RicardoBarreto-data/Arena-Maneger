package projetoarenainterface;

import projetoarenaconexao.ConexaoMySQL;
import projetoarenamaneger.Sessao;
import projetoarenamaneger.Usuario;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;

public class TelaNotificacoes extends JFrame {

    private JTextArea area;

    public TelaNotificacoes() {
        setTitle("Notificações");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        JLabel titulo = new JLabel("Notificações");
        titulo.setBounds(30, 20, 200, 30);
        add(titulo);
        area = new JTextArea();
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBounds(30, 60, 720, 320);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll);
        JButton btnLerTodas = new JButton("Ler todas");
        btnLerTodas.setBounds(530, 400, 100, 30);
        add(btnLerTodas);
        JButton btnRetornar = new JButton("Retornar");
        btnRetornar.setBounds(640, 400, 100, 30);
        add(btnRetornar);
        btnRetornar.addActionListener(e -> dispose());
        btnLerTodas.addActionListener(e -> {
            area.setText("Você não possui novas notificações.");
        });
        carregarNotificacoes();
    }

    private void carregarNotificacoes() {
        StringBuilder texto = new StringBuilder();
        Usuario usuario = Sessao.getUsuario();
        LocalDate hoje = LocalDate.now();
        try (Connection conn = ConexaoMySQL.conectar()) {
            texto.append("▶ Torneios Criados:\n");
            String sqlCriados = "SELECT nome, data_inicio FROM Torneio ORDER BY data_inicio DESC";
            PreparedStatement stmtCriados = conn.prepareStatement(sqlCriados);
            ResultSet rsCriados = stmtCriados.executeQuery();
            while (rsCriados.next()) {
                texto.append("- ").append(rsCriados.getString("nome"))
                     .append(" criado para ").append(rsCriados.getDate("data_inicio")).append("\n");
            }
            texto.append("\n▶ Torneios Editados:\n");
            String sqlEditados = "SELECT nome, data_edicao FROM Torneio WHERE data_edicao IS NOT NULL ORDER BY data_edicao DESC";
            PreparedStatement stmtEdit = conn.prepareStatement(sqlEditados);
            ResultSet rsEdit = stmtEdit.executeQuery();
            while (rsEdit.next()) {
                texto.append("- ").append(rsEdit.getString("nome"))
                     .append(" editado em ").append(rsEdit.getTimestamp("data_edicao").toLocalDateTime().toLocalDate()).append("\n");
            }
            texto.append("\n▶ Torneios Iniciados:\n");
            String sqlTorneios = "SELECT nome, data_inicio FROM Torneio WHERE data_inicio <= ?";
            PreparedStatement stmtT = conn.prepareStatement(sqlTorneios);
            stmtT.setDate(1, Date.valueOf(hoje));
            ResultSet rsT = stmtT.executeQuery();
            while (rsT.next()) {
                texto.append("- ").append(rsT.getString("nome")).append(" começou em ")
                     .append(rsT.getDate("data_inicio")).append("\n");
            }
            texto.append("\n▶ Últimos Resultados:\n");
            String sqlRes = "SELECT p.resultado, t.nome AS torneio, p.data " +
                            "FROM Partida p JOIN Torneio t ON p.id_torneio = t.id_torneio " +
                            "WHERE p.status = 'concluida'";
            Statement stmtR = conn.createStatement();
            ResultSet rsR = stmtR.executeQuery(sqlRes);
            while (rsR.next()) {
                texto.append("- ").append(rsR.getString("torneio")).append(": ")
                     .append(rsR.getString("resultado"))
                     .append(" (").append(rsR.getTimestamp("data").toLocalDateTime().toLocalDate()).append(")\n");
            }
            texto.append("\n▶ Inscrições Confirmadas:\n");
            if (usuario.getTipo().equals("jogador")) {
                String sqlInscricoes = "SELECT t.nome FROM Jogador j " +
                        "JOIN Time tm ON j.id_time = tm.id_time " +
                        "JOIN Torneio t ON tm.id_torneio = t.id_torneio " +
                        "WHERE j.id_usuario = ?";
                PreparedStatement stmtI = conn.prepareStatement(sqlInscricoes);
                stmtI.setInt(1, usuario.getId());
                ResultSet rsI = stmtI.executeQuery();
                while (rsI.next()) {
                    texto.append("- Inscrição confirmada no torneio: ").append(rsI.getString("nome")).append("\n");
                }
            } else {
                texto.append("- (Você não é jogador. Sem inscrições.)\n");
            }
            texto.append("\n▶ Atualizações:\n");
            texto.append("- Nova funcionalidade: Estatísticas aprimoradas disponíveis!\n");

            area.setText(texto.toString());

        } catch (Exception ex) {
            area.setText("Erro ao carregar notificações: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
