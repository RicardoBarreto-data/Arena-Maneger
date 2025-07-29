
package projetoarenamaneger;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Torneio {
    private int id;
    private String nome;
    private String jogo;
    private Date dataInicio;
    private List<Partida> partidas;

    public Torneio(int id, String nome, String jogo, Date dataInicio) {
        this.id = id;
        this.nome = nome;
        this.jogo = jogo;
        this.dataInicio = dataInicio;
        this.partidas = new ArrayList<>();
    }

    public void cadastrarPartida(Partida partida) {
        partidas.add(partida);
    }

    public void listarPartidas() {
        for (Partida p : partidas) {
            System.out.println("Partida: " + p.getTime1() + " vs " + p.getTime2());
        }
    }

    public String getNome() { return nome; }
    public String getJogo() { return jogo; }  
}
