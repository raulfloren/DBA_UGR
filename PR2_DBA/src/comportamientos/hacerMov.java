package comportamientos;

import agente.Agente;
import agente.Posicion;
import jade.core.behaviours.Behaviour;

/**
 *
 * @author floren
 */
public class hacerMov extends Behaviour {

    private Agente agente;
    private Posicion posAgent;

    public hacerMov(Agente a) {
        super(a);
    }

    // Metodos
    @Override
    public void action() {
        //move();
    }
/*
    public void move() {
        agente
        
        switch (this.movDecidido) {
            case ARR:
                posAgente.setFila(posAgente.getRow() - 1);  // Arriba
                break;
            case ABA:
                posAgente.setFila(posAgente.getFila() + 1);  // Abajo
                break;
            case IZQ:
                posAgente.setCol(posAgente.getCol() - 1);  // Izquierda
                break;
            case DER:
                posAgente.setCol(posAgente.getCol() + 1);  // Derecha
                break;
            case ARRIZQ:
                posAgente.setFila(posAgente.getFila() - 1);  // Arriba-Izquierda
                posAgente.setCol(posAgente.getCol() - 1);
                break;
            case ARRDER:
                posAgente.setFila(posAgente.getFila() - 1);  // Arriba-Derecha
                posAgente.setCol(posAgente.getCol() + 1);
                break;
            case ABAIZQ:
                posAgente.setFila(posAgente.getFila() + 1);  // Abajo-Izquierda
                posAgente.setCol(posAgente.getCol() - 1);
                break;
            case ABADER:
                posAgente.setFila(posAgente.getFila() + 1);  // Abajo-Derecha
                posAgente.setCol(posAgente.getCol() + 1);
                break;
        }
    }
*/
    @Override
    public boolean done() {
       
        return false;
       
    }

}
