package entorno;

import agente.Posicion;
import movimientos.Movimientos;

public class Entorno {

    private final static int AGENTE_ID = 9, OBJETIVO_ID = 8, SUELO_ID = 0;
    private Mapa mapa;
    private Posicion posAgente; // <-- Posición REAL del agente
    private Posicion posObjetivo;
    private int energiaConsumida;

    // Constructor entorno
    public Entorno(Mapa mapa, Posicion agente, Posicion objetivo) {
        this.mapa = mapa;
        this.posAgente = agente;
        this.posObjetivo = objetivo;
        this.energiaConsumida = 0;
        initEntorno();
    }

    public void initEntorno() {
        mapa.ponerItemEnMapa(posAgente, AGENTE_ID);
        mapa.ponerItemEnMapa(posObjetivo, OBJETIVO_ID);
    }

    public Mapa getMapa() {
        return this.mapa;
    }

    public Posicion getPosicionAgente() {
        return this.posAgente;
    }

    public Posicion getPosicionObjetivo() {
        return this.posObjetivo;
    }

    public void setPosAgente(Posicion posAgente, Posicion posAnterior) {
        this.posAgente = posAgente;
        mapa.ponerItemEnMapa(posAgente, AGENTE_ID);
        mapa.ponerItemEnMapa(posAnterior, 0);
    }
}
