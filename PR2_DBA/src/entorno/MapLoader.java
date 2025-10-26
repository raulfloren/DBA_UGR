package entorno;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MapLoader {

    /**
     * Carga un mapa desde un archivo de texto.
     *
     * @param filePath ruta del archivo (por ejemplo "resources/maps/mapa1.txt")
     * @return una matriz de enteros con el contenido del mapa
     * @throws IOException si el archivo no existe o tiene formato incorrecto
     */
    public static int[][] loadMap(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // Leer dimensiones
            int filas = Integer.parseInt(br.readLine().trim());
            int columnas = Integer.parseInt(br.readLine().trim());

            int[][] mapa = new int[filas][columnas];

            for (int i = 0; i < filas; i++) {
                String linea = br.readLine();
                if (linea == null) {
                    throw new IOException("Faltan filas en el archivo del mapa.");
                }

                // Divide por espacios o tabulaciones
                String[] valores = linea.trim().split("\\s+");

                if (valores.length != columnas) {
                    throw new IOException("Número incorrecto de columnas en la fila " + (i + 1));
                }

                for (int j = 0; j < columnas; j++) {
                    mapa[i][j] = Integer.parseInt(valores[j]);
                }
            }

            return mapa;
        }
    }
}
