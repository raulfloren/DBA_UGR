package Agente.comportamientos;

import Agente.Agente;
import herramientas.GestorAgentes;
import herramientas.GestorComunicaciones;
import herramientas.Posicion;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import entorno.Entorno;

public class ComunicacionAgente extends Behaviour {

    private final Entorno entorno;
    private final int RENO_ID;

    private final String CONVERSACION_AGENTE_ELFO_ID = "salvador-elfo-conversacion";
    private String CODIGO_SECRETO_SANTA_CONVERSACION_ID;
    private final Agente agenteSalvador;
    private EstadosAgente estados;

    private boolean fin;

    private AID elfoTraductor, santaClaus, rudolph;
    private ACLMessage msgElfo, msgSanta, msgRudolph;

    public ComunicacionAgente(Agente agent) {
        super(agent);
        this.RENO_ID = -5;
        this.agenteSalvador = agent;
        this.entorno = this.agenteSalvador.getSensores().getEntorno();
        this.estados = EstadosAgente.INICIO_MISION_SALVAR_NAVIDAD;
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
            // Mandamos a traducir el mensaje para santaá
            case INICIO_MISION_SALVAR_NAVIDAD -> {
                msgElfo = new ACLMessage(ACLMessage.REQUEST);

                mensaje = "Bro, voy a aprobar. He hecho las practicas confia. En plan.";

                msgElfo.addReceiver(elfoTraductor);
                msgElfo.setLanguage("GenZ");
                msgElfo.setContent(mensaje);
                msgElfo.setConversationId(CONVERSACION_AGENTE_ELFO_ID);
                agenteSalvador.send(msgElfo);
                agenteSalvador.getGUI().agregarTraza("Alumno envía REQUEST a Delegado");

                this.estados = EstadosAgente.ESPERANDO_TRADUCCION_ELFO_1;
            }

            // Esperando la traduccion del mensaje inical para enviarselo a santa
            case ESPERANDO_TRADUCCION_ELFO_1 -> {

                msgElfo = agenteSalvador.blockingReceive();

                if (msgElfo != null && msgElfo.getPerformative() == ACLMessage.INFORM) {

                    if (msgElfo.getSender().equals(elfoTraductor)) {

                        // Traduccion del mensaje
                        mensaje = msgElfo.getContent();

                        // Enviar PROPOSAL a SantaClaus
                        msgSanta = new ACLMessage(ACLMessage.PROPOSE);
                        msgSanta.addReceiver(this.santaClaus);
                        msgSanta.setContent(mensaje);

                        agenteSalvador.send(msgSanta);
                        agenteSalvador.getGUI().agregarTraza("Alumno envía PROPOSE al Profesor");
                        estados = EstadosAgente.ESPERANDO_CONFIRMACION_SANTA;

                    } else {
                        System.out.println("No entiendo lo que me quieres decir");
                    }
                } else {
                    System.out.println("Error esperando INFORM en: " + agenteSalvador.getLocalName());
                }

                this.estados = EstadosAgente.ESPERANDO_CONFIRMACION_SANTA;
            }

            // Esperamos la contestacion del proposal de santa y lo enviamos a traducir si es ACCEPT
            case ESPERANDO_CONFIRMACION_SANTA -> {

                msgSanta = agenteSalvador.blockingReceive();

                if (msgSanta != null && msgSanta.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {

                    if (msgSanta.getSender().equals(santaClaus) && GestorComunicaciones.isCorrectMensajeSantaClaus(msgSanta.getContent())) {

                        mensaje = msgSanta.getContent();

                        msgElfo = new ACLMessage(ACLMessage.REQUEST);
                        msgElfo.addReceiver(elfoTraductor);
                        msgElfo.setLanguage("Fines");
                        msgElfo.setContent(mensaje);
                        agenteSalvador.send(msgElfo);
                        agenteSalvador.getGUI().agregarTraza("Alumno envía REQUEST a Delegado");

                        this.estados = EstadosAgente.ESPERANDO_TRADUCCION_ELFO_2;
                    }
                } else if (msgSanta != null && msgSanta.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                    if (msgSanta.getSender().equals(santaClaus) && GestorComunicaciones.isCorrectMensajeSantaClaus(msgSanta.getContent())) {
                        agenteSalvador.doDelete();
                    }
                }
            }

            // Esperamos el mensaje de santa traducido en el que nos da el codigo secreto
            case ESPERANDO_TRADUCCION_ELFO_2 -> {

                msgElfo = agenteSalvador.blockingReceive();

                if (msgElfo != null && msgElfo.getPerformative() == ACLMessage.INFORM) {

                    if (msgElfo.getSender().equals(elfoTraductor)) {

                        // Obtener codigo
                        CODIGO_SECRETO_SANTA_CONVERSACION_ID = GestorComunicaciones.obtenerCodigoSecreto(msgElfo.getContent());

                        System.out.println("CODIGO SECRETO: " + CODIGO_SECRETO_SANTA_CONVERSACION_ID);

                        mensaje = "Bro, dime donde estan los apuntesó. En plan.";

                        // Enviar QUERY_REF a rudolph
                        msgRudolph = new ACLMessage(ACLMessage.QUERY_REF);
                        msgRudolph.addReceiver(this.rudolph);
                        msgRudolph.setConversationId(CODIGO_SECRETO_SANTA_CONVERSACION_ID);
                        msgRudolph.setContent(mensaje);

                        agenteSalvador.send(msgRudolph);
                        agenteSalvador.getGUI().agregarTraza("Alumno envía QUERY_REF a Alumno Empollon");
                        this.estados = EstadosAgente.ESPERANDO_COORDENADAS_RENO_PERDIDO;

                    } else {
                        System.out.println("No entiendo lo que me quieres decir");
                    }
                } else {
                    System.out.println("Error esperando INFORM en: " + agenteSalvador.getLocalName());
                }

            }

            // Esperamos la coordenada de un reno
            case ESPERANDO_COORDENADAS_RENO_PERDIDO -> {
                msgRudolph = agenteSalvador.blockingReceive();

                if (msgRudolph != null && msgRudolph.getPerformative() == ACLMessage.INFORM) // Mensaje correcto, es un inform
                {
                    if (msgRudolph.getSender().equals(rudolph) && GestorComunicaciones.isCorrectMensajeAgente(msgRudolph.getContent())) {
                        // QUEDAN RENOS
                        // PROCESADO DE COORDENADAS,  En el mensaje está la coordenada
                        mensaje = GestorComunicaciones.obtenerCoordenadasMensaje(msgRudolph.getContent());
                        // Si quedan renos aun, decodificamos la coordenada

                        String[] posicion = mensaje.split(",");
                        int fila = Integer.parseInt(posicion[0]);
                        int columna = Integer.parseInt(posicion[1]);
                        Posicion pos = new Posicion(fila, columna);
                        entorno.getMapa().ponerItemEnMapa(pos, RENO_ID);

                        this.agenteSalvador.setNuevoObjetivo(pos);

                        this.estados = EstadosAgente.BUSCANDO_RENOS_PERDIDOS;

                    }
                } else if (msgRudolph != null && msgRudolph.getPerformative() == ACLMessage.REFUSE) { // No quedan renos
                    System.out.println("HE ENCONTRADO TODOS LOS APUNTES");
                    this.estados = EstadosAgente.SOLICITAR_COORDENADAS_SANTA;
                } else { // Para cualquier otro caso, que no sea inform o refuse, volvemos a pedir las coordenadas.
                    this.estados = EstadosAgente.SOLICITAR_NUEVA_COORDENADA_RENO;

                }
            }

            // Pedimos a rudolph un nuevo reno
            case SOLICITAR_NUEVA_COORDENADA_RENO -> {
                // Creamos el mensaje de petición
                msgRudolph = new ACLMessage(ACLMessage.QUERY_REF);
                msgRudolph.addReceiver(rudolph);
                msgRudolph.setConversationId(CODIGO_SECRETO_SANTA_CONVERSACION_ID);
                msgRudolph.setContent("Bro, siguientes apuntes que estoy en racha. En plan.");

                agenteSalvador.send(msgRudolph);

                agenteSalvador.getGUI().agregarTraza("Agente solicita siguientes apuntes.");

                // Volvemos a esperar la respuesta
                this.estados = EstadosAgente.ESPERANDO_COORDENADAS_RENO_PERDIDO;
            }

            // Estamos moviendonos buscando los renos
            case BUSCANDO_RENOS_PERDIDOS -> {
                // Queremos que en el momento que llegue al objetivo solicite uno nuevo,
                // Cuando llegue al objetivo este ya sera null, 
                //comprobar objetivo anterior (objetivo en el que esta situado actualmente)
                if (this.agenteSalvador.getPosAgente().equals(this.agenteSalvador.getPosObjetivoAnterior())) {
                    System.out.println("HABEMOS ENCONTRADO LOS APUNTES DEL TEMA");

                    this.estados = EstadosAgente.SOLICITAR_NUEVA_COORDENADA_RENO;
                }
            }

            // Pedimos las coordenadas, primero lo enviamos a traducir
            case SOLICITAR_COORDENADAS_SANTA -> {

                msgElfo = new ACLMessage(ACLMessage.REQUEST);

                mensaje = "Bro, Pasate la ubi del examen. En plan.";

                // Primero hay que traducirlo
                msgElfo.addReceiver(elfoTraductor);
                msgElfo.setLanguage("GenZ");
                msgElfo.setContent(mensaje);
                msgElfo.setConversationId(CONVERSACION_AGENTE_ELFO_ID);
                agenteSalvador.send(msgElfo);
                agenteSalvador.getGUI().agregarTraza("Alumno envía REQUEST a Delegado");

                this.estados = EstadosAgente.ESPERANDO_TRADUCCION_ELFO_3;
            }

            // Esperando traduccion del mensaje donde se piden las coordenadas de santa
            case ESPERANDO_TRADUCCION_ELFO_3 -> {

                msgElfo = agenteSalvador.blockingReceive();

                if (msgElfo != null && msgElfo.getPerformative() == ACLMessage.INFORM) {

                    if (msgElfo.getSender().equals(elfoTraductor)) {

                        // Traduccion del mensaje
                        mensaje = msgElfo.getContent();

                        // Enviamos solicitud de coordenadas a SantaClaus
                        msgSanta = new ACLMessage(ACLMessage.REQUEST);
                        msgSanta.addReceiver(this.santaClaus);
                        msgSanta.setContent(mensaje);

                        agenteSalvador.send(msgSanta);
                        agenteSalvador.getGUI().agregarTraza("Alumno envía REQUEST a Profesor");

                    } else {
                        System.out.println("No entiendo lo que me quieres decir");
                    }
                } else {
                    System.out.println("Error esperando INFORM en: " + agenteSalvador.getLocalName());
                }

                this.estados = EstadosAgente.ESPERANDO_COORDENADAS_SANTA;

            }

            // Santa nos dice las coordenadas, las enviamos a traducir
            case ESPERANDO_COORDENADAS_SANTA -> {

                msgSanta = agenteSalvador.blockingReceive();

                if (msgSanta != null && msgSanta.getPerformative() == ACLMessage.INFORM) {

                    if (msgSanta.getSender().equals(santaClaus) && GestorComunicaciones.isCorrectMensajeSantaClaus(msgSanta.getContent())) {

                        mensaje = msgSanta.getContent();

                        // Lo enviamos a traducir
                        msgElfo = new ACLMessage(ACLMessage.REQUEST);
                        msgElfo.addReceiver(elfoTraductor);
                        msgElfo.setLanguage("Fines");
                        msgElfo.setContent(mensaje);
                        agenteSalvador.send(msgElfo);

                        agenteSalvador.getGUI().agregarTraza("Alumno envía REQUEST a Delegado");

                        this.estados = EstadosAgente.ESPERANDO_TRADUCCION_ELFO_4;
                    }
                }
            }

            // Esperando la traduccion del mensaje donde santa nos decia las coordenadas
            case ESPERANDO_TRADUCCION_ELFO_4 -> {

                msgElfo = agenteSalvador.blockingReceive();

                if (msgElfo != null && msgElfo.getPerformative() == ACLMessage.INFORM) {

                    if (msgElfo.getSender().equals(elfoTraductor)) {

                        // Obtener coordenadas
                        mensaje = GestorComunicaciones.obtenerCoordenadasMensaje(msgElfo.getContent());

                        String[] posicion = mensaje.split(",");
                        int fila = Integer.parseInt(posicion[0]);
                        int columna = Integer.parseInt(posicion[1]);
                        Posicion pos = new Posicion(fila, columna);
                        this.agenteSalvador.setNuevoObjetivo(pos);

                    } else {
                        System.out.println("No entiendo lo que me quieres decir");
                    }
                } else {
                    System.out.println("Error esperando INFORM en: " + agenteSalvador.getLocalName());
                }

                this.estados = EstadosAgente.YENDO_A_SANTA;
            }

            // Yendo a santa
            case YENDO_A_SANTA -> {
                if (this.agenteSalvador.getPosAgente().equals(this.agenteSalvador.getPosObjetivoAnterior())) {
                    System.out.println("HABEMOS PRESENTADO AL EXAMEN");
                    this.estados = EstadosAgente.PEDIR_HoHoHo;
                }
            }

            // Pedimos el hohoho, primero lo enviamos a traducir
            case PEDIR_HoHoHo -> {
                msgElfo = new ACLMessage(ACLMessage.REQUEST);

                mensaje = "Bro, he llegado, ¿habemos salvado el cuatri?. En plan.";

                // Primero hay que traducirlo
                msgElfo.addReceiver(elfoTraductor);
                msgElfo.setLanguage("GenZ");
                msgElfo.setContent(mensaje);
                msgElfo.setConversationId(CONVERSACION_AGENTE_ELFO_ID);
                agenteSalvador.send(msgElfo);
                agenteSalvador.getGUI().agregarTraza("Alumno envía REQUEST a Delegado");

                this.estados = EstadosAgente.ESPERANDO_TRADUCCION_ELFO_5;

            }

            // Esperando traduccion del mennsaje donde se pide el hohoho
            case ESPERANDO_TRADUCCION_ELFO_5 -> {

                msgElfo = agenteSalvador.blockingReceive();

                if (msgElfo != null && msgElfo.getPerformative() == ACLMessage.INFORM) {

                    if (msgElfo.getSender().equals(elfoTraductor)) {

                        mensaje = msgElfo.getContent();

                        // Pedimos el HoHoHo
                        msgSanta = new ACLMessage(ACLMessage.REQUEST);
                        msgSanta.addReceiver(santaClaus);
                        msgSanta.setContent(mensaje);
                        msgSanta.setConversationId(CODIGO_SECRETO_SANTA_CONVERSACION_ID);
                        agenteSalvador.send(msgSanta);
                        agenteSalvador.getGUI().agregarTraza("Alumno envía REQUEST al Profesor");

                        this.estados = EstadosAgente.ESPERANDO_HoHoHo;

                    } else {
                        System.out.println("No entiendo lo que me quieres decir");
                    }
                } else {
                    System.out.println("Error esperando INFORM en: " + agenteSalvador.getLocalName());
                }
            }

            // Ya ha llegado donde santa, esperamos el HoHoHo;
            case ESPERANDO_HoHoHo -> {

                msgSanta = agenteSalvador.blockingReceive();

                if (msgSanta != null && msgSanta.getPerformative() == ACLMessage.INFORM) {
                    if (msgSanta.getSender().equals(santaClaus) && GestorComunicaciones.isCorrectMensajeSantaClaus(msgSanta.getContent())) {

                        System.out.println(msgSanta.getContent());
                        this.agenteSalvador.haSalvadoElCuatri();
                    }
                }

                this.estados = EstadosAgente.WAIT;
            }

            case WAIT -> {
                msgRudolph = agenteSalvador.blockingReceive();

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
