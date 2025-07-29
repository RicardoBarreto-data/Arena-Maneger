
package projetoarenamaneger;

public class Jogador {
    private int id;
    private String nome;
    private String perfil;

    public Jogador(int id, String nome, String perfil) {
        this.id = id;
        this.nome = nome;
        this.perfil = perfil;
    }

    public String getNome() { return nome; }
}
