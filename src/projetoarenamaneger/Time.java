
package projetoarenamaneger;

import java.util.ArrayList;
import java.util.List;

public class Time {
    private int id;
    private String nome;
    private List<Jogador> jogadores;

    public Time(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.jogadores = new ArrayList<>();
    }

    public void adicionarJogador(Jogador jogador) {
        jogadores.add(jogador);
    }

    public String getNome() { return nome; }

}
