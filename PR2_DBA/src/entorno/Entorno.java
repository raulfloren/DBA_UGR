package entorno;

import agente.Posicion;

/**
 *
 * @author floren
 */
public class Entorno {

    private final static int AGENTE_ID = 9, OBJETIVO_ID = 8;
    private Mapa mapa;
    private Posicion agente, objetivo;

    public Entorno(Mapa mapa, Posicion agente, Posicion objetivo) {
        this.mapa = mapa;
        this.agente = agente;
        this.objetivo = objetivo;
        initEntorno();
    }

    public void initEntorno() {
        mapa.ponerItemEnMapa(agente, AGENTE_ID);
        mapa.ponerItemEnMapa(objetivo, OBJETIVO_ID);
    }

}
