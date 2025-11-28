package Rudolph;

import GUI.SimulacionAgenteGUI;
import jade.core.Agent;
import Rudolph.comportamientos.ComunicacionRudolph;
import herramientas.GestorAgentes;
import herramientas.Posicion;
import java.util.ArrayList;
/**
 *
 * @author floren
 */
public class Rudolph extends Agent {

    private SimulacionAgenteGUI g;
    private ArrayList<Posicion> posRenosPerdidos;

    @Override
    protected void setup() {
        System.out.println("Agente Rudolph iniciado: " + getLocalName());

        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            this.g = (SimulacionAgenteGUI) args[0];
            this.posRenosPerdidos = (ArrayList<Posicion>) args[1];
        } else {
            System.out.println("Error: Parámetros de inicialización insuficientes para el agente.");
            doDelete(); // Eliminar agente si los parámetros son insuficientes
            return;
        }

        GestorAgentes.registrarAgente(this, "NPC", "Rudolph");

        addBehaviour(new ComunicacionRudolph(this));
    }

    public SimulacionAgenteGUI getGraficos() {
        return g;
    }

    public ArrayList<Posicion> getPosRenosPerdidos() {
        return posRenosPerdidos;
    }
}
