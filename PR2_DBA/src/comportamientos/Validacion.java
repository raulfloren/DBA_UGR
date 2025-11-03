package comportamientos;

import agente.Agente;
import entorno.Entorno;
import jade.core.behaviours.Behaviour;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Validacion extends Behaviour {

    private final Agente agente;
    private Entorno entorno;
    //Sprivate Entorno entorno;

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
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Validacion.class.getName()).log(Level.SEVERE, null, ex);
        }

        entorno.setPosAgente(agente.getPosAgente(), agente.getPosAnterior());

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

De la lista de las casillas posibles que hemos obtenido con el comportamiento percepcion y el metodo verCasillasDisponibles()

Decidimos cual de esas casillas es la que mejor nos viene

 */
