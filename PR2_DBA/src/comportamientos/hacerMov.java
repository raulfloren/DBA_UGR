package comportamientos;

import GUI.SimulacionAgenteGUI;
import agente.Percepcion;
import agente.Posicion;
import entorno.Entorno;
import jade.core.behaviours.Behaviour;
import movimientos.Movimientos;

/**
 *
 * @author floren
 * @author juanma
 */
public class hacerMov extends Behaviour {

    private Entorno entorno;
    private SimulacionAgenteGUI visualizer;
    private Posicion posObjetivo;
    private boolean objetivoAlcanzado = false;

    public hacerMov(Entorno entorno, SimulacionAgenteGUI visualizer, Posicion posObjetivo) {
        super(null); // 'myAgent' se establece cuando se añada al agente
        this.entorno = entorno;
        this.visualizer = visualizer;
        this.posObjetivo = posObjetivo;
    }

    @Override
    public void action() {
        // 1. PERCEPCIÓN
        Percepcion p = entorno.getPercepcion();

        // 2. DECISIÓN (Comprobar si hemos llegado)
        if (p.posActual.getFila() == posObjetivo.getFila() && p.posActual.getColumna() == posObjetivo.getColumna()) {
            objetivoAlcanzado = true;
            System.out.println("🎉 ¡Objetivo alcanzado! Energía consumida: " + p.energiaConsumida);
            visualizer.mostrarVentanaVictoria();
            return;
        }

        // 3. DECISIÓN (Elegir próximo movimiento)
        Movimientos movDecidido = decidirMovimiento(p);

        // 4. ACCIÓN (Solicitar movimiento al entorno)
        boolean movido = entorno.solicitarMovimiento(movDecidido);

        // 5. ACTUALIZAR GUI (si nos movimos)
        if (movido) {
            visualizer.actualizarMatriz(entorno.getMapa().getMapa(), movDecidido);
            
            // traza en GUI
            String traza = String.format("Pos: (%d, %d)\nAcción: %s\nEnergía: %d",
                    p.posActual.getColumna(), p.posActual.getFila(), // X, Y
                    movDecidido, p.energiaConsumida + 1);
            visualizer.agregarTraza(traza);
        }
        
        // Pausar la simulación para poder verla
        block(200); // 200 ms
    }

    /**
     * 
     * logica greedy simple
     */
    private Movimientos decidirMovimiento(Percepcion p) {
        // Calcular la dirección hacia el objetivo
        int deltaFila = posObjetivo.getFila() - p.posActual.getFila();
        int deltaCol = posObjetivo.getColumna() - p.posActual.getColumna();

        // 1. Intentar moverse en Columnas (Eje X)
        if (deltaCol > 0 && p.sensorDerecha == 0) {
            return Movimientos.RIGHT;
        }
        if (deltaCol < 0 && p.sensorIzquierda == 0) {
            return Movimientos.LEFT;
        }

        // 2. Si no podemos o queremos movernos en X, moverse en Filas (Eje Y)
        if (deltaFila > 0 && p.sensorAbajo == 0) {
            return Movimientos.DOWN;
        }
        if (deltaFila < 0 && p.sensorArriba == 0) {
            return Movimientos.UP;
        }

        // 3. Si estamos bloqueados en la dirección 'greedy', probamos lo que sea
        if (p.sensorArriba == 0) return Movimientos.UP;
        if (p.sensorAbajo == 0) return Movimientos.DOWN;
        if (p.sensorIzquierda == 0) return Movimientos.LEFT;
        if (p.sensorDerecha == 0) return Movimientos.RIGHT;

        // Atascado (sin movimientos libres)
        return Movimientos.NONE;
    }

    @Override
    public boolean done() {
        return objetivoAlcanzado;
    }
}