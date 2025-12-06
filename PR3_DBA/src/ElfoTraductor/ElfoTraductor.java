package ElfoTraductor;

import GUI.SimulacionAgenteGUI;
import jade.core.Agent;
import ElfoTraductor.comportamientos.ComunicacionElfoTraductor;
import herramientas.GestorAgentes;

public class ElfoTraductor extends Agent {

    private SimulacionAgenteGUI g;

    @Override
    protected void setup() {
        System.out.println("Soy ElfoTraductor: " + getAID().getLocalName());

        Object[] args = getArguments();
        if (args != null && args.length == 1) {
            this.g = (SimulacionAgenteGUI) args[0];
        } else {
            System.out.println("Error: ElfoTraductor Parámetros.");
            doDelete(); // Eliminar el agenteá
            return;
        }

        GestorAgentes.registrarAgente(this, "NPC", "elfoTraductor");

        addBehaviour(new ComunicacionElfoTraductor(this));
    }

    public SimulacionAgenteGUI getGraficos() {
        return g;
    }
}
