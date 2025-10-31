package agente;

import GUI.SimulacionAgenteGUI;
import comportamientos.DecisionMov;
import comportamientos.Percepcion;
import comportamientos.HacerMov;
import entorno.Entorno;
import jade.core.Agent;
import java.util.ArrayList;
import movimientos.Movimientos;

public class Agente extends Agent {

    // GUI
    private SimulacionAgenteGUI visualizer;

    // Sensores y posiciones Agente y objetivo;
    private Sensores sensores;
    private Posicion posAgente, posObjetivo;

    //Movimientos
    private ArrayList<Movimientos> casillasDisponibles;
    private Movimientos movimientoDecidido; // se modifciara en decidirMov()

    // Constructor
    public Agente(Sensores sensores, Posicion posAgente, Posicion posObjetivo) {
        this.sensores = sensores;
        this.posAgente = posAgente;
        this.posObjetivo = posObjetivo;
        this.casillasDisponibles = new ArrayList();
        this.movimientoDecidido = null;
    }

    @Override
    protected void setup() {
        System.out.println("Soy agente: " + getAID().getLocalName());

        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            entorno = (Entorno) args[0];
            visualizer = (SimulacionAgenteGUI) args[1];
            posObjetivo = entorno.getPosicionObjetivo();

            addBehaviour(new HacerMov(entorno, visualizer, posObjetivo));
        } else {
            System.err.println("❌ Error: El agente necesita (Entorno, Visualizer) como argumentos.");
            doDelete();
        }

        //percepción → decisión → acción → validación, 
        addBehaviour(new Percepcion(this));
        addBehaviour(new DecisionMov(this));
        addBehaviour(new HacerMov(this));

    }

    public Sensores getSensores() {
        return sensores;
    }

    public boolean objetivoEncontrado() {
        return posAgente.equals(posObjetivo);
    }

    public void verCasillasDisponibles() {
        this.casillasDisponibles = sensores.verCasillasDisponibles();
    }

    public void decidirMov() {
        /*
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
        if (p.sensorArriba == 0) {
            return Movimientos.UP;
        }
        if (p.sensorAbajo == 0) {
            return Movimientos.DOWN;
        }
        if (p.sensorIzquierda == 0) {
            return Movimientos.LEFT;
        }
        if (p.sensorDerecha == 0) {
            return Movimientos.RIGHT;
        }

        // Atascado (sin movimientos libres)
        return Movimientos.NONE;
         */
    }

    public void hacerMov() {
        switch (movimientoDecidido) {
            case UP:
            //modificar pos agente en -1 filas y 0 columnas ...
            case DOWN:

            case RIGHT:

            case LEFT:
        }

        sensores.addEnegia();
    }
}
