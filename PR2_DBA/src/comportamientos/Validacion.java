package comportamientos;

import agente.Agente;
import jade.core.behaviours.Behaviour;

public class Validacion extends Behaviour {

    private final Agente agente;
    //Sprivate Entorno entorno;

    public Validacion(Agente agente) {
        this.agente = agente;
    }

    @Override
    public void action() {
        /*
        actualizar el entorno (cambio poscion agente)
        y mover agente en la gui
         */

    }

    @Override
    public boolean done() {
        return agente.objetivoEncontrado();
    }

}

/*

De la lista de las casillas posibles que hemos obtenido con el comportamiento percepcion y el metodo verCasillasDisponibles()

Decidimos cual de esas casillas es la que mejor nos viene

 */
