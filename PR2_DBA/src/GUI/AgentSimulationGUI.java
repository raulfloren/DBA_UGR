package GUI;

/**
 *
 * @author floren
 */
import action.Actions;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class AgentSimulationGUI extends JFrame {

    private int[][] matriz;
    private JTextArea textAreaTraza;
    private JPanel panelMatriz;

    private final static int AGENTE = 9, OBJETIVO = 8, MURO = -1, SUELO = 0, SUELO1 = 1, SUELO2 = 2, SUELO3 = 3;

    private Actions direccionAgente;
    private Map<Actions, Image> imagenesAgente;
    private Map<Integer, Image> imagenes;

    public AgentSimulationGUI(String textoInicial, int[][] matriz, Actions direccionAgente) {
        this.matriz = matriz;
        this.direccionAgente = direccionAgente;
        cargarImagenes();
        inicializarComponentes(textoInicial);
    }

    public AgentSimulationGUI() {
        this.matriz = null;
        this.direccionAgente = null;
        cargarImagenes();
    }

    /**
     * Carga imágenes opcionales (no es obligatorio que existan).
     */
    private void cargarImagenes() {
        imagenesAgente = new HashMap<>();
        imagenes = new HashMap<>();
        try {
            imagenes.put(SUELO, ImageIO.read(getClass().getResource("/assets/SUELO.png")));
            imagenes.put(SUELO1, ImageIO.read(getClass().getResource("/assets/SUELO1.png")));
            imagenes.put(SUELO2, ImageIO.read(getClass().getResource("/assets/SUELO2.png")));
            imagenes.put(SUELO3, ImageIO.read(getClass().getResource("/assets/SUELO3.png")));
            imagenes.put(MURO, ImageIO.read(getClass().getResource("/assets/MURO.png")));
            imagenes.put(OBJETIVO, ImageIO.read(getClass().getResource("/assets/OBJETIVO.png")));
            imagenesAgente.put(Actions.AR, ImageIO.read(getClass().getResource("/assets/ARR.png")));
            imagenesAgente.put(Actions.AB, ImageIO.read(getClass().getResource("/assets/ABA.png")));
            imagenesAgente.put(Actions.IZQ, ImageIO.read(getClass().getResource("/assets/IZQ.png")));
            imagenesAgente.put(Actions.DCHA, ImageIO.read(getClass().getResource("/assets/DER.png")));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("⚠️ No se encontraron imágenes, se usará dibujo por colores.");
        }
    }

    private void inicializarComponentes(String textoInicial) {
        setTitle("Simulación del Agente");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        panelMatriz = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarMatriz(g);
            }
        };
        panelMatriz.setPreferredSize(new Dimension(700, 700));
        panelMatriz.setBackground(Color.WHITE);

        textAreaTraza = new JTextArea();
        textAreaTraza.setEditable(false);
        textAreaTraza.setText(textoInicial);
        textAreaTraza.setLineWrap(true);
        textAreaTraza.setWrapStyleWord(true);

        JScrollPane scrollPaneTraza = new JScrollPane(textAreaTraza);
        scrollPaneTraza.setPreferredSize(new Dimension(300, 700));

        add(panelMatriz, BorderLayout.CENTER);
        add(scrollPaneTraza, BorderLayout.EAST);

        setVisible(true);
    }

    private void dibujarMatriz(Graphics g) {
        if (matriz == null) {
            return;
        }

        int filas = matriz.length;
        int columnas = matriz[0].length;
        int anchoCelda = Math.min(700 / columnas, 700 / filas);
        int altoCelda = anchoCelda;
        int offsetX = (700 - (anchoCelda * columnas)) / 2;
        int offsetY = (700 - (altoCelda * filas)) / 2;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                int valor = matriz[i][j];
                Image imagen = imagenes.get(valor);

                if (imagen != null) {
                    // Si hay imagen, la usa
                    g.drawImage(imagen, offsetX + j * anchoCelda, offsetY + i * altoCelda, anchoCelda, altoCelda, this);
                } else {
                    // Si no hay imagen, usa color
                    switch (valor) {
                        case MURO ->
                            g.setColor(Color.DARK_GRAY);
                        case OBJETIVO ->
                            g.setColor(Color.GREEN);
                        case SUELO1 ->
                            g.setColor(new Color(240, 240, 240));
                        case SUELO2 ->
                            g.setColor(new Color(220, 220, 220));
                        case SUELO3 ->
                            g.setColor(new Color(200, 200, 200));
                        default ->
                            g.setColor(Color.WHITE);
                    }
                    g.fillRect(offsetX + j * anchoCelda, offsetY + i * altoCelda, anchoCelda, altoCelda);
                }

                if (valor == AGENTE) {
                    Image imgAgente = imagenesAgente.get(direccionAgente);
                    if (imgAgente != null) {
                        g.drawImage(imgAgente, offsetX + j * anchoCelda, offsetY + i * altoCelda, anchoCelda, altoCelda, this);
                    } else {
                        // Si no hay imagen, dibuja el agente como un círculo azul
                        g.setColor(Color.BLUE);
                        g.fillOval(offsetX + j * anchoCelda + anchoCelda / 4, offsetY + i * altoCelda + altoCelda / 4, anchoCelda / 2, altoCelda / 2);
                    }
                }

                g.setColor(Color.BLACK);
                g.drawRect(offsetX + j * anchoCelda, offsetY + i * altoCelda, anchoCelda, altoCelda);
            }
        }
    }

    public void agregarTraza(String nuevaAccion) {
        textAreaTraza.insert(nuevaAccion + "\n--------------------------------------------\n", 0);
    }

    public void actualizarMatriz(int[][] nuevaMatriz, Actions nuevaDireccionAgente) {
        this.matriz = nuevaMatriz;
        this.direccionAgente = nuevaDireccionAgente;
        panelMatriz.repaint();
    }

    public void mostrarVentanaVictoria() {
        JOptionPane.showMessageDialog(this, "¡El agente ha alcanzado el objetivo!", "Victoria", JOptionPane.INFORMATION_MESSAGE);
    }
}
