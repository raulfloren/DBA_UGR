package Rudolph.comportamientos;

import Rudolph.Rudolph;
import static Rudolph.comportamientos.EstadosRudolph.*;
import herramientas.GestorAgentes;
import herramientas.GestorComunicaciones;
import herramientas.Posicion;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ComunicacionRudolph extends Behaviour {

    private final String CLAVE_SECRETA_PARA_SALVAR_LA_NAVIDAD = "Profee, apruebanos.";
    private final String CONVERSACION_AGENTE_RUDOLPH_ID = "salvador-rudolph-conversacion";

    private final Rudolph agenteRudolph;
    private EstadosRudolph estados;

    private boolean fin;

    private AID agente;
    private ACLMessage msgAgente;

    public ComunicacionRudolph(Rudolph agent) {
        super(agent);
        this.agenteRudolph = agent;
        this.estados = ESPERANDO_AL_SALVADOR;
        this.conocerAgentes();
    }

    private void conocerAgentes() {

        System.out.println("Soy Rudolph buscando agentes: ");

        boolean todosLosAgentesRegistrados = false;
        AID[] agentes = null;

        while (!todosLosAgentesRegistrados) {

            // Buscar los agentes del DF
            agentes = GestorAgentes.buscarAgentes(this.agenteRudolph, "PLAYER");

            if (agentes.length == 1) { // Número esperado de servicios
                todosLosAgentesRegistrados = true;
            } else {
                try {
                    Thread.sleep(100); // Esperar 1 segundo antes de volver a buscar
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        System.out.println("Rudolph, encuentra: " + GestorAgentes.buscarAgenteEnLista(agentes, "salvador"));

        this.agente = GestorAgentes.buscarAgenteEnLista(agentes, "salvador");
    }

    @Override
    public void action() {

        String mensaje = "";

        switch (estados) {
            case ESPERANDO_AL_SALVADOR -> {
                System.out.println("Esperando rudolph");

                msgAgente = agenteRudolph.blockingReceive();

                System.out.println("Recibido rudolph");
                System.out.println(msgAgente);

                String posReno = "Bro, no quedan renos perdidos. En plan.";

                if (msgAgente != null && msgAgente.getPerformative() == ACLMessage.QUERY_REF) {

                    if (msgAgente.getSender().equals(agente) && GestorComunicaciones.isCorrectMensajeAgente(msgAgente.getContent())) {

                        if (!msgAgente.getConversationId().equals(CLAVE_SECRETA_PARA_SALVAR_LA_NAVIDAD)) {      // Si no es el codigo correcto
                            msgAgente = msgAgente.createReply(ACLMessage.REFUSE);
                            msgAgente.setContent("Bro, que me esta contando. En plan.");

                        } else {
                            msgAgente = msgAgente.createReply(ACLMessage.INFORM);

                            if (agenteRudolph.getPosRenosPerdidos().isEmpty()) {     // No quedan coordenadas
                                msgAgente.setContent(posReno);

                            } else {      // Quedan coordenadas y codigo de converasion correcto
                                // Obtiene la posicion de un reno perdido
                                Posicion pos = new Posicion(agenteRudolph.getPosRenosPerdidos().getFirst());
                                System.out.println("QUEDAN " + agenteRudolph.getPosRenosPerdidos().size()+ " =======================================================================================================================================");
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

            }

            default -> {

            }

        }

    }

    @Override
    public boolean done() {
        return this.fin;
    }
}
