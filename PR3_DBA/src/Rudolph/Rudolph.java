package Rudolph;

import GUI.SimulacionAgenteGUI;
import jade.core.Agent;
import Rudolph.comportamientos.ComunicacionRudolph;
import herramientas.GestorAgentes;
import herramientas.Posicion;
import java.util.ArrayList;

public class Rudolph extends Agent {

    private SimulacionAgenteGUI g;
    private ArrayList<Posicion> posRenosPerdidos;

    @Override
    @SuppressWarnings("unchecked")
    protected void setup() {
        System.out.println("Soy Rudolph iniciado: " + getAID().getLocalName());

        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            this.g = (SimulacionAgenteGUI) args[0];
            this.posRenosPerdidos = (ArrayList<Posicion>) args[1];
        } else {
            System.out.println("Error: Rudolph Parámetros.");
            doDelete(); // Eliminar el agente
            return;
        }

        GestorAgentes.registrarAgente(this, "NPC", "rudolph");

        addBehaviour(new ComunicacionRudolph(this));
    }

    public SimulacionAgenteGUI getGraficos() {
        return g;
    }

    public ArrayList<Posicion> getPosRenosPerdidos() {
        return posRenosPerdidos;
    }
}
