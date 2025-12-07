package SantaClaus;

import GUI.SimulacionAgenteGUI;
import jade.core.Agent;
import SantaClaus.comportamientos.ComunicacionSantaClaus;

import herramientas.GestorAgentes;
import herramientas.Posicion;

public class SantaClaus extends Agent {

    private SimulacionAgenteGUI g;

    private Posicion posSantaClaus;

    @Override
    protected void setup() {
        System.out.println("Soy SantaClaus: " + getAID().getLocalName());

        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            this.g = (SimulacionAgenteGUI) args[0];
            this.posSantaClaus = (Posicion) args[1];
        } else {
            System.out.println("Error: SantaClaus Parámetros.");
            doDelete(); // Eliminar el agente
            return;
        }

        GestorAgentes.registrarAgente(this, "NPC", "santaClaus");

        addBehaviour(new ComunicacionSantaClaus(this, posSantaClaus));
    }

    public SimulacionAgenteGUI getGraficos() {
        return g;
    }

}
