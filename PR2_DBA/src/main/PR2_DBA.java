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
 */
public class PR2_DBA {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Prueba: <nombre_mapa> <agente_pos_x>  <agente_pos_y> <objectivo_pos_x>  <objectivo_pos_y>");
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
            int agente_X = Integer.parseInt(args[1]);
            int agente_Y = Integer.parseInt(args[2]);
            int objetivo_X = Integer.parseInt(args[3]);
            int objetivo_Y = Integer.parseInt(args[4]);

            Posicion agentPos = new Posicion(agente_X, agente_Y);
            Posicion goalPos = new Posicion(objetivo_X, objetivo_Y);

            // --- Crear entorno ---
            Entorno entorno = new Entorno(mapa, agentPos, goalPos);

            // --- Crear interfaz gráfica ---
            SimulacionAgenteGUI visualizer = new SimulacionAgenteGUI(
                    "Simulación desde archivo: " + nombreMapa,
                    mapa.getMapa(),
                    Movimientos.AR
            );
            visualizer.setVisible(true);

            // --- Crear contenedor secundario ---
            Profile agentProfile = new ProfileImpl();
            ContainerController agentContainer = jadeRuntime.createAgentContainer(agentProfile);

            // --- Crear agente (por ahora sin clase específica) ---
            String claseAgente = "agente.Agente";
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
