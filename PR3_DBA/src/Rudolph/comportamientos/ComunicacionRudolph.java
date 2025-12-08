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

        String mensaje = "Bro, no quedan mas apuntes. En plan.";

        switch (estados) {
            // Esperando a que el agente pida coordenadas
            case ESPERANDO_AL_SALVADOR -> {

                msgAgente = agenteRudolph.blockingReceive();

                if (msgAgente != null && msgAgente.getPerformative() == ACLMessage.QUERY_REF) {

                    if (msgAgente.getSender().equals(agente) && GestorComunicaciones.isCorrectMensajeAgente(msgAgente.getContent())) {

                        if (!msgAgente.getConversationId().equals(CLAVE_SECRETA_PARA_SALVAR_LA_NAVIDAD)) {      // Si no es el codigo correcto
                            msgAgente = msgAgente.createReply(ACLMessage.NOT_UNDERSTOOD);
                            msgAgente.setContent("Bro, que me esta contando. En plan.");
                            agenteRudolph.getGraficos().mensajeRudolph(msgAgente.getContent(), "Alumno Empollon envia NOT_UNDERSTOOD a Alumno");

                        } else { // Si el codigo es correcto

                            // Quedan coordenadas y codigo de converasion correcto
                            // Obtiene la posicion de un reno perdido
                            msgAgente = msgAgente.createReply(ACLMessage.INFORM);

                            Posicion pos = new Posicion(agenteRudolph.getPosRenosPerdidos().getFirst());
                            agenteRudolph.getPosRenosPerdidos().removeFirst();

                            mensaje = "[" + pos.getFila() + "," + pos.getColumna() + "]";

                            // Comunica la posicion del reno a agente
                            msgAgente.setContent("Bro, acepto. Las coordenadas son: " + mensaje + ". En plan.");
                            agenteRudolph.getGraficos().mensajeRudolph(msgAgente.getContent(), "Alumno Empollon envia INFORM a Alumno");
                        }

                        agenteRudolph.send(msgAgente);

                        if (agenteRudolph.getPosRenosPerdidos().isEmpty()) {     // No quedan coordenadas
                            this.estados = EstadosRudolph.HA_SALVADO_LA_NAVIDAD;
                        }

                    }
                }

            }

            // El agente ha econtrado todos los renos
            case HA_SALVADO_LA_NAVIDAD -> {

                msgAgente = agenteRudolph.blockingReceive();

                msgAgente = msgAgente.createReply(ACLMessage.REFUSE);
                msgAgente.setContent(mensaje);
                agenteRudolph.send(msgAgente);
                agenteRudolph.getGraficos().agregarTraza("Alumno Empollon envia REFUSE a Alumno");
                agenteRudolph.getGraficos().mensajeRudolph(mensaje, "Alumno Empollon envia REFUSE a Alumno");
                this.estados = EstadosRudolph.FIN_AGENTE;

            }

            // Fin agente
            case FIN_AGENTE -> {
                agenteRudolph.doDelete();
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
