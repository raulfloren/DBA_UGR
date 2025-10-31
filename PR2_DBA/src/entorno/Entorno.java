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
    private Posicion agente; // <-- Posición REAL del agente
    private Posicion objetivo;
    private int energiaConsumida;

    public Entorno(Mapa mapa, Posicion agente, Posicion objetivo) {
        this.mapa = mapa;
        this.agente = agente;
        this.objetivo = objetivo;
        this.energiaConsumida = 0;
        initEntorno();
    }

    public void initEntorno() {
        mapa.ponerItemEnMapa(agente, AGENTE_ID);
        mapa.ponerItemEnMapa(objetivo, OBJETIVO_ID);
    }
    
    public Mapa getMapa() {
        return this.mapa;
    }
    
    public Posicion getPosicionObjetivo() {
        return this.objetivo;
    }

    /**
     * El agente llama para saber qué hay a su alrededor.
     * Esta es la única información que recibe del mundo.
     */
    public Percepcion getPercepcion() {
        int s_ar = leerSensor(agente.getFila() - 1, agente.getColumna());
        int s_ab = leerSensor(agente.getFila() + 1, agente.getColumna());
        int s_iz = leerSensor(agente.getFila(), agente.getColumna() - 1);
        int s_de = leerSensor(agente.getFila(), agente.getColumna() + 1);

        return new Percepcion(agente, energiaConsumida, s_ar, s_ab, s_iz, s_de);
    }

    /**
     * Método auxiliar para leer un sensor.
     * Devuelve -1 si está fuera del mapa o si es un obstáculo.
     * Devuelve 0 si está libre.
     */
    private int leerSensor(int fila, int col) {
        // Comprobar límites del mapa
        if (fila < 0 || fila >= mapa.getFilas() || col < 0 || col >= mapa.getColumnas()) {
            return -1; // Fuera de límites es como un muro
        }
        
        // Comprobar obstáculo
        if (mapa.getMapa()[fila][col] == -1) {
            return -1; // Obstáculo
        }
        
        return 0; // Celda libre
    }

    /**
     * El agente llama a esto para INTENTAR moverse.
     * El entorno valida el movimiento y actualiza el mundo si es posible.
     */
    public boolean solicitarMovimiento(Movimientos mov) {
        if (mov == Movimientos.NONE) {
            return true; // No moverse siempre es válido
        }

        Posicion nuevaPos = calcularNuevaPosicion(agente, mov);

        // Validar si la nueva posición es transitable (dentro de límites y no es -1)
        if (mapa.esPosible(nuevaPos)) {
            // 1. Limpiar la celda antigua del agente
            mapa.ponerItemEnMapa(agente, SUELO_ID);
            
            // 2. Mover agente a la nueva celda
            mapa.ponerItemEnMapa(nuevaPos, AGENTE_ID);
            
            // 3. Actualizar la posición interna del agente en el entorno
            this.agente = nuevaPos;
            
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
            case UP -> fila--;
            case DOWN -> fila++;
            case LEFT -> col--;
            case RIGHT -> col++;
        }
        
        // Devuelve un NUEVO objeto Posicion
        return new Posicion(fila, col);
    }
}