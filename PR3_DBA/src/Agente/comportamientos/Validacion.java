package Agente.comportamientos;

import Agente.Agente;
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

        if (agente.getPosObjetivo() != null) {

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

            if (this.agente.objetivoEncontrado()) {
                this.agente.notificarRenoEncontrado();
                this.agente.cleanMemoria();

            }
        }

    }

    @Override
    public boolean done() {
        if (agente.navidadSalvada()) {
            agente.getGUI().mostrarVentanaVictoria();
            return true;
        } else {
            return false;
        }
    }

}
