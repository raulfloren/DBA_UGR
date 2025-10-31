package comportamientos;

import GUI.SimulacionAgenteGUI;
import agente.Agente;
import agente.Posicion;
import entorno.Entorno;
import jade.core.behaviours.Behaviour;
import movimientos.Movimientos;

/**
 *
 * @author floren
 * @author juanma
 */
public class HacerMov extends Behaviour {

    private Entorno entorno;
    private SimulacionAgenteGUI visualizer;
    private Posicion posObjetivo;
    private boolean objetivoAlcanzado = false;
    private final Agente agente;

    public HacerMov(Agente agente) {
        this.agente = agente;
    }

    @Override
    public void action() {
        agente.hacerMov();
        /*
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
         */
    }

    @Override
    public boolean done() {
        return agente.objetivoEncontrado();
    }
}
