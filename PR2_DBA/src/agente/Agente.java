package agente;

import GUI.SimulacionAgenteGUI;
import comportamientos.DecisionMov;
import comportamientos.Percepcion;
import comportamientos.HacerMov;
import comportamientos.Validacion;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.HashSet;
import movimientos.Movimientos;
//import static movimientos.Movimientos.*;

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

    // Memoria del agente (para LRTA*)
    // Almacena el coste heurístico aprendido (H) para cada posición
    private HashMap<Posicion, Double> memoriaHeuristica;

    private HashMap<Posicion, Integer> memoriaVisitadas;
    private static final double PENALTY_VISITADA = 0.3; // Penalización pequeña

    // Coste de dar un paso
    private static final double COSTE_ENERGIA = 1.0;

    // Constructor
    public Agente(Posicion posAgente, Posicion posObjetivo, Sensores sensores) {
        this.posAgente = posAgente;
        this.posObjetivo = posObjetivo;
        this.sensores = sensores;
        this.casillasDisponibles = new ArrayList<>();
        this.movimientoDecidido = null;

        this.memoriaHeuristica = new HashMap<>();
        this.memoriaVisitadas = new HashMap<>();

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

    // --- LÓGICA DE DECISIÓN LRTA* ---
    /**
     * Obtiene la posición resultante tras aplicar un movimiento.
     */
    private Posicion getProximaPosicion(Movimientos mov) {
        Posicion posTrasMov = switch (mov) {
            case UP ->
                new Posicion(posAgente.getFila() - 1, posAgente.getColumna());
            case DOWN ->
                new Posicion(posAgente.getFila() + 1, posAgente.getColumna());
            case LEFT ->
                new Posicion(posAgente.getFila(), posAgente.getColumna() - 1);
            case RIGHT ->
                new Posicion(posAgente.getFila(), posAgente.getColumna() + 1);
        };
        return posTrasMov;
    }

    /**
     * Obtiene el valor heurístico H(n) de una posición. Si no está en la
     * memoria, la calcula (Manhattan), la guarda y la devuelve.
     */
    private double getHeuristica(Posicion pos) {
        // Usamos computeIfAbsent para obtener el valor si existe, 
        // o para calcularlo, guardarlo y devolverlo si no existe.
        return memoriaHeuristica.computeIfAbsent(pos, p -> {
            // Valor heurístico inicial: Distancia Manhattan
            return (double) distanciaManhattan(p, posObjetivo);

        });
    }

    private double calcularCostePaso(Posicion proximaPos) {
        // Si la hemos visitado CUALQUIER número de veces, añadimos la penalización.
        if (memoriaVisitadas.containsKey(proximaPos)) {
            // El coste de decisión es el coste real MÁS la penalización
            double h = getHeuristica(proximaPos);
            return COSTE_ENERGIA + (h * PENALTY_VISITADA);

        } else {
            return COSTE_ENERGIA; // Coste normal
        }
    }

    // Decidir movimiento (LRTA*)
// Archivo: agente/Agente.java
    // Decidir movimiento (LRTA* Híbrido + Desempate Euclídeo)
    public void decidirMov() {

        double minCosteDecision = Double.MAX_VALUE;    // El 'f_dec' más bajo encontrado
        double minCosteAprendizaje = Double.MAX_VALUE; // El 'f_apr' más bajo para aprender
        double minCosteEuclideo = Double.MAX_VALUE;   // El 'tie-breaker' euclídeo
        Movimientos mejorMovimiento = null;

        double H_actual = getHeuristica(posAgente); // H de la casilla actual

        Posicion posPrev = posAnterior;

        for (Movimientos mov : casillasDisponibles) {
            Posicion proximaPos = getProximaPosicion(mov);
            double H_proximaPos = getHeuristica(proximaPos);

            if (posPrev != null && proximaPos.equals(posPrev)) {
                continue; // evita volver atrás directamente
            }

            // --- 1. Coste para Aprender (Puro) ---
            double costeF_Aprendizaje = COSTE_ENERGIA + H_proximaPos;

            // !! AQUÍ ESTÁ EL BUG CORREGIDO !!
            if (costeF_Aprendizaje < minCosteAprendizaje) {
                minCosteAprendizaje = costeF_Aprendizaje; // <-- Corregido
            }

            // --- 2. Coste para Decidir (Con Penalización) ---
            // k será 1.0 (nuevo) o 2.0 (visitado)
            double K_costePaso_conPenalizacion = calcularCostePaso(proximaPos);
            double costeF_Decision = K_costePaso_conPenalizacion + H_proximaPos;

            // --- 3. Lógica de Decisión con Desempate ---
            if (costeF_Decision < minCosteDecision) {
                // A. Es un nuevo coste mínimo. Es el mejor movimiento.
                minCosteDecision = costeF_Decision;
                minCosteEuclideo = distanciaEuclidea(proximaPos, posObjetivo); // Guardamos su Euclídea
                mejorMovimiento = mov;

            } else if (Math.abs(costeF_Decision - minCosteDecision) < 0.001) { // Comparación segura de doubles
                // B. Es un EMPATE. Usamos el desempate Euclídeo.
                double costeEuclideoActual = distanciaEuclidea(proximaPos, posObjetivo);

                if (costeEuclideoActual < minCosteEuclideo) {
                    // Este movimiento gana el desempate
                    // System.out.println(String.format("    -> GANA DESEMPATE EUCLÍDEO (Actual: %.2f < Anterior: %.2f)",
                    //       costeEuclideoActual, minCosteEuclideo));
                    minCosteEuclideo = costeEuclideoActual;
                    mejorMovimiento = mov;
                }
            }
        } // --- Fin del bucle for ---

        // --- 4. ¡El aprendizaje! (Monótono y Puro) ---
        if (!casillasDisponibles.isEmpty()) {
            Posicion posActualCopia = new Posicion(posAgente);

            // H_nuevo no se irá a Infinito gracias a la corrección del bug
            double H_nuevo = Math.max(H_actual, minCosteAprendizaje + COSTE_ENERGIA);

            memoriaHeuristica.put(posActualCopia, H_nuevo);
        }

        // --- 5. Establecer el movimiento decidido ---
        this.movimientoDecidido = mejorMovimiento;
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

        if (movimientoDecidido == null) {
            // Esto puede pasar si el agente está atrapado
            System.err.println("¡AGENTE ATRAPADO! No hay movimiento decidido.");
            // Opcionalmente, podrías forzar el fin del agente aquí
            // doDelete();
            return;
        }
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

    public void updateMemoriaVisitadas() {
        // Usamos posAnterior porque la memoria se actualiza DESPUÉS de moverse
        // No, espera, se actualiza en HacerMov, ANTES de moverse... 
        // No, HacerMov actualiza posAgente. 
        // Vamos a actualizar la memoria en 'HacerMov' DESPUÉS de que se mueva.

        // Miento, 'HacerMov.java' lo llama *después* de 'agente.hacerMov()'.
        // Así que 'posAgente' ya es la *nueva* posición.
        Posicion posActualCopia = new Posicion(posAgente);
        memoriaVisitadas.put(posActualCopia, memoriaVisitadas.getOrDefault(posActualCopia, 0) + 1);
    }

    // --- MÉTODOS DE MEMORIA ---
    public void imprimirMemoria() {
        // Actualizado para imprimir la nueva memoria
        for (HashMap.Entry<Posicion, Double> entry : memoriaHeuristica.entrySet()) {
            Posicion posicion = entry.getKey();
            Double heuristica = entry.getValue();
            //System.out.println(String.format("Posición: (%d, %d), H aprendido: %.2f",
            //      posicion.getFila(), posicion.getColumna(), heuristica));
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Agente " + getAID().getLocalName() + " terminando (takeDown).");
        imprimirMemoria(); // Imprime la memoria aprendida al final
        // Cierra la ventana de la GUI (por si acaso no se cerró)
        if (GUI != null) {
            GUI.dispose();
        }
    }
}
