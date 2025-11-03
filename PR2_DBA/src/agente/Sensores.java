package agente;

import entorno.Entorno;
import java.util.ArrayList;
import movimientos.Movimientos;

public class Sensores {

    private Entorno entorno;
    private int energia;

    public Sensores(Entorno entorno, int engeria) {
        this.entorno = entorno;
        this.energia = energia;
    }

    public void addEnergia() {
        energia++;
    }

    public int getEnergia() {
        return energia;
    }

    public Entorno getEntorno() {
        return entorno;
    }

    public ArrayList<Movimientos> verCasillasDisponibles() {
        // Aqui se llama al entorno para ver las casillas disponibles a las que se puede mover el agente 
        ArrayList<Movimientos> casillasDisponibles = new ArrayList<>();

        int fila = entorno.getPosicionAgente().getFila();
        int columna = entorno.getPosicionAgente().getColumna();

        if (leerSensores(fila - 1, columna, Movimientos.UP) != null) {
            casillasDisponibles.add(Movimientos.UP);
        }
        if (leerSensores(fila + 1, columna, Movimientos.DOWN) != null) {
            casillasDisponibles.add(Movimientos.DOWN);
        }
        if (leerSensores(fila, columna - 1, Movimientos.LEFT) != null) {
            casillasDisponibles.add(Movimientos.LEFT);
        }
        if (leerSensores(fila, columna + 1, Movimientos.RIGHT) != null) {
            casillasDisponibles.add(Movimientos.RIGHT);
        }

        return casillasDisponibles;
    }

    /**
     * Método auxiliar para leer un sensor. Devuelve -1 si está fuera del mapa o
     * si es un obstáculo. Devuelve 0 si está libre.
     */
    private Movimientos leerSensores(int fila, int columna, Movimientos movimiento) {
        //Aqui hay que comprobar la direccion de los 4 sensores

        // Comprobar límites del mapa
        if (fila < 0 || fila >= entorno.getMapa().getFilas() || columna < 0 || columna >= entorno.getMapa().getColumnas()) {
            return null; // Fuera de límites es como un muro
        }

        // Comprobar obstáculo
        if (entorno.getMapa().getMapa()[fila][columna] == -1) {
            return null; // Obstáculo
        }

        return movimiento; // Celda libre
    }
}
