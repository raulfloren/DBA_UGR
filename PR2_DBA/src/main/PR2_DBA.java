package main;

import GUI.SimulacionAgenteGUI;
import movimientos.Movimientos;
import agente.Posicion;
import entorno.Entorno;
import entorno.Mapa;

//JADE
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 *
 * @author floren
 * @author juanma
 */
public class PR2_DBA {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Prueba: <nombre_mapa> <agente_pos_x>  <agente_pos_y> <objectivo_pos_x>  <objectivo_pos_y>");
            return; // <-- Detener ejecución si faltan argumentos
        }

        try {
            // --- Iniciar JADE ---
            Runtime jadeRuntime = Runtime.instance();
            Profile mainProfile = new ProfileImpl();
            mainProfile.setParameter(Profile.GUI, "false");  // No mostrar GUI de JADE
            AgentContainer mainContainer = jadeRuntime.createMainContainer(mainProfile);

            System.out.println("🟢 Main container de JADE iniciado...");

            // --- Crear mapa ---
            String nombreMapa = args[0];
            Mapa mapa = new Mapa(nombreMapa);

            // --- Crear posiciones ---
            int agente_X = Integer.parseInt(args[1]); // Columna
            int agente_Y = Integer.parseInt(args[2]); // Fila
            int objetivo_X = Integer.parseInt(args[3]); // Columna
            int objetivo_Y = Integer.parseInt(args[4]); // Fila

            // <-- El constructor de Posicion es (fila, columna)(corregido)
            Posicion agentPos = new Posicion(agente_Y, agente_X);
            Posicion goalPos = new Posicion(objetivo_Y, objetivo_X);

            // --- Crear entorno ---
            Entorno entorno = new Entorno(mapa, agentPos, goalPos);

            // --- Crear interfaz gráfica ---
            SimulacionAgenteGUI visualizer = new SimulacionAgenteGUI(
                    "Simulación desde archivo: " + nombreMapa,
                    mapa.getMapa(),
                    Movimientos.UP // <-- Cambiado de AR a UP(corregido)
            );
            visualizer.setVisible(true);

            // --- Crear contenedor secundario ---
            Profile agentProfile = new ProfileImpl();
            ContainerController agentContainer = jadeRuntime.createAgentContainer(agentProfile);

            // --- Crear agente ---
            String claseAgente = "agente.Agente";
            // Pasamos el Entorno y el Visualizer al agente
            Object[] argsAgente = new Object[]{entorno, visualizer}; 

            AgentController agente = agentContainer.createNewAgent("AgenteInteligente", claseAgente, argsAgente);

            // --- Iniciar agente ---
            agente.start();
            System.out.println("🤖 Agente iniciado correctamente.");

        } catch (StaleProxyException e) {
            e.printStackTrace();
            System.err.println("❌ Error al crear o iniciar el agente JADE.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}