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
    
    // --- UPDATED: Variables for 4 status rows ---
    // Row 1: Main Agent Status
    private JLabel labelFoto1; 
    private JTextArea labelMensaje1;
    // Row 2: Secondary Status / Metric
    private JLabel labelFoto2; 
    private JTextArea labelMensaje2;
    // Row 3: Third Status / Metric
    private JLabel labelFoto3; 
    private JTextArea labelMensaje3;
    // Row 4: Fourth Status / Metric
    private JLabel labelFoto4; 
    private JTextArea labelMensaje4;
    // --- END UPDATED ---

    private final static int MURO = -1, SUELO = 0, SUELO2 = 1, CAMINO = -4, AGENTE = -2, SANTA = -3, RENO = -5, RENO_PICTURE = -6, SANTA_PICTURE = -7, ELFO_PICTURE = -8, AGENT_PICTURE = -9;

    private Movimientos direccionAgente;
    private Map<Movimientos, Image> imagenesAgente;
    private Map<Integer, Image> imagenes;

    public SimulacionAgenteGUI(String textoInicial, int[][] matriz, Movimientos direccionAgente) {
        this.matriz = matriz;
        this.direccionAgente = direccionAgente;
        cargarImagenes();
        inicializarComponentes(textoInicial);
        // Initialize the status
        actualizarEstado(1, "¡Simulación Iniciada!", AGENTE);
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
            imagenes.put(SUELO2, ImageIO.read(getClass().getResource("/assets/SUELO2.png")));
            imagenes.put(MURO, ImageIO.read(getClass().getResource("/assets/MURO.png")));
            imagenes.put(CAMINO, ImageIO.read(getClass().getResource("/assets/CAMINO.png")));
            imagenes.put(SANTA, ImageIO.read(getClass().getResource("/assets/OBJETIVO.png")));
            imagenes.put(RENO, ImageIO.read(getClass().getResource("/assets/RENO.png")));

            imagenes.put(RENO_PICTURE, ImageIO.read(getClass().getResource("/assets/alumnoEmpollon.png")));
            imagenes.put(SANTA_PICTURE, ImageIO.read(getClass().getResource("/assets/profesor.png")));
            imagenes.put(ELFO_PICTURE, ImageIO.read(getClass().getResource("/assets/delegado.png")));            
            imagenes.put(AGENT_PICTURE, ImageIO.read(getClass().getResource("/assets/alumno.png")));  
            
            imagenesAgente.put(Movimientos.DOWN, ImageIO.read(getClass().getResource("/assets/heroDOWN.png")));
            imagenesAgente.put(Movimientos.UP, ImageIO.read(getClass().getResource("/assets/heroUP.png")));
            imagenesAgente.put(Movimientos.LEFT, ImageIO.read(getClass().getResource("/assets/heroLEFT.png")));
            imagenesAgente.put(Movimientos.RIGHT, ImageIO.read(getClass().getResource("/assets/heroRIGHT.png")));
            

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("⚠️ No se encontraron imágenes, se usará dibujo por colores.");
        }
    }

    private void inicializarComponentes(String textoInicial) {
        setTitle("Simulación del Agente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        // JTextArea con scroll (Traza)
        textAreaTraza = new JTextArea();
        textAreaTraza.setEditable(false);
        textAreaTraza.setText(textoInicial);
        textAreaTraza.setLineWrap(true);
        textAreaTraza.setWrapStyleWord(true);

        JScrollPane scrollPaneTraza = new JScrollPane(textAreaTraza);
        scrollPaneTraza.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPaneTraza.setMaximumSize(new Dimension(300, 300));
        scrollPaneTraza.setPreferredSize(new Dimension(300, 300));
        scrollPaneTraza.setMinimumSize(new Dimension(300, 100));

        // Etiqueta del título de la traza
        JLabel trazaLabel = new JLabel("Historial de Acciones:");
        trazaLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        trazaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelDerecho.add(Box.createVerticalStrut(10));
        panelDerecho.add(trazaLabel);
        panelDerecho.add(scrollPaneTraza);
        panelDerecho.add(Box.createVerticalStrut(10));

        // Panel para la Grilla de Estado (4 Filas)
        JPanel panelMensaje = new JPanel(new GridBagLayout());
        panelMensaje.setBorder(BorderFactory.createTitledBorder("Últimos mensajes"));
        panelMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelMensaje.setMaximumSize(new Dimension(300, 300));

        // Configuracion de GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicializar y añadir las 4 filas de estado
        initializeStatusRow(panelMensaje, gbc, 0);
        initializeStatusRow(panelMensaje, gbc, 1);
        initializeStatusRow(panelMensaje, gbc, 2);
        initializeStatusRow(panelMensaje, gbc, 3);

        // Asignar los componentes creados a las variables de instancia.
        // Los componentes se añaden como: [Img1, Scroll1, Img2, Scroll2, ...]
        Component[] components = panelMensaje.getComponents();

        // Mapping Row 1
        labelFoto1 = (JLabel) components[0]; 
        labelMensaje1 = (JTextArea) components[1]; // Simplified casting
        // Mapping Row 2
        labelFoto2 = (JLabel) components[2];
        labelMensaje2 = (JTextArea) components[3]; // Simplified casting
        // Mapping Row 3
        labelFoto3 = (JLabel) components[4];
        labelMensaje3 = (JTextArea) components[5]; // Simplified casting
        // Mapping Row 4
        labelFoto4 = (JLabel) components[6];
        labelMensaje4 = (JTextArea) components[7]; // Simplified casting

        // Añadir el nuevo panel al panelDerecho
        panelDerecho.add(panelMensaje);

        // Espacio flexible para que el botón quede abajo
        panelDerecho.add(Box.createVerticalGlue());

        // Panel para centrar el botón tanto en X como en Y
        JPanel panelBoton = new JPanel(new GridBagLayout());
        JButton cerrarButton = new JButton("Cerrar");
        cerrarButton.addActionListener(e -> dispose());
        panelBoton.add(cerrarButton);
        panelDerecho.add(panelBoton, BorderLayout.SOUTH);

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

        pack(); // ajusta el tamaño de la ventana al contenido
        setLocationRelativeTo(null); // centra la ventana en pantalla
        setVisible(true);
    }

    /**
     * Helper method to create and add a single row (Image and Text) to the grid panel.
     */
    private void initializeStatusRow(JPanel parentPanel, GridBagConstraints gbc, int row) {
        // Image Label (Column 0) remains the same
        JLabel labelFoto = new JLabel(); 
        labelFoto.setPreferredSize(new Dimension(40, 40)); 
        labelFoto.setMinimumSize(new Dimension(40, 40));
        labelFoto.setHorizontalAlignment(SwingConstants.CENTER);
        labelFoto.setOpaque(true);
        labelFoto.setBackground(Color.LIGHT_GRAY);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.1; 
        gbc.fill = GridBagConstraints.BOTH; // Fill vertically is important for alignment
        parentPanel.add(labelFoto, gbc);

        // --- MODIFICATION START: JTextArea added directly ---
        JTextArea textAreaMensaje = new JTextArea("...");
        textAreaMensaje.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textAreaMensaje.setEditable(false);
        textAreaMensaje.setLineWrap(true);   // Text wrapping is still enabled
        textAreaMensaje.setWrapStyleWord(true);
        textAreaMensaje.setOpaque(false);

        // Crucial: Set its preferred size to zero for the height. 
        // This tells GridBagLayout to use the component's *natural* height (the wrapped text).
        // The width (250) is still constrained by the layout weight.
        textAreaMensaje.setPreferredSize(new Dimension(250, 0)); 

        // You should use setBorder(null) for the JTextArea, not the scroll pane.
        textAreaMensaje.setBorder(null); 

        // Add the JTextArea directly to the panel
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.9;
        gbc.fill = GridBagConstraints.BOTH; // Ensure it fills the cell
        parentPanel.add(textAreaMensaje, gbc);
        // --- MODIFICATION END ---
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
                        case SANTA ->
                            g.setColor(Color.GREEN);
                        case CAMINO ->
                            g.setColor(Color.LIGHT_GRAY);
                        case RENO ->
                            g.setColor(Color.BLUE);
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
        // Update the main agent status when the matrix changes
        //actualizarEstado(1, "Dirección: " + nuevaDireccionAgente.name(), AGENTE);
    }
    
    /**
     * Actualiza la imagen y el mensaje de estado en una fila específica del panel derecho.
     * @param row La fila a actualizar (1 a 4).
     * @param mensaje El texto de estado a mostrar.
     * @param imagenKey La clave de la imagen a mostrar (ej. AGENTE, SANTA, etc.).
     */
    public void actualizarEstado(int row, String mensaje, int imagenKey) {
        JLabel photoLabel = switch (row) {
            case 1 -> labelFoto1;
            case 2 -> labelFoto2;
            case 3 -> labelFoto3;
            case 4 -> labelFoto4;
            default -> {
                System.err.println("Fila de estado inválida: " + row);
                yield null;
            }
        };

        JTextArea textLabel = switch (row) {
            case 1 -> labelMensaje1;
            case 2 -> labelMensaje2;
            case 3 -> labelMensaje3;
            case 4 -> labelMensaje4;
            default -> null;
        };

        if (textLabel != null) {
            textLabel.setText(mensaje);
            textLabel.setForeground(imagenKey == SANTA ? new Color(0, 150, 0) : Color.BLACK);
        }
        
        if (photoLabel != null) {
            Image img = imagenes.get(imagenKey); 
            if (img == null && imagenKey == AGENTE) {
                 // If no general AGENT image, use the directional one
                 img = imagenesAgente.get(direccionAgente);
            }

            if (img != null) {
                // Scale the image to the fixed label size
                int w = photoLabel.getPreferredSize().width;
                int h = photoLabel.getPreferredSize().height;
                Image scaledImage = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaledImage));
                photoLabel.setText(null);
                photoLabel.setBackground(Color.WHITE);
            } else {
                photoLabel.setIcon(null); 
                photoLabel.setText("❓"); 
                photoLabel.setBackground(Color.LIGHT_GRAY);
            }
        }
    }

    public void mensajeAgente(String mensaje, String traza)
    {
        actualizarEstado(1, "Alumno: " + mensaje, AGENT_PICTURE);
        agregarTraza(traza);
    }
    
    public void mensajeRudolph(String mensaje, String traza)
    {
        actualizarEstado(2, "Empollon: " + mensaje, RENO_PICTURE);
        agregarTraza(traza); 
    }   
    
    public void mensajeElfo(String mensaje, String traza)
    {
        actualizarEstado(3, "Delegado: " + mensaje, ELFO_PICTURE);
        agregarTraza(traza); 
    }
    
    public void mensajeSanta(String mensaje, String traza)
    {
        actualizarEstado(4, "Profesor: " + mensaje, SANTA_PICTURE);
        agregarTraza(traza); 
    } 
    
    public void mostrarVentanaVictoria() {
        // Update the state before showing the message
        actualizarEstado(1, "¡OBJETIVO ALCANZADO!", SANTA); 
        JOptionPane.showMessageDialog(this, "¡El agente ha alcanzado el objetivo!", "Victoria", JOptionPane.INFORMATION_MESSAGE);
    }
}