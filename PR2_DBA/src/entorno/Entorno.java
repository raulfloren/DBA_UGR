package entorno;

import agente.Posicion;

public class Entorno {

    private final static int AGENTE_ID = -2, OBJETIVO_ID = -3, SUELO_ID = 0;
    private Mapa mapa;
    private Posicion posAgente; // <-- Posición REAL del agente
    private final Posicion posObjetivo;

    // Constructor entorno
    public Entorno(Mapa mapa, Posicion agente, Posicion objetivo) {
        this.mapa = mapa;
        this.posAgente = agente;
        this.posObjetivo = objetivo;
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
        mapa.ponerItemEnMapa(posAnterior, 1);
    }
}
