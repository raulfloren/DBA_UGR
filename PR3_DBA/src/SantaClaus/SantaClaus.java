package SantaClaus;

import GUI.SimulacionAgenteGUI;
import jade.core.Agent;
import SantaClaus.comportamientos.ComunicacionSantaClaus;

import herramientas.GestorAgentes;

public class SantaClaus extends Agent {

    private SimulacionAgenteGUI g;

    @Override
    protected void setup() {
        System.out.println("Agente SantaClaus iniciado: " + getLocalName());

        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            this.g = (SimulacionAgenteGUI) args[0];
        } else {
            System.out.println("Error: SantaClaus Parámetros.");
            doDelete(); // Eliminar el agente
            return;
        }

        GestorAgentes.registrarAgente(this, "NPC", "SantaClaus");

        addBehaviour(new ComunicacionSantaClaus(this));
    }

    public SimulacionAgenteGUI getGraficos() {
        return g;
    }

}
