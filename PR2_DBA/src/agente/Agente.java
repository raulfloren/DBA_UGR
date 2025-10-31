package agente;

import GUI.SimulacionAgenteGUI;
import comportamientos.hacerMov;
import entorno.Entorno;
import jade.core.Agent;

/**
 *
 * @author floren
 * @author juanma
 */
public class Agente extends Agent {
    
    // almacenamiento para referencias al entorno, la GUI y objetivo
    private Entorno entorno;
    private SimulacionAgenteGUI visualizer;
    private Posicion posObjetivo;

    @Override
    protected void setup() {
        System.out.println("Soy agente: " + getAID().getLocalName());

        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            entorno = (Entorno) args[0];
            visualizer = (SimulacionAgenteGUI) args[1];
            posObjetivo = entorno.getPosicionObjetivo();
            
            addBehaviour(new hacerMov(entorno, visualizer, posObjetivo));
        } else {
            System.err.println("❌ Error: El agente necesita (Entorno, Visualizer) como argumentos.");
            doDelete();
        }
    }
}