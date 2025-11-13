package comportamientos;

import agente.Agente;
import entorno.Entorno;
import jade.core.behaviours.Behaviour;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Validacion extends Behaviour {

    private final Agente agente;
    private Entorno entorno;

    public Validacion(Agente agente, Entorno entorno) {
        this.agente = agente;
        this.entorno = entorno;
    }

    @Override
    public void action() {
        /*
        actualizar el entorno (cambio poscion agente)
        y mover agente en la gui
         */

        // Pausar la simulación para poder verla
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            Logger.getLogger(Validacion.class.getName()).log(Level.SEVERE, null, ex);
        }

        entorno.setPosAgente(agente.getPosAgente(), agente.getPosAnterior());
        agente.imprimirMemoria();
        agente.getGUI().actualizarMatriz(entorno.getMapa().getMapa(), agente.getMovDecidido());

        // traza en GUI
        String traza = String.format("Pos: (%d, %d)\nAcción: %s\nEnergía: %d",
                agente.getPosAgente().getFila(), agente.getPosAgente().getColumna(),
                agente.getMovDecidido(), agente.getSensores().getEnergia());

        agente.getGUI().agregarTraza(traza);

    }

    @Override
    public boolean done() {
        if (agente.objetivoEncontrado()) {
            agente.getGUI().mostrarVentanaVictoria();
            return true;
        } else {
            return false;
        }
    }

}

/*

El movimiento tenia que ser valido pues para que se añadiera a la lista de casillas disponibles tenia que ser una casilla transitable

ahora bien, una vez hecho el movimiento se actualiza el entorno y la GUI y si se ha llegado al final se muestra la ventana


/////
La memoria del agente se actualizar´ aqui cuando se implemente


 */
