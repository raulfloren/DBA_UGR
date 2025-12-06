package Agente.comportamientos;

import Agente.Agente;
import static Agente.comportamientos.EstadosAgente.*;
import herramientas.GestorAgentes;
import herramientas.GestorComunicaciones;
import herramientas.Posicion;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ComunicacionAgente extends Behaviour {

    private final String CONVERSACION_AGENTE_ELFO_ID = "salvador-elfo-conversacion";
    private String CODIGO_SECRETO_SANTA_CONVERSACION_ID;
    private Agente agenteSalvador;
    private EstadosAgente estados;

    private boolean fin;

    private AID elfoTraductor, santaClaus, rudolph;
    private ACLMessage msgElfo, msgSanta, msgRudolph;

    public ComunicacionAgente(Agente agent) {
        super(agent);
        this.agenteSalvador = agent;
        this.estados = INICIO_MISION_SALVAR_NAVIDAD;
        this.conocerAgentes();
    }

    private void conocerAgentes() {

        System.out.println("Soy agente buscando agentes: ");

        boolean todosLosAgentesRegistrados = false;
        AID[] agentes = null;

        while (!todosLosAgentesRegistrados) {

            // Buscar los agentes del DF
            agentes = GestorAgentes.buscarAgentes(this.agenteSalvador, "NPC");

            if (agentes.length == 3) { // Número esperado de servicios
                todosLosAgentesRegistrados = true;
            } else {
                try {
                    Thread.sleep(100); // Esperar 1 segundo antes de volver a buscar
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        this.elfoTraductor = GestorAgentes.buscarAgenteEnLista(agentes, "elfoTraductor");
        this.santaClaus = GestorAgentes.buscarAgenteEnLista(agentes, "santaClaus");
        this.rudolph = GestorAgentes.buscarAgenteEnLista(agentes, "rudolph");
    }

    @Override
    public void action() {
        String mensaje = "";

        switch (estados) {
            case INICIO_MISION_SALVAR_NAVIDAD -> {
                msgElfo = new ACLMessage(ACLMessage.REQUEST);

                mensaje = "Bro, voy a salvar la navida confia. En plan.";

                msgElfo.addReceiver(elfoTraductor);
                msgElfo.setLanguage("GenZ");
                msgElfo.setContent(mensaje);
                msgElfo.setConversationId(CONVERSACION_AGENTE_ELFO_ID);
                agenteSalvador.send(msgElfo);
                agenteSalvador.getGUI().agregarTraza("Agente envía REQUEST a Elfo Traductor");

                estados = ESPERANDO_TRADUCCION_ELFO_1;
            }

            case ESPERANDO_TRADUCCION_ELFO_1 -> {

                System.out.println("Esperando traduccion elfo");

                msgElfo = agenteSalvador.blockingReceive();

                System.out.println("Recibida traduccion elfo");
                System.out.println(msgElfo);

                if (msgElfo != null && msgElfo.getPerformative() == ACLMessage.INFORM) {

                    if (msgElfo.getSender().equals(elfoTraductor)) {

                        // Traduccion del mensaje
                        mensaje = msgElfo.getContent();

                        // Enviar PROPOSAL a SantaClaus
                        msgSanta = new ACLMessage(ACLMessage.PROPOSE);
                        msgSanta.addReceiver(this.santaClaus);
                        msgSanta.setContent(mensaje);

                        agenteSalvador.send(msgSanta);
                        agenteSalvador.getGUI().agregarTraza("Agente envía PROPOSE a Santa Claus");
                        estados = EstadosAgente.ESPERANDO_CONFIRMACION_SANTA;

                    } else {
                        System.out.println("No entiendo lo que me quieres decir");
                    }
                } else {
                    System.out.println("Error esperando INFORM en: " + agenteSalvador.getLocalName());
                }

                this.estados = ESPERANDO_CONFIRMACION_SANTA;

            }

            case ESPERANDO_CONFIRMACION_SANTA -> {
                System.out.println("Esperando confirmacion santa");

                msgSanta = agenteSalvador.blockingReceive();

                System.out.println("Confirmacion santa");

                System.out.println(msgSanta);

                if (msgSanta != null && msgSanta.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {

                    if (msgSanta.getSender().equals(santaClaus) && GestorComunicaciones.isCorrectMensajeSantaClaus(msgSanta.getContent())) {

                        mensaje = msgSanta.getContent();

                        msgElfo = new ACLMessage(ACLMessage.REQUEST);
                        msgElfo.addReceiver(elfoTraductor);
                        msgElfo.setLanguage("Fines");
                        msgElfo.setContent(mensaje);
                        agenteSalvador.send(msgElfo);
                        agenteSalvador.getGUI().agregarTraza("Agente envía REQUEST a Elfo Traductor");

                        estados = ESPERANDO_TRADUCCION_ELFO_2;
                    }
                } else if (msgSanta != null && msgSanta.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                    if (msgSanta.getSender().equals(santaClaus) && GestorComunicaciones.isCorrectMensajeSantaClaus(msgSanta.getContent())) {
                        agenteSalvador.doDelete();
                    }
                }
            }

            case ESPERANDO_TRADUCCION_ELFO_2 -> {

                System.out.println("Esperando traduccion elfo");

                msgElfo = agenteSalvador.blockingReceive();

                System.out.println("Recibida traduccion elfo");
                System.out.println(msgElfo);

                if (msgElfo != null && msgElfo.getPerformative() == ACLMessage.INFORM) {

                    if (msgElfo.getSender().equals(elfoTraductor)) {

                        // Obtener codigo
                        CODIGO_SECRETO_SANTA_CONVERSACION_ID = GestorComunicaciones.obtenerCodigoSecreto(msgElfo.getContent());

                        System.out.println("CODIGO SECRETO: " + CODIGO_SECRETO_SANTA_CONVERSACION_ID);

                        mensaje = "Bro, pasate la ubi. En plan.";

                        // Enviar QUERY_REF a rudolph
                        msgRudolph = new ACLMessage(ACLMessage.QUERY_REF);
                        msgRudolph.addReceiver(this.rudolph);
                        msgRudolph.setConversationId(CODIGO_SECRETO_SANTA_CONVERSACION_ID);
                        msgRudolph.setContent(mensaje);

                        agenteSalvador.send(msgRudolph);
                        agenteSalvador.getGUI().agregarTraza("Agente envía QUERY_REF a Rudolph");
                        estados = BUSCANDO_RENOS_PERDIDOS;

                    } else {
                        System.out.println("No entiendo lo que me quieres decir");
                    }
                } else {
                    System.out.println("Error esperando INFORM en: " + agenteSalvador.getLocalName());
                }

            }

/*            case ESPERANDO_COORDENADAS_RENO_PERDIDO -> {
                System.out.println("Esperando coordenada reno");

                msgRudolph = agenteSalvador.blockingReceive();

                System.out.println("Recibida coordenada reno");
                System.out.println(msgRudolph);

                if (msgRudolph != null && msgRudolph.getPerformative() == ACLMessage.INFORM) {

                    if (msgRudolph.getSender().equals(rudolph) && GestorComunicaciones.isCorrectMensajeAgente(msgRudolph.getContent())) {
                        // En el mensaje está la coordenada

                        mensaje = GestorComunicaciones.obtenerCoordenadasMensaje(msgRudolph.getContent());

                        String[] posicion = mensaje.split(",");
                        int fila = Integer.parseInt(posicion[0]);
                        int columna = Integer.parseInt(posicion[1]);

                        this.agenteSalvador.setNuevoObjetivo(new Posicion(fila, columna));

                        estados = EstadosAgente.MOVIENDOSE;
                    }
                }

            }
            }

            case MOVIENDOSE -> {
                // PREGUNTA: ¿Ya hemos llegado?
                if (agenteSalvador.objetivoEncontrado()) {

                    System.out.println("✅ ¡Llegué al reno! Solicitando el siguiente...");

                    // Importante: Avisar al agente para que limpie memoria/objetivo si es necesario
                    agenteSalvador.notificarRenoEncontrado();

                    // Ahora sí, cambiamos de estado para pedir la siguiente coordenada
                    estados = SOLICITAR_NUEVA_COORDENADA_RENO;

                } else {
                    // SI NO HEMOS LLEGADO:
                    // Hacemos block() para soltar la CPU y que 'DecisionMov' y 'HacerMov'
                    // puedan ejecutar sus pasos en este ciclo.
                    // NO uses blockingReceive() aquí.
                    block();
                }
            }

            case SOLICITAR_NUEVA_COORDENADA_RENO -> {
                // Creamos el mensaje de petición
                ACLMessage peticion = new ACLMessage(ACLMessage.QUERY_REF);
                peticion.addReceiver(rudolph);
                peticion.setConversationId(CODIGO_SECRETO_SANTA_CONVERSACION_ID);
                peticion.setContent("Bro, dame el siguiente. En plan.");

                agenteSalvador.send(peticion);
                agenteSalvador.getGUI().agregarTraza("Agente solicita siguiente reno.");

                // Volvemos a esperar la respuesta
                estados = ESPERANDO_COORDENADAS_RENO_PERDIDO;
            }
*/
            case BUSCANDO_RENOS_PERDIDOS -> {
                // Si el agente ha encontrado el objetivo, o no tiene un objetivo claro aun (primer reno que busca), pregunta al reno
                if (agenteSalvador.objetivoEncontrado() || !agenteSalvador.hayObjetivo())
                {
                    
                    // Preguntamos a Rudolph por el siguiente reno
                    agenteSalvador.send(msgRudolph);
                    agenteSalvador.getGUI().agregarTraza("Agente envía QUERY_REF a Rudolph");
                    System.out.println("Esperando respuesta del reno");
                    msgRudolph = agenteSalvador.blockingReceive();
                    System.out.println("Recibida respuesta del reno");
                    System.out.println(msgRudolph);
                    
                    // Procesado de la respuesta del reno
                    if (msgRudolph != null && msgRudolph.getPerformative() == ACLMessage.INFORM) // Mensaje correcto, es un inform
                    {
                        if (msgRudolph.getSender().equals(rudolph) && GestorComunicaciones.isCorrectMensajeAgente(msgRudolph.getContent())) 
                        {
                            // PROCESADO DE COORDENADAS
                            mensaje = GestorComunicaciones.obtenerCoordenadasMensaje(msgRudolph.getContent());
                            if (!mensaje.equals("Bro, no quedan renos perdidos. En plan.")) // Si quedan renos aun, decodificamos la coordenada
                            {
                                String[] posicion = mensaje.split(",");
                                int fila = Integer.parseInt(posicion[0]);
                                int columna = Integer.parseInt(posicion[1]); 
                                this.agenteSalvador.setNuevoObjetivo(new Posicion(fila, columna));
                            }
                            else // No quedan mas renos, dejamos de buscar, y continuamos con el siguiente estado
                            {
                                this.agenteSalvador.setNuevoObjetivo(null);
                                // estados = ; // Establecemos el siguiente estado
                                System.out.println("HE ENCONTRADO TODOS LOS RENOS");
                                estados = null;
                            }
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
