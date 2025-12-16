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

        String mensaje = "Bro, no quedan mas apuntes. En plan.";

        switch (estados) {
            // Esperando a que el agente pida coordenadas
            case ESPERANDO_AL_SALVADOR -> {

                msgAgente = agenteRudolph.blockingReceive();

                if (msgAgente != null) {
                    // Comprobamos Performativa, Emisor, Contenido correcto y ConversationID correcto
                    if (msgAgente.getPerformative() == ACLMessage.QUERY_REF
                            && msgAgente.getSender().equals(agente)
                            && GestorComunicaciones.isCorrectMensajeAgente(msgAgente.getContent())
                            && msgAgente.getConversationId().equals(CLAVE_SECRETA_PARA_SALVAR_LA_NAVIDAD)) {

                        // Quedan coordenadas y codigo de converasion correcto
                        // Obtiene la posicion de un reno perdido
                        ACLMessage reply = msgAgente.createReply(ACLMessage.INFORM);

                        Posicion pos = new Posicion(agenteRudolph.getPosRenosPerdidos().getFirst());
                        agenteRudolph.getPosRenosPerdidos().removeFirst();

                        String coordenadas = "[" + pos.getFila() + "," + pos.getColumna() + "]";

                        // Comunica la posicion del reno a agente
                        reply.setContent("Bro, acepto. Las coordenadas son: " + coordenadas + ". En plan.");

                        agenteRudolph.send(reply);
                        agenteRudolph.getGraficos().mensajeRudolph(reply.getContent(), "Alumno Empollon envia INFORM a Alumno");

                        // Comprobar si ya no quedan más renos para cambiar de estado
                        if (agenteRudolph.getPosRenosPerdidos().isEmpty()) {
                            this.estados = EstadosRudolph.HA_SALVADO_LA_NAVIDAD;
                        }

                    } else {
                        // Si falla el emisor, el contenido, el ID de conversación o la performativa
                        enviarNotUnderstood(msgAgente);
                    }
                }
            }

            // El agente ha econtrado todos los renos
            case HA_SALVADO_LA_NAVIDAD -> {

                msgAgente = agenteRudolph.blockingReceive();

                if (msgAgente != null) {
                    // Solo contestamos con REFUSE si es una petición válida del Agente Salvador
                    if (msgAgente.getPerformative() == ACLMessage.QUERY_REF
                            && msgAgente.getSender().equals(agente)) {

                        ACLMessage reply = msgAgente.createReply();
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent(mensaje);

                        agenteRudolph.send(reply);

                        agenteRudolph.getGraficos().agregarTraza("Alumno Empollon envia REFUSE a Alumno");
                        agenteRudolph.getGraficos().mensajeRudolph(mensaje, "Alumno Empollon envia REFUSE a Alumno");

                        this.estados = EstadosRudolph.FIN_AGENTE;
                    } else {
                        // Si nos habla otro o nos dicen algo raro
                        enviarNotUnderstood(msgAgente);
                    }
                }
            }

            // Fin agente
            case FIN_AGENTE -> {
                agenteRudolph.doDelete();
            }

            default -> {

            }

        }

    }

    /**
     * Método para enviar un mensaje NOT_UNDERSTOOD
     */
    private void enviarNotUnderstood(ACLMessage msg) {
        if (msg != null) {
            System.out.println("Mensaje no entendido de: " + msg.getSender().getLocalName());
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            reply.setContent("No he entendido tu mensaje");
            agenteRudolph.send(reply);
        }
    }

    @Override
    public boolean done() {
        return this.fin;
    }
}
