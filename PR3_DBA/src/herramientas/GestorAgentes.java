package herramientas;

import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class GestorAgentes {

    /**
     * Registra un agente en el DF con el tipo y nombre del servicio
     * especificado.
     *
     * @param agente El agente que desea registrarse.
     * @param tipo El tipo de servicio que ofrece el agente.
     * @param nombre El nombre del servicio que ofrece el agente.
     */
    public static void registrarAgente(Agent agente, String tipo, String nombre) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agente.getAID()); // Identificador del agente
        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipo); // Tipo de servicio ofrecido
        sd.setName(nombre); // Nombre del servicio
        dfd.addServices(sd);

        try {
            DFService.register(agente, dfd);
        } catch (FIPAException e) {
            System.err.println("Error registrando el agente " + agente.getLocalName() + ": " + e.getMessage());
        }
    }

    /**
     * Busca agentes en el DF que ofrezcan un servicio del tipo especificado.
     *
     * @param agente El agente que realiza la búsqueda.
     * @param tipo El tipo de servicio que desea buscar.
     * @return Un arreglo de AID de los agentes encontrados.
     */
    public static AID[] buscarAgentes(Agent agente, String tipo) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipo); // Tipo de servicio buscado
        template.addServices(sd);

        try {
            DFAgentDescription[] resultados = DFService.search(agente, template);
            AID[] agentesEncontrados = new AID[resultados.length];
            for (int i = 0; i < resultados.length; i++) {
                agentesEncontrados[i] = resultados[i].getName();
            }
            return agentesEncontrados;
        } catch (FIPAException e) {
            System.err.println("Error buscando agentes para " + agente.getLocalName() + ": " + e.getMessage());
            return new AID[0];
        }
    }

    /**
     * Desregistra un agente del DF antes de que termine su ejecución.
     *
     * @param agente El agente que desea desregistrarse.
     */
    public static void desregistrarAgente(Agent agente) {
        try {
            DFService.deregister(agente);
        } catch (FIPAException e) {
            System.err.println("Error desregistrando el agente " + agente.getLocalName() + ": " + e.getMessage());
        }
    }

    /**
     * Busca un agente en un arreglo de AID por su nombre.
     *
     * @param agentes El arreglo de AID donde se realiza la búsqueda.
     * @param nombre El nombre del agente que deseas buscar (puede ser parcial o
     * completo).
     * @return El AID del agente encontrado o null si no se encuentra.
     */
    public static AID buscarAgenteEnLista(AID[] agentes, String nombre) {
        if (agentes == null || agentes.length == 0) {
            return null;
        }

        for (AID agente : agentes) {
            if (agente.getLocalName().equalsIgnoreCase(nombre)) {
                return agente;
            }
        }

        System.out.println("No se encontró ningún agente con el nombre: " + nombre);
        return null;
    }

}
