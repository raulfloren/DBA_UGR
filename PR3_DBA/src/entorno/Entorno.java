package entorno;

import herramientas.Posicion;
import movimientos.Movimientos;
import static movimientos.Movimientos.*;

public class Entorno {

    private final static int AGENTE_ID = -2, OBJETIVO_ID = -3, SUELO_ID = 0, CAMINO_ID = -4;
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
        mapa.ponerItemEnMapa(posAnterior, CAMINO_ID);
    }

    public void mueveAgente(Movimientos mov) {
        switch (mov) {
            case UP ->
                posAgente.setFila(posAgente.getFila() - 1);  // Arriba

            case DOWN ->
                posAgente.setFila(posAgente.getFila() + 1);  // Abajo

            case LEFT ->
                posAgente.setColumna(posAgente.getColumna() - 1);  // Izquierda

            case RIGHT ->
                posAgente.setColumna(posAgente.getColumna() + 1);  // Derecha

        }
    }
}
