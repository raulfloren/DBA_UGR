package entorno;

import agente.Posicion;
import java.io.IOException;

/**
 *
 * @author floren
 */
public class Mapa {

    private String nombre;
    private int[][] mapa;
    private int filas;
    private int columnas;

    public Mapa(String nombreMapa) {
        this.nombre = nombreMapa;
        try {
            this.mapa = MapLoader.loadMap(nombreMapa);
            if (mapa != null) {
                this.filas = mapa.length;
                this.columnas = mapa[0].length;
            }
        } catch (IOException e) {
            System.err.println("❌ Error al cargar el mapa: " + nombreMapa);
            e.printStackTrace();
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int[][] getMapa() {
        return mapa;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public void printMapa() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print(mapa[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void ponerItemEnMapa(Posicion pos, int item) {
        if (esPosible(pos)) {
            mapa[pos.getFila()][pos.getColumna()] = item;
        }
    }

    public int getItemEnPosicion(int row, int column) {
        int item = mapa[row][column];
        return item;
    }

    public int getItemEnPosicion(Posicion pos) {
        int item = mapa[pos.getFila()][pos.getColumna()];
        return item;
    }

    public boolean esPosible(Posicion pos) {
        return (pos.getFila() >= 0 && pos.getFila() < this.getFilas())
                && (pos.getColumna() >= 0 && pos.getColumna() < this.getColumnas())
                && (this.getItemEnPosicion(pos) != -1);
    }
}
