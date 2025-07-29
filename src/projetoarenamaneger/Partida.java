
package projetoarenamaneger;

public class Partida {
    private int id;
    private Time time1;
    private Time time2;
    private String resultado;

    public Partida(int id, Time time1, Time time2) {
        this.id = id;
        this.time1 = time1;
        this.time2 = time2;
        this.resultado = "Sem resultado";
    }

    public void definirResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getTime1() { return time1.getNome(); }
    public String getTime2() { return time2.getNome(); }
}
