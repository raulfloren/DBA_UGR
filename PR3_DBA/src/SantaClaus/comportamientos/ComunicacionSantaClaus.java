package SantaClaus.comportamientos;

import SantaClaus.SantaClaus;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import static SantaClaus.comportamientos.EstadosSantaClaus.*;
import herramientas.GestorAgentes;
import herramientas.GestorComunicaciones;

public class ComunicacionSantaClaus extends Behaviour {

    private final String CONVERSACION_AGENTE_SANTA_ID = "salvador-santa-conversacion";
    private final String CLAVE_SECRETA_PARA_SALVAR_LA_NAVIDAD = "Profee, apruebanos.";

    private SantaClaus agenteSanta;
    private EstadosSantaClaus estados;

    private boolean esValiente;

    private boolean fin;

    private AID agente, santaClaus;
    private ACLMessage msgAgente;

    public ComunicacionSantaClaus(SantaClaus agent) {
        super(agent);
        this.agenteSanta = agent;
        this.estados = ESPERANDO_VALIENTE;
        this.conocerAgentes();
    }

    private void conocerAgentes() {

        System.out.println("Soy santa buscando agentes: ");

        boolean todosLosAgentesRegistrados = false;
        AID[] agentes = null;

        while (!todosLosAgentesRegistrados) {

            // Buscar los agentes del DF
            agentes = GestorAgentes.buscarAgentes(this.agenteSanta, "PLAYER");

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

        System.out.println("Santa, encuentra: " + GestorAgentes.buscarAgenteEnLista(agentes, "salvador"));

        this.agente = GestorAgentes.buscarAgenteEnLista(agentes, "salvador");
    }

    @Override
    public void action() {
        String mensajeConfirm = "";

        switch (estados) {
            case ESPERANDO_VALIENTE -> {
                System.out.println("Esperando un valiente");

                msgAgente = agenteSanta.blockingReceive();

                System.out.println("Valiente encontrado");
                System.out.println(msgAgente);

                if (msgAgente != null && msgAgente.getPerformative() == ACLMessage.PROPOSE) {
                    if (msgAgente.getSender().equals(agente) && GestorComunicaciones.isCorrectMensajeSantaClaus(msgAgente.getContent())) {

                        esValiente = esValiente();
                        mensajeConfirm = GestorComunicaciones.SantaClausConfirmaDigno(esValiente, CLAVE_SECRETA_PARA_SALVAR_LA_NAVIDAD);

                        // Enviar PROPOSAL ACCEPT o REJECT al agente
                        msgAgente = new ACLMessage(esValiente ? ACLMessage.ACCEPT_PROPOSAL : ACLMessage.REJECT_PROPOSAL);

                        msgAgente.addReceiver(agente);
                        msgAgente.setContent(mensajeConfirm);

                        agenteSanta.send(msgAgente);
                        agenteSanta.getGraficos().agregarTraza("Santa Claus envía ACCEPT_PROPOSAL/REJECT_PROPOSAL a Agente");
                        estados = ESPERANDO_SOLICITUD_COORDENADAS;

                    } else {

                        System.out.println("No comprendo");
                    }
                } else {
                    System.out.println("No ha llegado nada");
                }
            }
            case ESPERANDO_SOLICITUD_COORDENADAS -> {
                msgAgente = agenteSanta.blockingReceive();

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
        return true;
        //return (((int) (Math.random() * 11)) < 8);
    }

    @Override
    public boolean done() {
        return this.fin;
    }
}
