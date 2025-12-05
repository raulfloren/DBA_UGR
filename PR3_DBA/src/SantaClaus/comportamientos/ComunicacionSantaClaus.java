package SantaClaus.comportamientos;

import SantaClaus.SantaClaus;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import SantaClaus.comportamientos.EstadosSantaClaus;
import static SantaClaus.comportamientos.EstadosSantaClaus.ESPERANDO_SOLICITUD_COORDENADAS;
import herramientas.GestorComunicaciones;

public class ComunicacionSantaClaus extends Behaviour {

    private final String CONVERSACION_AGENTE_SANTA_ID = "salvador-santa-conversacion";

    private SantaClaus agenteSanta;
    private EstadosSantaClaus estados;

    private boolean esValiente;

    private boolean fin;

    private AID agente, SantaClaus;
    private ACLMessage msgAgente, msgSanta;

    public ComunicacionSantaClaus(SantaClaus agent) {
        super(agent);
        this.agenteSanta = agent;
    }

    @Override
    public void action() {
        String mensajeConfirm = "";

        switch (estados) {
            case ESPERANDO_VALIENTE -> {
                msgAgente = agenteSanta.blockingReceive();

                if (msgAgente != null && msgAgente.getPerformative() == ACLMessage.PROPOSE) {
                    if (msgAgente.getSender().equals(agente)) {

                        esValiente = esValiente();
                        mensajeConfirm = GestorComunicaciones.SantaClausConfirmaDigno(esValiente, CONVERSACION_AGENTE_SANTA_ID);

                        // Enviar CONFIRM o DISCONFIRM al barco
                        msgSanta = new ACLMessage(ACLMessage.REQUEST);
                        msgSanta.addReceiver(agente);
                        msgSanta.setContent(mensajeConfirm);
                        msgSanta.setReplyWith("validation-request");
                        msgSanta.setConversationId(CONVERSACION_AGENTE_SANTA_ID);
                        agenteSanta.send(msgSanta);
                        agenteSanta.getGraficos().agregarTraza(msgSanta.toString());
                        estados = ESPERANDO_SOLICITUD_COORDENADAS;
                    } else {
                        System.out.println("No entiendo lo que me quieres decir, soy Jarl");
                    }
                } else {
                    System.out.println("No ha llegado nada");
                }
                // proposal accept / proposal reject

            }

            case ESPERANDO_SOLICITUD_COORDENADAS -> {
                // enviar nuestra coordenada
            }

            case ESPERANDO_SALVADOR_NAVIDAD -> {
                // Decir hohoho
            }

            default -> {
            }
        }

    }

    private boolean esValiente() {
        return (((int) (Math.random() * 11)) < 8);
    }

    @Override
    public boolean done() {
        return this.fin;
    }
}
