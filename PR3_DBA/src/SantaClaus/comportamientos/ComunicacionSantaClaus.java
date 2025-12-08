package SantaClaus.comportamientos;

import SantaClaus.SantaClaus;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import herramientas.GestorAgentes;
import herramientas.GestorComunicaciones;
import entorno.Entorno;
import herramientas.Posicion;

public class ComunicacionSantaClaus extends Behaviour {

    private Posicion posSanta;

    private final String CONVERSACION_AGENTE_SANTA_ID = "salvador-santa-conversacion";
    private final String CLAVE_SECRETA_PARA_SALVAR_LA_NAVIDAD = "Profee, apruebanos.";

    private SantaClaus agenteSanta;
    private EstadosSantaClaus estados;

    private boolean esValiente;

    private boolean fin;

    private AID agente, santaClaus;
    private ACLMessage msgAgente;

    public ComunicacionSantaClaus(SantaClaus agent, Posicion posSanta) {
        super(agent);
        this.agenteSanta = agent;
        this.posSanta = posSanta;
        this.estados = EstadosSantaClaus.ESPERANDO_VALIENTE;
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
        String mensaje;

        switch (estados) {
            // Alguien debe salvar la navidad
            case ESPERANDO_VALIENTE -> {

                msgAgente = agenteSanta.blockingReceive();

                if (msgAgente != null && msgAgente.getPerformative() == ACLMessage.PROPOSE) {
                    if (msgAgente.getSender().equals(agente) && GestorComunicaciones.isCorrectMensajeSantaClaus(msgAgente.getContent())) {

                        esValiente = esValiente();
                        mensaje = GestorComunicaciones.SantaClausConfirmaDigno(esValiente, CLAVE_SECRETA_PARA_SALVAR_LA_NAVIDAD);

                        // Enviar PROPOSAL ACCEPT o REJECT al agente
                        msgAgente = new ACLMessage(esValiente ? ACLMessage.ACCEPT_PROPOSAL : ACLMessage.REJECT_PROPOSAL);

                        msgAgente.addReceiver(agente);
                        msgAgente.setContent(mensaje);

                        agenteSanta.send(msgAgente);
                        agenteSanta.getGraficos().agregarTraza("Profesor envía " + (esValiente ? "ACCEPT_PROPOSAL" : "REJECT_PROPOSAL") + " a Alumno");
                        this.estados = EstadosSantaClaus.ESPERANDO_SOLICITUD_COORDENADAS;

                    } else {

                        System.out.println("No comprendo");
                    }
                } else {
                    System.out.println("No ha llegado nada");
                }
            }

            // El agente ha encontrado todos los renos
            case ESPERANDO_SOLICITUD_COORDENADAS -> {

                msgAgente = agenteSanta.blockingReceive();

                if (msgAgente != null && msgAgente.getPerformative() == ACLMessage.REQUEST) {
                    if (msgAgente.getSender().equals(agente) && GestorComunicaciones.isCorrectMensajeSantaClaus(msgAgente.getContent())) {
                        // enviar nuestra coordenada

                        msgAgente = msgAgente.createReply(ACLMessage.INFORM);
                        mensaje = "Hyvää joulua, [" + posSanta.getFila() + "," + posSanta.getColumna() + "]. Nähdään pian.";
                        msgAgente.setContent(mensaje);

                        agenteSanta.send(msgAgente);
                        agenteSanta.getGraficos().agregarTraza("Profesor envía INFORM a Alumno");
                        this.estados = EstadosSantaClaus.ESPERANDO_SALVADOR_NAVIDAD;

                    }
                }

            }

            // Decir HoHoHo
            case ESPERANDO_SALVADOR_NAVIDAD -> {

                msgAgente = agenteSanta.blockingReceive();

                if (msgAgente != null && msgAgente.getPerformative() == ACLMessage.REQUEST) {
                    if (msgAgente.getSender().equals(agente) && GestorComunicaciones.isCorrectMensajeSantaClaus(msgAgente.getContent())) {

                        msgAgente = new ACLMessage(ACLMessage.INFORM);
                        mensaje = "Hyvää joulua, HoHoHo APROBASTE!!. Nähdään pian.";
                        msgAgente.addReceiver(agente);
                        msgAgente.setContent(mensaje);

                        agenteSanta.send(msgAgente);
                        agenteSanta.getGraficos().agregarTraza("Profesor envía INFORM a Alumno");
                        this.estados = EstadosSantaClaus.FIN_AGENTE;

                    }
                }

            }

            case FIN_AGENTE -> {
                agenteSanta.doDelete();
            }

            default -> {
            }
        }

    }

    private boolean esValiente() {
        //return true;
        return (((int) (Math.random() * 11)) < 8);
    }

    @Override
    public boolean done() {
        return this.fin;
    }
}
