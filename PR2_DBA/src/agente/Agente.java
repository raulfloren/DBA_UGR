package agente;


import comportamientos.hacerMov;
import jade.core.Agent;

/**
 *
 * @author floren
 */
public class Agente extends Agent {

    @Override
    protected void setup() {

        System.out.println("Soy agente: " + getAID().getLocalName());

        addBehaviour(new hacerMov(this));
        //doDelete();
    }

}
