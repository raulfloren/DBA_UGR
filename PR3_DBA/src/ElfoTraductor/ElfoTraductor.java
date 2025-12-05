package ElfoTraductor;

import GUI.SimulacionAgenteGUI;
import jade.core.Agent;
import ElfoTraductor.comportamientos.ComunicacionElfoTraductor;
import herramientas.GestorAgentes;

public class ElfoTraductor extends Agent {

    private SimulacionAgenteGUI g;

    @Override
    protected void setup() {
        System.out.println("Agente ElfoTraductor iniciado: " + getLocalName());

        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            this.g = (SimulacionAgenteGUI) args[0];
        } else {
            System.out.println("Error: ElfoTraductor Parámetros.");
            doDelete(); // Eliminar el agenteá
            return;
        }

        GestorAgentes.registrarAgente(this, "NPC", "ElfoTraductor");

        addBehaviour(new ComunicacionElfoTraductor(this));
    }

    public SimulacionAgenteGUI getGraficos() {
        return g;
    }
}
