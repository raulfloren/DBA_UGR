package comportamientos;

import agente.Agente;
import jade.core.behaviours.Behaviour;


public class HacerMov extends Behaviour {

    private final Agente agente;

    public HacerMov(Agente agente) {
        this.agente = agente;
    }

    @Override
    public void action() {
        agente.hacerMov();
    }

    @Override
    public boolean done() {
        return agente.objetivoEncontrado();
    }
}

/*

Hacemos el movimientoDecidido y aumentamos la energia con los sensores

*/
