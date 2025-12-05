package Agente;

import herramientas.Sensores;
import herramientas.Posicion;
import GUI.SimulacionAgenteGUI;
import Agente.comportamientos.DecisionMov;
import Agente.comportamientos.Percepcion;
import Agente.comportamientos.HacerMov;
import Agente.comportamientos.Validacion;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.HashMap;
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

    // Memoria del agente (para LRTA*)
    // Almacena el coste heurístico aprendido (H) para cada posición
    private HashMap<Posicion, Double> memoriaHeuristica;

    private HashMap<Posicion, Integer> memoriaVisitadas;
    private static final double PENALTY_VISITADA = 0.4; // Penalización pequeña

    // Coste de dar un paso
    private static final double COSTE_ENERGIA = 1.0;

    private Movimientos ultimoMovimiento = null;

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

        // percepción → decisión → acción → validación, 
        addBehaviour(new Percepcion(this));
        addBehaviour(new DecisionMov(this));
        addBehaviour(new HacerMov(this));
        addBehaviour(new Validacion(this, sensores.getEntorno()));
        addBehaviour(new ComunicacionAgente(this));

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
    // Posicion simulada despues de un movimiento
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
            return (double) ((distanciaManhattan(p, posObjetivo) + distanciaEuclidea(p, posObjetivo)) / 2);

        });
    }

    private double calcularCostePaso(Posicion proximaPos) {
        double h = getHeuristica(proximaPos);  // obtenemos la heurística base

        // Si nunca se ha visitado, el coste es normal
        if (!memoriaVisitadas.containsKey(proximaPos)) {
            return COSTE_ENERGIA;
        }

        // Si ya se ha visitado, aplicamos penalización dinámica
        int veces = memoriaVisitadas.getOrDefault(proximaPos, 0);

        // Penalización progresiva: cuanto más se visita, más caro es
        double penalizacionDinamica = h * (PENALTY_VISITADA * veces);

        return COSTE_ENERGIA + penalizacionDinamica;
    }

    // Decidir movimiento (LRTA*)
    public void decidirMov() {

        double minCosteDecision = Double.MAX_VALUE;    // El 'f_dec' más bajo encontrado
        double minCosteAprendizaje = Double.MAX_VALUE; // El 'f_apr' más bajo para aprender
        double minCosteEuclideo = Double.MAX_VALUE;   // El 'tie-breaker' euclídeo
        Movimientos mejorMovimiento = null;

        double H_actual = getHeuristica(posAgente); // H de la casilla actual

        Posicion posPrev = posAnterior;

        // Para cada posible movimiento, estudiamos su factibilidad
        for (Movimientos mov : casillasDisponibles) {
            Posicion proximaPos = getProximaPosicion(mov);
            double H_proximaPos = getHeuristica(proximaPos);

            // Comprobación para no volver atrás
            if (posPrev != null && proximaPos.equals(posPrev)) {
                continue;
            }

            // --- 1. Coste inicial ---
            double costeF_Aprendizaje = COSTE_ENERGIA + H_proximaPos;

            if (costeF_Aprendizaje < minCosteAprendizaje) {
                minCosteAprendizaje = costeF_Aprendizaje;
            }

            // --- 2. Bonificación de dirección ---
            // k será 1.0 (nuevo) o 2.0 (visitado)
            double K_costePaso_conPenalizacion = calcularCostePaso(proximaPos);
            double dx = posObjetivo.getColumna() - posAgente.getColumna();
            double dy = posObjetivo.getFila() - posAgente.getFila();
            double dirX = Math.signum(dx), dirY = Math.signum(dy);

            Posicion next = getProximaPosicion(mov);
            double preferenciaDireccion = 0;
            if (dirX != 0 && Math.signum(next.getColumna() - posAgente.getColumna()) == dirX) {
                preferenciaDireccion -= 0.2;
            }
            if (dirY != 0 && Math.signum(next.getFila() - posAgente.getFila()) == dirY) {
                preferenciaDireccion -= 0.2;
            }

            // 3. Momentum (Inercia)
            // Damos un bonus (coste negativo) si el movimiento actual
            // es el mismo que el último movimiento realizado.
            // Esto "pega" al agente a una dirección (ej. seguir pegado al muro)
            double preferenciaMomentum = 0;
            if (ultimoMovimiento != null && mov == ultimoMovimiento) {
                // Fuerte preferencia por seguir recto
                preferenciaMomentum = -0.1; // Ajusta este valor si es necesario
            }

            double costeF_Decision = K_costePaso_conPenalizacion + H_proximaPos + preferenciaDireccion + preferenciaMomentum;

            // --- 4. Lógica de Decisión con Desempate ---
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
        }

        // --- 4. Aprendizaje: Actualización del coste en la posición actual ---
        if (!casillasDisponibles.isEmpty()) {
            Posicion posActualCopia = new Posicion(posAgente);
            double H_nuevo = Math.max(H_actual, minCosteAprendizaje);
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

    // Hacer el movimiento //HAY QUE METERLO EN EL ENTORNO, AGENTE SOLO DICE QUE SE QUIERE MOVER A DIRECCION x
    public void hacerMov() {
        // Guardamos la posicion anterior para actulizar el entorno y la GUI
        posAnterior = new Posicion(posAgente);

        if (movimientoDecidido == null) {
            System.err.println("¡AGENTE ATRAPADO! No hay movimiento decidido.");
            return;
        }
        switch (movimientoDecidido) {
            case UP ->
                sensores.move(UP);

            case DOWN ->
                sensores.move(DOWN);

            case LEFT ->
                sensores.move(LEFT);

            case RIGHT ->
                sensores.move(RIGHT);

        }

        sensores.addEnergia();

        this.ultimoMovimiento = this.movimientoDecidido;
    }

    public void updateMemoriaVisitadas() {
        Posicion posActualCopia = new Posicion(posAgente);
        memoriaVisitadas.put(posActualCopia, memoriaVisitadas.getOrDefault(posActualCopia, 0) + 1);
    }

    public void imprimirMemoria() {
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
