package GUI;

import movimientos.Movimientos;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class SimulacionAgenteGUI extends JFrame {

    private int[][] matriz;
    private JTextArea textAreaTraza;
    private JPanel panelMatriz;

    private final static int MURO = -1, SUELO = 0, AGENTE = 9, OBJETIVO = 8;

    private Movimientos direccionAgente;
    private Map<Movimientos, Image> imagenesAgente;
    private Map<Integer, Image> imagenes;

    public SimulacionAgenteGUI(String textoInicial, int[][] matriz, Movimientos direccionAgente) {
        this.matriz = matriz;
        this.direccionAgente = direccionAgente;
        cargarImagenes();
        inicializarComponentes(textoInicial);
    }

    public SimulacionAgenteGUI() {
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
            imagenes.put(MURO, ImageIO.read(getClass().getResource("/assets/MURO.png")));
            imagenes.put(OBJETIVO, ImageIO.read(getClass().getResource("/assets/OBJETIVO.png")));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("⚠️  No se encontraron imágenes, se usará dibujo por colores.");
        }
    }

    private void inicializarComponentes(String textoInicial) {
        setTitle("Simulación del Agente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Panel central para la matriz
        panelMatriz = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarMatriz(g);
            }
        };
        panelMatriz.setPreferredSize(new Dimension(700, 700));
        panelMatriz.setBackground(Color.WHITE);
        add(panelMatriz, BorderLayout.CENTER);

        // Panel derecho con BoxLayout vertical
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setPreferredSize(new Dimension(300, 700));

        // JTextArea con scroll
        textAreaTraza = new JTextArea();
        textAreaTraza.setEditable(false);
        textAreaTraza.setText(textoInicial);
        textAreaTraza.setLineWrap(true);
        textAreaTraza.setWrapStyleWord(true);

        JScrollPane scrollPaneTraza = new JScrollPane(textAreaTraza);
        scrollPaneTraza.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPaneTraza.setMaximumSize(new Dimension(300, 700 - 50));
        scrollPaneTraza.setPreferredSize(new Dimension(300, 700 - 50));
        scrollPaneTraza.setMinimumSize(new Dimension(300, 100));
        panelDerecho.add(scrollPaneTraza);

        // Espacio flexible para que el botón quede abajo
        panelDerecho.add(Box.createVerticalGlue());

        // Panel para centrar el botón tanto en X como en Y
        JPanel panelBoton = new JPanel(new GridBagLayout());
        JButton cerrarButton = new JButton("Cerrar");
        cerrarButton.addActionListener(e -> dispose());
        panelBoton.add(cerrarButton);
        panelDerecho.add(panelBoton, BorderLayout.SOUTH);

        add(panelDerecho, BorderLayout.EAST);
        add(panelDerecho, BorderLayout.EAST);

        // Key listener para cerrar con Q
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyChar() == 'q' || e.getKeyChar() == 'Q') {
                    dispose();
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();

        pack();  // ajusta el tamaño de la ventana al contenido
        setLocationRelativeTo(null); // centra la ventana en pantalla
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

    public void actualizarMatriz(int[][] nuevaMatriz, Movimientos nuevaDireccionAgente) {
        this.matriz = nuevaMatriz;
        this.direccionAgente = nuevaDireccionAgente;
        panelMatriz.repaint();
    }

    public void mostrarVentanaVictoria() {
        JOptionPane.showMessageDialog(this, "¡El agente ha alcanzado el objetivo!", "Victoria", JOptionPane.INFORMATION_MESSAGE);
    }
}
