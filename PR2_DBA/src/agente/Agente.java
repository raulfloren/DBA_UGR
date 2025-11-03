package agente;

import GUI.SimulacionAgenteGUI;
import comportamientos.DecisionMov;
import comportamientos.Percepcion;
import comportamientos.HacerMov;
import comportamientos.Validacion;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import movimientos.Movimientos;
import static movimientos.Movimientos.*;

public class Agente extends Agent {

    // GUI
    private SimulacionAgenteGUI GUI;

    // Sensores y posiciones Agente y objetivo;
    private Sensores sensores;
    private Posicion posAgente, posObjetivo;

    //Movimientos
    private ArrayList<Movimientos> casillasDisponibles;
    private Movimientos movimientoDecidido; // se modifciara en decidirMov()
    private Posicion posAnterior; //Para dibujar rastro

    // Constructor
    public Agente(Posicion posAgente, Posicion posObjetivo, Sensores sensores) {
        this.posAgente = posAgente;
        this.posObjetivo = posObjetivo;
        this.sensores = sensores;
        this.casillasDisponibles = new ArrayList<>();
        this.movimientoDecidido = null;
    }

    public Agente() {
        this(null, null, null);
    }

    @Override
    protected void setup() {
        System.out.println("Soy agente: " + getAID().getLocalName());

        Object[] args = getArguments();
        if (args != null && args.length == 4) {
            posAgente = (Posicion) args[0];
            posObjetivo = (Posicion) args[1];
            sensores = (Sensores) args[2];
            GUI = (SimulacionAgenteGUI) args[3];

        } else {
            System.err.println("❌ Error: El agente necesita (posAgente, posObjetivo, Senores, GUI) como argumentos.");
            doDelete();
        }

        //percepción → decisión → acción → validación, 
        addBehaviour(new Percepcion(this));
        addBehaviour(new DecisionMov(this));
        addBehaviour(new HacerMov(this));
        addBehaviour(new Validacion(this, sensores.getEntorno()));

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
        Map<Movimientos, Integer> distanciaEnCasilla = new HashMap<>();

        for (Movimientos mov : casillasDisponibles) {
            calcularDistanciaTrasMov(mov, distanciaEnCasilla);
        }

        // El que menor distancia nos de sera el mejor
        Movimientos mejorMovimiento = null;
        int menorDistancia = Integer.MAX_VALUE;

        for (Map.Entry<Movimientos, Integer> entry : distanciaEnCasilla.entrySet()) {
            if (entry.getValue() < menorDistancia) {
                menorDistancia = entry.getValue();
                mejorMovimiento = entry.getKey();
            }
        }
        this.movimientoDecidido = mejorMovimiento;
    }

    private void calcularDistanciaTrasMov(Movimientos mov, Map<Movimientos, Integer> distanciaEnCasilla) {
        Posicion posTrasMov = new Posicion(posAgente.getFila(), posAgente.getColumna());

        switch (mov) {
            case UP:
                posTrasMov.setFila(posAgente.getFila() - 1);
                break;
            case DOWN:
                posTrasMov.setFila(posAgente.getFila() + 1);
                break;
            case LEFT:
                posTrasMov.setColumna(posAgente.getColumna() - 1);
                break;
            case RIGHT:
                posTrasMov.setColumna(posAgente.getColumna() + 1);
                break;
        }

        int distancia = distanciaManhattan(posTrasMov, posObjetivo);
        distanciaEnCasilla.put(mov, distancia);

    }

    private int distanciaManhattan(Posicion posAgente, Posicion posObjetivo) {
        return Math.abs(posObjetivo.getFila() - posAgente.getFila()) + Math.abs(posObjetivo.getColumna() - posAgente.getColumna());
    }

    public void hacerMov() {
        //modificar pos agente en -1 filas y 0 columnas ...
        posAnterior = new Posicion(posAgente.getFila(), posAgente.getColumna());;

        switch (movimientoDecidido) {
            case UP:
                posAgente.setFila(posAgente.getFila() - 1);  // Arriba
                break;

            case DOWN:
                posAgente.setFila(posAgente.getFila() + 1);  // Abajo
                break;

            case LEFT:
                posAgente.setColumna(posAgente.getColumna() - 1);  // Izquierda
                break;

            case RIGHT:
                posAgente.setColumna(posAgente.getColumna() + 1);  // Derecha
                break;

        }

        sensores.addEnergia();
    }

    public SimulacionAgenteGUI getGUI() {
        return GUI;
    }

    public Movimientos getMovDecidido() {
        return movimientoDecidido;
    }

    public Posicion getPosAgente() {
        return posAgente;
    }

    public Posicion getPosAnterior() {
        return posAnterior;
    }
}
