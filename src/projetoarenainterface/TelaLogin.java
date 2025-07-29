package projetoarenainterface;
import projetoarenamaneger.Sessao;
import projetoarenamaneger.Usuario;
import projetoarenaconexao.ConexaoMySQL;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TelaLogin extends JFrame {

    public TelaLogin() {
        setTitle("Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JLabel lblTitulo = new JLabel("Login");
        lblTitulo.setBounds(170, 20, 100, 30);
        add(lblTitulo);
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, 70, 100, 20);
        add(lblEmail);
        JTextField txtEmail = new JTextField();
        txtEmail.setBounds(50, 90, 300, 25);
        add(txtEmail);
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setBounds(50, 125, 100, 20);
        add(lblSenha);
        JPasswordField txtSenha = new JPasswordField();
        txtSenha.setBounds(50, 145, 300, 25);
        add(txtSenha);
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(150, 200, 100, 30);
        add(btnEntrar);
        btnEntrar.addActionListener((ActionEvent e) -> {
            String email = txtEmail.getText().trim();
            String senha = new String(txtSenha.getPassword()).trim();
            if (email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
                return;
            }
            try (Connection conn = ConexaoMySQL.conectar()) {
                String sql = "SELECT * FROM Usuario WHERE email = ? AND senha = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, email);
                stmt.setString(2, senha);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome_usuario");
                    String tipo = rs.getString("tipo");
                    Usuario u = new Usuario(id, nome, tipo);
                    Sessao.setUsuario(u);
                    JOptionPane.showMessageDialog(this, "Bem-vindo, " + nome + "!");
                    dispose();
                    switch (tipo) {
                        case "administrador":
                            new TelaAdministrador().setVisible(true);
                            break;
                        case "organizador":
                            new TelaOrganizador().setVisible(true);
                            break;
                        case "jogador":
                            new TelaJogador().setVisible(true);
                            break;
                        default:
                            JOptionPane.showMessageDialog(this, "Tipo de usuário inválido.");
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Email ou senha inválidos.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
} //completo!