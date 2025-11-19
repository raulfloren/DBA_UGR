# Práctica 2: Movimiento de un Agente en un Mundo Bidimensional

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/en/)
[![JADE](https://img.shields.io/badge/JADE-Agent%20Framework-00A2E8?style=for-the-badge&logo=jfrog&logoColor=white)](http://jade.tilab.com/)
[![LRTA*](https://img.shields.io/badge/Algoritmo-LRTA*-5C2D91?style=for-the-badge)](https://en.wikipedia.org/wiki/Learning_Real-time_A*)

Un sistema de agente inteligente desarrollado en **Java** utilizando la plataforma **JADE** (Java Agent Development Framework). El agente está diseñado para navegar de forma autónoma en un mundo 2D simulado, esquivando obstáculos y alcanzando un objetivo de manera eficiente mediante una estrategia de búsqueda auto-aprendizaje basada en el algoritmo **Learning Real-Time A\* (LRTA\*)**.

---

## ✨ Características Principales
* **Navegación Autónoma:** Un agente inteligente capaz de recorrer un mundo de cuadrícula 2D dinámico.
* **Evasión de Obstáculos:** Los mapas se cargan desde archivos de texto, donde `-1` denota obstáculos y `0` representa casillas transitables.
* **Búsqueda LRTA\*:** Implementación del algoritmo LRTA\*, una heurística de búsqueda **greedy** en tiempo real que aprende de los estados visitados para mejorar las decisiones futuras.
* **Arquitectura Desacoplada:** Clara separación entre el **Agente**, el **Entorno** y el **Mapa**, garantizando la encapsulación y la coherencia de las acciones del agente.
* **Comportamientos JADE:** Ciclo de vida del agente estructurado siguiendo el clásico ciclo **Percepción → Decisión → Acción → Validación**, utilizando los mecanismos de comportamientos de JADE.
* **Interfaz Gráfica de Usuario (GUI):** Una simulación visual que muestra el movimiento del agente en tiempo real, el mapa y un registro de movimientos tomados. 

---

## 📐 Estructura del Sistema
El proyecto utiliza una arquitectura sólida y desacoplada para gestionar la interacción del agente con el mundo.

### 1. Componentes Clave
| Componente | Responsabilidad | Clases Principales |
| :--- | :--- | :--- |
| **Mapa/Mundo** | Representa la cuadrícula 2D, carga los datos y gestiona el estado del mundo. | `MapLoader`, `Mapa` |
| **Entorno** | Actúa como interfaz entre el Agente y el Mapa, validando y registrando las posiciones/acciones del agente. | `Entorno` |
| **Sensor** | Encapsula la percepción del agente; consulta al Entorno para determinar movimientos válidos. | `Sensor` |
| **Agente** | La entidad inteligente central, que gestiona su estado interno (posiciones visitadas, mapa de calor) y sus comportamientos. | `Agente` |

### 2. Ciclo de Comportamiento del Agente (JADE)
El agente ejecuta continuamente un ciclo de cuatro **Comportamientos JADE** personalizados hasta que se encuentra el objetivo:

1.  **Percepción:** Consulta el `Sensor` (`verCasillasDisponibles()`) para encontrar los posibles movimientos siguientes y actualiza la memoria de celdas vistas del agente (`updateMemoriaVistas()`).
2.  **Decisión (`DecisiónMov`):** Selecciona el mejor movimiento (`mejorMovimiento`) utilizando la estrategia **LRTA\***.
3.  **Acción (`HacerMov`):** Ejecuta el movimiento elegido, comunicándolo al `Sensor` para que actualice el `Entorno`, y actualiza el mapa de calor/memoria interna del agente.
4.  **Validación:** Comprueba si se ha alcanzado el objetivo y actualiza la GUI con los resultados y el registro de movimientos.



---

## 🧠 Estrategia de Decisión LRTA\*
El núcleo de la inteligencia del agente es su toma de decisiones de búsqueda de caminos, implementada con **Learning Real-Time A\*** (**LRTA\***).

Este es un algoritmo de búsqueda en tiempo real, de naturaleza **greedy**, que solo explora los vecinos inmediatos del estado actual (las celdas visibles a través del sensor). Crucialmente, **aprende** actualizando el coste heurístico del estado actual después de cada movimiento, lo que mejora su guía para futuras decisiones.

### Función de Coste
El coste de decisión $F_{\text{decisión}}$ para moverse del estado actual $s$ a un potencial estado siguiente $s'$ se calcula como:

$$F_{\text{decisión}} = \text{costo}(s, s') + h(s') + P_{\text{dirección}} + P_{\text{momento}}$$

Donde:
* **$\text{costo}(s, s')$:** El coste energético (o coste del paso) de la transición.
* **$h(s')$:** El valor **Heurístico** para la siguiente posición. Se calcula como el *promedio de las distancias Manhattan y Euclídea* al objetivo.
* **$P_{\text{dirección}}$ (Bonificación de Dirección):** Una **bonificación** si la dirección del movimiento se alinea con la dirección general hacia el objetivo.
* **$P_{\text{momento}}$ (Bonificación de Momento/Inercia):** Una **bonificación** si el movimiento actual es el mismo que el realizado anteriormente, fomentando una dirección persistente a lo largo de paredes o en espacio abierto.
* **Penalización por Visita:** Se aplica una **penalización** dentro del cálculo de `h(s')` si la celda ha sido visitada antes, lo que encarece el camino y promueve la exploración.

### Componente de Aprendizaje
Antes de moverse a la posición elegida, el agente actualiza el valor heurístico del estado actual $s$ (el estado que está dejando) para que sea el máximo entre su antiguo valor heurístico y el mejor valor $f(s')$ (coste + heurística) encontrado entre sus vecinos. Este paso crucial obliga al agente a aprender de los costes previamente subestimados.

---

## 🚀 Puesta en Marcha

### Prerrequisitos
* **Java Development Kit (JDK) 8 o superior**
* **Librerías del Framework JADE** (Necesarias para el sistema de agentes)

### Ejecutar el Proyecto
1.  **Clonar el repositorio:**
    ```bash
    cd [Tu Carpeta de Proyecto]
    git clone https://github.com/raulfloren/DBA_UGR/tree/develop
    ```
2.  **Compilar y Ejecutar:**
    Para compilación y ejecución, usaremos el Makefile.
    ```bash
    make compile # Para compilación
    make 0 # Cuando indicamos un número, ejecutará el mapa asociado a ese id
    
3.  **Defensa:** 
    Para la defensa del proyecto, tenemos un script que ejecuta mapas específicos con posiciones dadas por el profesor. Se usa de la siguiente forma:
    ```bash
    ./mapasDefensa 0 # El número es el id de la prueba 
    ```
    Estas son las distintas pruebas que corresponden a los id dados:

    | Tipo mapa | Fichero | Agent-x | Agent-y | Goal-x | Goal-y | Energía (Referencia) | Energía (Agente) |
    | :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: |
    | Sin obstáculos | `mapWithoutObstacle.txt` | 49 | 49 | 0 | 0 | 98 | 98 |
    | Horizontal | `mapHorizontal.txt` | 30 | 49 | 30 | 0 | 85 | 85 |
    | Vertical | `mapVertical.txt` | 0 | 25 | 49 | 25 | 103 | 103 |
    | Diagonal | `mapTriangleBig.txt` | 30 | 49 | 40 | 0 | 193 | 1083 |
    | Convexo | `mapComplex1.txt` | 30 | 49 | 40 | 0 | 381 | 711 |
    | Cóncavo | `mapComplex2.txt` | 30 | 49 | 40 | 0 | 107 | 107 |
    | Complejo 1 | `mapComplex3.txt` | 49 | 17 | 3 | 15 | 162 | 1476 |
    | Complejo 2 | `mapComplex4.txt` | 49 | 27 | 15 | 39 | 178 | 404 |
    | Sorpresa 1 | `mapComplex5.txt` | 25 | 49 | 25 | 37 | 102 | 920 |
    | Sorpresa 2 | `mapComplex6.txt` | 24 | 31 | 25 | 34 | 176 | 884 | 

    Líneas de ejecución para cada prueba:
    ```bash
    make 0 AGENTX=49 AGENTY=49 GOALX=0 GOALY=0
    make 1 AGENTX=30 AGENTY=49 GOALX=30 GOALY=0
    make 2 AGENTX=0 AGENTY=25 GOALX=49 GOALY=25
    make 3 AGENTX=30 AGENTY=49 GOALX=40 GOALY=0
    make 4 AGENTX=30 AGENTY=49 GOALX=40 GOALY=0
    make 5 AGENTX=30 AGENTY=49 GOALX=40 GOALY=0
    make 6 AGENTX=49 AGENTY=17 GOALX=3 GOALY=15
    make 7 AGENTX=49 AGENTY=27 GOALX=15 GOALY=39
    make 8 AGENTX=25 AGENTY=49 GOALX=25 GOALY=37
    make 9 AGENTX=24 AGENTY=31 GOALX=25 GOALY=34
    ```

### Configuración
El mapa se carga desde un archivo de texto (por ejemplo, `mapa.txt`). La estructura del archivo es:
1.  Dimensiones del mapa (ej. `10 10`)
2.  Matriz del mapa, donde:
    * **`0`**: Casilla transitable
    * **`-1`**: Obstáculo (Muro)

---

## 🤝 Contribuidores

* **Raúl Florentino Serra**
* **Jesús Pereira Sánchez**
* **Juan Manuel Guerrero Espigares**

Proyecto realizado para la asignatura **Diseño Basado en Agentes** del **Grado en Ingeniería Informática** de la Universidad de Granada (UGR).