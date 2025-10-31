package comportamientos;

import agente.Agente;
import jade.core.behaviours.Behaviour;

/**
 *
 * @author juanma
 */
public class Percepcion extends Behaviour {

    private final Agente agente;

    public Percepcion(Agente agente) {
        this.agente = agente;
    }

    @Override
    public void action() {
        agente.verCasillasDisponibles();
    }

    @Override
    public boolean done() {
        return agente.objetivoEncontrado();
    }
}

/*

El metodo see lo que hace es ver las casillas que podemos ver en la posicion actual en la que estamos


Con esa lista de casillas que tenemos, estudiamos que movimientos podemos hacer

 */
