package Rudolph.comportamientos;

import Rudolph.Rudolph;
import herramientas.GestorAgentes;
import herramientas.GestorComunicaciones;
import herramientas.Posicion;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ComunicacionRudolph extends Behaviour {

    private final String CONVERSACION_AGENTE_RUDOLPH_ID = "salvador-rudolph-conversacion";

    private final Rudolph agenteRudolph;

    private boolean fin;

    private AID agente;
    private ACLMessage msgAgente;

    public ComunicacionRudolph(Rudolph agent) {
        super(agent);
        this.agenteRudolph = agent;
        this.doHandshake();
    }

    private void doHandshake() {
        boolean conexionEstablecidaAgente = false;
        AID[] agentes = null;

        while (!conexionEstablecidaAgente) {

            // Buscar los agentes de tipo PLAYER (solo hay uno)
            agentes = GestorAgentes.buscarAgentes(this.agenteRudolph, "PLAYER");
            if (agentes.length == 1) { // Número esperado de servicios
                conexionEstablecidaAgente = true;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        this.agente = GestorAgentes.buscarAgenteEnLista(agentes, "salvador");

    }

    @Override
    public void action() {

        msgAgente = agenteRudolph.blockingReceive();
        String posReno = "Bro, no quedan renos perdidos. En plan.";

        if (msgAgente != null && msgAgente.getPerformative() == ACLMessage.REQUEST) {

            if (msgAgente.getSender().equals(agente) && GestorComunicaciones.isCorrectMensajeAgente(msgAgente.getContent())) {

                if (!msgAgente.getConversationId().equals(CONVERSACION_AGENTE_RUDOLPH_ID)) {      // Si no es el codigo correcto
                    msgAgente = msgAgente.createReply(ACLMessage.NOT_UNDERSTOOD);
                    msgAgente.setContent("Bro, no puedo ayudarte. En plan.");

                } else if (agenteRudolph.getPosRenosPerdidos().isEmpty()) {     // No quedan coordenadas
                    msgAgente = msgAgente.createReply(ACLMessage.REFUSE);
                    msgAgente.setContent(posReno);

                } else {      // Quedan coordenadas y codigo de converasion correcto
                    msgAgente = msgAgente.createReply(ACLMessage.AGREE);

                    // Obtiene la posicion de un reno perdido
                    Posicion pos = new Posicion(agenteRudolph.getPosRenosPerdidos().getFirst());
                    agenteRudolph.getPosRenosPerdidos().removeFirst();
                    posReno = "[" + pos.getFila() + "," + pos.getColumna() + "]";

                    // Comunica la posicion del reno a agente
                    msgAgente.setContent("Bro, acepto. Las coordenadas son: " + posReno + ". En plan.");
                }

                agenteRudolph.send(msgAgente);
                agenteRudolph.getGraficos().agregarTraza(msgAgente.toString());
            }
        }
    }

    @Override
    public boolean done() {
        return this.fin;
    }
}
