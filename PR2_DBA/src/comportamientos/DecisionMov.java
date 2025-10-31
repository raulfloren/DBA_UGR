package comportamientos;

import agente.Agente;
import jade.core.behaviours.Behaviour;

public class DecisionMov extends Behaviour {

    private final Agente agente;

    public DecisionMov(Agente agente) {
        this.agente = agente;
    }

    @Override
    public void action() {
        agente.decidirMov();
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
