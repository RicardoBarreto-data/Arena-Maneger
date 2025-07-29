
package projetoarenainterface;
import javax.swing.*;

public class TelaLogout extends JFrame {

    public TelaLogout(JFrame parent) {
        setTitle("Logout");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JLabel titulo = new JLabel("Logout");
        titulo.setBounds(20, 10, 200, 30);
        add(titulo);
        JTextArea mensagem = new JTextArea(
            "Tem certeza de que deseja realmente sair do sistema?\nTodas as alterações não salvas serão perdidas.");
        mensagem.setEditable(false);
        mensagem.setBounds(20, 50, 350, 50);
        mensagem.setBackground(getBackground());
        add(mensagem);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(190, 120, 90, 30);
        add(btnCancelar);
        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setBounds(290, 120, 90, 30);
        add(btnConfirmar);
        btnCancelar.addActionListener(e -> dispose());
        btnConfirmar.addActionListener(e -> {
            System.exit(0);
        });
    }
}
