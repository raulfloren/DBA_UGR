package agente;

import java.util.Objects;

public class Posicion {

    private int fila, columna;

    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    //"Operador sobrecaga =="
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Posicion posicion = (Posicion) obj;
        return this.fila == posicion.fila && this.columna == posicion.columna;
    }

    // HashCode (si dos hashcode son iguales, significa que estan en la misma posicion
    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    void setColumna(int columna) {
        this.columna = columna;
    }

    public Posicion(Posicion copia) {
        this(copia.getFila(), copia.getColumna());
    }

}
