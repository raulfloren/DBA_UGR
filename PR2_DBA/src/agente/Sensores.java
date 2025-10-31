package agente;

import entorno.Entorno;
import java.util.ArrayList;
import movimientos.Movimientos;

/**
 *
 * @author floren
 */
public class Sensores {

    private Entorno entorno;
    private int energia;

    public Sensores(Entorno entorno, int engeria) {
        this.entorno = entorno;
        this.energia = energia;
    }

    public ArrayList<Movimientos> verCasillasDisponibles() {
        // Aqui se llama al entorno para ver las casillas disponibles a las que se puede mover el agente 
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void addEnegia() {
        energia += 1;
    }
}
