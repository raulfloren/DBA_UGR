package entorno;

import comportamientos.Percepcion;
import agente.Posicion;
import movimientos.Movimientos;

/**
 *
 * @author floren
 * @author juanma
 */
public class Entorno {

    private final static int AGENTE_ID = 9, OBJETIVO_ID = 8, SUELO_ID = 0;
    private Mapa mapa;
    private Posicion posAgente; // <-- Posición REAL del agente
    private Posicion posObjetivo;
    private int energiaConsumida;

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

    /**
     * El agente llama a esto para INTENTAR moverse. El entorno valida el
     * movimiento y actualiza el mundo si es posible.
     */
    public boolean solicitarMovimiento(Movimientos mov) {
        if (mov == Movimientos.NONE) {
            return true; // No moverse siempre es válido
        }

        Posicion nuevaPos = calcularNuevaPosicion(posAgente, mov);

        // Validar si la nueva posición es transitable (dentro de límites y no es -1)
        if (mapa.esPosible(nuevaPos)) {
            // 1. Limpiar la celda antigua del agente
            mapa.ponerItemEnMapa(posAgente, SUELO_ID);

            // 2. Mover agente a la nueva celda
            mapa.ponerItemEnMapa(nuevaPos, AGENTE_ID);

            // 3. Actualizar la posición interna del agente en el entorno
            this.posAgente = nuevaPos;

            // 4. Incrementar energía
            this.energiaConsumida++;

            return true; // Movimiento exitoso

        } else {
            // El agente ha chocado o intentado salirse
            // El PDF dice que el agente debería "morir" NO IMPLEMENTADO
            // Por ahora, simplemente rechazamos el movimiento
            return false; // Movimiento inválido
        }
    }

    private Posicion calcularNuevaPosicion(Posicion pos, Movimientos mov) {
        int fila = pos.getFila();
        int col = pos.getColumna();

        switch (mov) {
            case UP ->
                fila--;
            case DOWN ->
                fila++;
            case LEFT ->
                col--;
            case RIGHT ->
                col++;
        }

        // Devuelve un NUEVO objeto Posicion
        return new Posicion(fila, col);
    }

    public void setPosAgente(Posicion posAgente, Posicion posAnterior) {
        this.posAgente = posAgente;
        mapa.ponerItemEnMapa(posAgente, AGENTE_ID);
        mapa.ponerItemEnMapa(posAnterior, 0);
    }
}
