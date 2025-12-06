package ElfoTraductor.comportamientos;

import ElfoTraductor.ElfoTraductor;
import herramientas.GestorAgentes;
import herramientas.GestorComunicaciones;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ComunicacionElfoTraductor extends Behaviour {

    private ElfoTraductor agenteElfo;

    private boolean fin;

    private AID agente;
    private ACLMessage msgAgente;

    public ComunicacionElfoTraductor(ElfoTraductor agent) {
        super(agent);
        this.agenteElfo = agent;
        this.conocerAgentes();
    }

    private void conocerAgentes() {

        System.out.println("Soy elfotraductor buscando agentes: ");

        boolean todosLosAgentesRegistrados = false;
        AID[] agentes = null;

        while (!todosLosAgentesRegistrados) {

            // Buscar los agentes del DF. 
            // En este caso va a buscar el agente explorador que solo hay 1 
            agentes = GestorAgentes.buscarAgentes(this.agenteElfo, "PLAYER");

            if (agentes.length == 1) { // Número esperado de servicios
                todosLosAgentesRegistrados = true;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        System.out.println("Elfo, encuentra: " + GestorAgentes.buscarAgenteEnLista(agentes, "salvador"));

        this.agente = GestorAgentes.buscarAgenteEnLista(agentes, "salvador");
    }

    @Override
    public void action() {
        String mensajeTraducido = "";

        System.out.println("Esperando elfo");

        msgAgente = agenteElfo.blockingReceive(); //  Espera el mensaje del agente

        System.out.println("Recibido elfo");
        System.out.println(msgAgente);

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
                    switch (idioma.toUpperCase().trim()) {
                        case "GENZ" -> // GENZ-FINES
                        {
                            System.out.println("Traduciendo a Fines");
                            mensajeTraducido = GestorComunicaciones.traduceAgente_SantaClaus(msgAgente.getContent());

                        }
                        case "FINES" -> // FINES-GENZ
                        {
                            System.out.println("Traduciendo a GenZ");
                            mensajeTraducido = GestorComunicaciones.traduceSantaClaus_Agente(msgAgente.getContent());
                        }
                        default -> {
                        }
                    }
                }

                // Enviar INFORM de vuelta al agente
                msgAgente = msgAgente.createReply(ACLMessage.INFORM);
                msgAgente.setContent(mensajeTraducido);

                agenteElfo.send(msgAgente);
                agenteElfo.getGraficos().agregarTraza("Elfo traductor envía INFORM a Agente");

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
