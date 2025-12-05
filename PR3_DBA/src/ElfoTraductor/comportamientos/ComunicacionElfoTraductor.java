package ElfoTraductor.comportamientos;

import ElfoTraductor.ElfoTraductor;
import herramientas.GestorAgentes;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ComunicacionElfoTraductor extends Behaviour {

    private final String CONVERSACION_AGENTE_ELFO_ID = "salvador-elfo-traductor-conversacion";

    private ElfoTraductor agenteElfo;
    private EstadosElfoTraductor estados;

    private boolean fin;

    private AID agente;
    private ACLMessage msgAgente;

    public ComunicacionElfoTraductor(ElfoTraductor agent) {
        super(agent);
        this.agenteElfo = agent;
        this.estados = EstadosElfoTraductor.ESPERANDO_A_TRADUCIR;
    }

    @Override
    public void action() {
        String mensajeTraducido;

        switch (estados) {
            
            case ESPERANDO_A_TRADUCIR:
                              
                msgAgente = agente.blockingReceive(); //  Espera el mensaje del agente

                if (msgBarco != null && msgBarco.getPerformative() == ACLMessage.REQUEST) {

                    if (msgBarco.getSender().equals(barco)) {

                        // Traduccion del mensaje
                        mensajeTraducido = GestorComunicacion.traduceBarcoJarl(msgBarco.getContent());

                        // Enviar INFORM al barco vikingo
                        msgBarco = msgBarco.createReply(ACLMessage.INFORM);
                        msgBarco.setContent(mensajeTraducido);
                        agente.send(msgBarco);
                        agente.getGraficos().agregarTraza(msgBarco.toString());
                        this.paso = EstadosSkal.ESPERANDO_MENSAJE_JARL;
                    } else {
                        System.out.println("No entiendo lo que me quieres decir soy Skal");
                    }
                } else {
                    System.out.println("Error esperando REQUEST en: " + agente.getLocalName());
                }

                break;

            case ESPERANDO_MENSAJE_JARL:
                msgJarl = agente.blockingReceive();

                if (msgJarl != null && msgJarl.getPerformative() == ACLMessage.REQUEST) {

                    if (msgJarl.getSender().equals(jarl)) {
                        // Traduccion del mensaje
                        mensajeTraducido = GestorComunicacion.traduceJarlBarco(msgJarl.getContent());

                        // Enviar INFORM al barco vikingo
                        msgJarl = msgJarl.createReply(ACLMessage.INFORM);
                        msgJarl.setContent(mensajeTraducido);
                        agente.send(msgJarl);
                        agente.getGraficos().agregarTraza(msgJarl.toString());
                        this.paso = EstadosSkal.ESPERANDO_MENSAJE_BARCO;
                    } else {
                        System.out.println("No entiendo lo que me quieres decir, soy Skal");
                    }
                } else {
                    System.out.println("Error esperando REQUEST en: " + agente.getLocalName());
                }

                break;

            default:
                System.out.println("[Skal] Error: Estado desconocido.");
                myAgent.doDelete();
                break;
        }

    }

    @Override
    public boolean done() {
        return this.fin;
    }
}
