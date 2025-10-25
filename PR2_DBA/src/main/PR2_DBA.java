package main;

import GUI.AgentSimulationGUI;
import action.Actions;
import environment.MapLoader;
import java.io.IOException;

/**
 *
 * @author floren
 */
public class PR2_DBA {

    public static void main(String[] args) {
        if (args.length != 1) { // De momento solo esta el map_name
            System.err.println("Try: <map_name> <agent_pos_x>  <agent_pos_y> <objective_pos_x>  <objective_pos_y>");
        }

        String mapName = "resources/maps/" + args[0];
        
        //Esto es una prueba de pr

        try {
            // Cargar mapa desde archivo
            int[][] mapa = MapLoader.loadMap(mapName);

            // Crear ventana del simulador
            AgentSimulationGUI visualizer = new AgentSimulationGUI("Simulación desde archivo", mapa, Actions.AR);
            visualizer.setVisible(true);

        } catch (IOException e) {
            System.err.println("Error cargando el mapa: " + e.getMessage());
        }
    }
}
