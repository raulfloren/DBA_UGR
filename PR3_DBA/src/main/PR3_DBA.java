/*
https://www.google.com/search?q=salvador+persona&client=ubuntu-sn&hs=mMh&sca_esv=267665e6926584fe&channel=fs&udm=2&biw=1920&bih=934&sxsrf=AE3TifOu14bGIQ847u9hijI_qyNYiXn_UQ%3A1764331152250&ei=kI4paeb0DvKF9u8Pm8z1oQE&ved=0ahUKEwjm_OHc5ZSRAxXygv0HHRtmPRQQ4dUDCBI&uact=5&oq=salvador+persona&gs_lp=Egtnd3Mtd2l6LWltZyIQc2FsdmFkb3IgcGVyc29uYTIFEAAYgAQyBRAAGIAEMgUQABiABDIEEAAYHjIEEAAYHjIGEAAYBRgeMgYQABgFGB4yBhAAGAUYHjIGEAAYBRgeMgYQABgFGB5IzAtQ3AJY2gpwAXgAkAEAmAHfAaAB4QaqAQUwLjYuMbgBA8gBAPgBAZgCCKACtwfCAg0QABiABBixAxhDGIoFwgIGEAAYBxgewgIKEAAYgAQYQxiKBcICCBAAGIAEGLEDwgILEAAYgAQYsQMYgwHCAgYQABgKGB6YAwCIBgGSBwUxLjYuMaAHqyiyBwUwLjYuMbgHsAfCBwUyLTIuNsgHQQ&sclient=gws-wiz-img#vhid=pggvRCzm4zpHkM&vssid=mosaic
*/
package main;

import GUI.SimulacionAgenteGUI;
import movimientos.Movimientos;
import herramientas.Posicion;
import herramientas.Sensores;
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

public class PR3_DBA {

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

            // --- El constructor de Posicion es (fila, columna)
            Posicion posAgente = new Posicion(agente_Y, agente_X);
            Posicion posObjetivo = new Posicion(objetivo_Y, objetivo_X);

            // --- Crear entorno ---
            Entorno entorno = new Entorno(mapa, posAgente, posObjetivo);

            // --- Crear sensores ---
            Sensores sensores = new Sensores(entorno, 0);

            // --- Crear interfaz gráfica ---
            SimulacionAgenteGUI GUI = new SimulacionAgenteGUI(
                    "Simulación desde archivo: " + nombreMapa,
                    mapa.getMapa(),
                    Movimientos.UP // <-- Cambiado de AR a UP(corregido)
            );
            GUI.setVisible(true);

            // --- Crear agente ---
            String claseAgente = "agente.Agente";

            // --- Pasamos el Entorno y el Visualizer al agente ---
            Object[] argsAgente = new Object[]{posAgente, posObjetivo, sensores, GUI};

            // --- Crear contenedor secundario ---
            Profile agentProfile = new ProfileImpl();
            ContainerController agentContainer = jadeRuntime.createAgentContainer(agentProfile);

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
