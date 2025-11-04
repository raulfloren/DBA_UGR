package agente;

import GUI.SimulacionAgenteGUI;
import comportamientos.DecisionMov;
import comportamientos.Percepcion;
import comportamientos.HacerMov;
import comportamientos.Validacion;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    // Memoria del agente
    private HashMap<Posicion, Integer> memoriaVisitadas;
    private HashSet<Posicion> memoriaVistas;

    private static final int PESO_DISTANCIA = 5;
    private static final int PESO_MEMORIA = 7;

    private static final int PENALTY_VISITADA = 7;
    private static final int PENALTY_VISTA = 2;

    // Constructor
    public Agente(Posicion posAgente, Posicion posObjetivo, Sensores sensores) {
        this.posAgente = posAgente;
        this.posObjetivo = posObjetivo;
        this.sensores = sensores;
        this.casillasDisponibles = new ArrayList<>();
        this.movimientoDecidido = null;

        this.memoriaVisitadas = new HashMap<>();
        this.memoriaVistas = new HashSet<>();

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

    // Getter y Setters
    public Sensores getSensores() {
        return sensores;
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

    public boolean objetivoEncontrado() {
        return posAgente.equals(posObjetivo);
    }

    // Casillas adyacentes disponibles
    public void verCasillasDisponibles() {
        this.casillasDisponibles = sensores.verCasillasDisponibles();
    }

    // Decidir movimiento
    public void decidirMov() {
        Map<Movimientos, Double> costesCasilla = new HashMap<>();

        for (Movimientos mov : casillasDisponibles) {
            System.out.println("  AAAAAAAAAAAAAAAA:" + mov.toString() + "BBBBB");
            costesCasilla.put(mov, calcularCosteMov(mov));
        }

        Movimientos mejorMovimiento = null;
        double menorCoste = Double.MAX_VALUE;

        for (Map.Entry<Movimientos, Double> entry : costesCasilla.entrySet()) {
            Movimientos mov = entry.getKey();
            double coste = entry.getValue();

            // Imprime un log útil para depurar
            System.out.println(String.format("  -> Opción: %s, Costo: %.2f", mov, coste));

            if (coste < menorCoste) {
                // Encontramos un nuevo coste mínimo
                menorCoste = coste;
                mejorMovimiento = mov;
            }
        }

        this.movimientoDecidido = mejorMovimiento;

        System.out.println("==> Movimiento Decidido: " + this.movimientoDecidido);
    }

    // Distancia al objetivo tras hacer el movimiento siguiente 
    private double calcularCosteMov(Movimientos mov) {

        Posicion posTrasMov = new Posicion(posAgente.getFila(), posAgente.getColumna());

        switch (mov) {
            case UP ->
                posTrasMov.setFila(posAgente.getFila() - 1);
            case DOWN ->
                posTrasMov.setFila(posAgente.getFila() + 1);
            case LEFT ->
                posTrasMov.setColumna(posAgente.getColumna() - 1);
            case RIGHT ->
                posTrasMov.setColumna(posAgente.getColumna() + 1);
        }

        double costeDistancia = distanciaManhattan(posTrasMov, posObjetivo);
        costeDistancia += distanciaEuclidea(posTrasMov, posObjetivo);

        int costeMemoria;

        if (memoriaVisitadas.containsKey(posTrasMov)) {        // Si la hemos visitado ya y la vamos a visitar de nuevo
            int visitas = memoriaVisitadas.getOrDefault(posTrasMov, 0) + 1;
            costeMemoria = PENALTY_VISITADA * visitas;
        } else if (memoriaVistas.contains(posTrasMov)) {        // Si la hemos visto ya y nos vamos a mover a ella 
            costeMemoria = PENALTY_VISTA;
        } else {                                                    // No hemos visitado la celda, ni la gemos visto anteriormente
            costeMemoria = 0;
        }

        return (PESO_DISTANCIA * costeDistancia) + (PESO_MEMORIA * costeMemoria);

    }

    // Distancia Manhattan desde la posAgente hasta la posIbjetivo
    private int distanciaManhattan(Posicion posAgente, Posicion posObjetivo) {
        return Math.abs(posObjetivo.getFila() - posAgente.getFila()) + Math.abs(posObjetivo.getColumna() - posAgente.getColumna());
    }

    // Distancia Ecuclidea desde la posAgente hasta la posIbjetivo
    private double distanciaEuclidea(Posicion posAgente, Posicion posObjetivo) {
        int filaAux = posObjetivo.getFila() - posAgente.getFila();
        int colAux = posObjetivo.getColumna() - posAgente.getColumna();
        return Math.sqrt(filaAux * filaAux + colAux * colAux);
    }

    // Hacer el movimiento
    public void hacerMov() {
        // Guardamos la posicion anterior para actulizar el entorno y la GUI
        posAnterior = new Posicion(posAgente);

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

    // --- MÉTODOS DE MEMORIA ---
    // Casillas vistas y visitadas por el agente
    public void updateMemoriaVisitadas() {
        Posicion posActualCopia = new Posicion(posAgente);
        memoriaVisitadas.put(posActualCopia, memoriaVisitadas.getOrDefault(posActualCopia, 0) + 1);
    }

    public void updateMemoriaVistas() {
        ArrayList<Posicion> casillasVistas = sensores.getCasillasVistas();
        for (Posicion pos : casillasVistas) {
            memoriaVistas.add(new Posicion(pos));
        }
    }

    public void imprimirMemoria() {
        System.out.println("Memoria del Agente:");
        for (HashMap.Entry<Posicion, Integer> entry : memoriaVisitadas.entrySet()) {
            Posicion posicion = entry.getKey();
            Integer vecesPasadas = entry.getValue();
            System.out.println(String.format("Posición: %d %d, Veces Pasadas: %d", posicion.getFila(), posicion.getColumna(), vecesPasadas));
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Agente " + getAID().getLocalName() + " terminando (takeDown).");

        // Cierra la ventana de la GUI (por si acaso no se cerró)
        if (GUI != null) {
            GUI.dispose();
        }
    }
}
