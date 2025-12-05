package ElfoTraductor.comportamientos;

import ElfoTraductor.ElfoTraductor;
import herramientas.GestorComunicaciones;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ComunicacionElfoTraductor extends Behaviour {

    private final ElfoTraductor agenteElfo;

    private boolean fin;

    private AID agente;
    private ACLMessage msgAgente;

    public ComunicacionElfoTraductor(ElfoTraductor agent) {
        super(agent);
        this.agenteElfo = agent;
    }

    @Override
    public void action() {
        String mensajeTraducido = "";

        msgAgente = agenteElfo.blockingReceive(); //  Espera el mensaje del agente

        if (msgAgente != null && msgAgente.getPerformative() == ACLMessage.REQUEST) { // Mensaje del agente debe ser REQUEST

            if (!msgAgente.getSender().equals(agente)) {
                System.out.println("No me comunico contigo");
            } else { // El emisor debe ser el agente
                // El mensaje puede estar en dos idiomas, genz o fines, y quiero traducirlo al otro

                String idioma = msgAgente.getLanguage();
                if (null == idioma) {
                    // devolver con not understood
                } else // Traduccion del mensaje
                // Dos opciones
                {
                    switch (idioma) {
                        case "GenzZ" -> // GENZ-FINES
                            mensajeTraducido = GestorComunicaciones.traduceAgente_SantaClaus(msgAgente.getContent());
                        case "Fines" -> // FINES-GENZ
                            mensajeTraducido = GestorComunicaciones.traduceSantaClaus_Agente(msgAgente.getContent());
                        default -> {
                        }
                    }
                }

                // Enviar INFORM de vuelta al agente
                msgAgente = msgAgente.createReply(ACLMessage.INFORM);
                msgAgente.setContent(mensajeTraducido);
                agenteElfo.send(msgAgente);
                agenteElfo.getGraficos().agregarTraza(msgAgente.toString());

            }
        } else {
            System.out.println("Error esperando REQUEST en: " + agente.getLocalName()); // Not understood
        }
    }

    @Override
    public boolean done() {
        return this.fin;
    }
}
