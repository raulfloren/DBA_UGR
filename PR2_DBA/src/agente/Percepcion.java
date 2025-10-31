/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agente;


/**
 *
 * @author juanma
 */

public class Percepcion{
    // 0 = libre, -1 = obstáculo
    public final int sensorArriba;
    public final int sensorAbajo;
    public final int sensorIzquierda;
    public final int sensorDerecha;
    
    public final Posicion posActual;
    public final int energiaConsumida;

    public Percepcion(Posicion pos, int energia, int arriba, int abajo, int izq, int der) {
        this.posActual = pos;
        this.energiaConsumida = energia;
        this.sensorArriba = arriba;
        this.sensorAbajo = abajo;
        this.sensorIzquierda = izq;
        this.sensorDerecha = der;
    }
}