# Makefile para PR2_DBA - elegir mapa con make <indice> y JADE externo

SRC_DIR = src
BUILD_DIR = build/classes
MAIN_CLASS = main.PR3_DBA
JADE_LIB = lib/jade.jar

# Colores
GREEN = \033[0;32m
RED = \033[0;31m
YELLOW = \033[0;33m
RESET = \033[0m

# Lista de mapas
MAPS = 100x100-sinObstaculos.txt \
       100x100-conObstaculos.txt \
       mapWithoutObstacle.txt \
       mapWithVerticalWall.txt \
       mapWithHorizontalWall.txt \
       mapWithDiagonalWall.txt \
       mapWithComplexObstacle1.txt \
       mapWithComplexObstacle2.txt \
       mapWithComplexObstacle3.txt \
       mapWithComplexObstacle4.txt \
       mapita.txt \
	   mapHorizontal.txt \
	   mapVertical.txt \
	   mapTriangleBig.txt \
	   mapComplex1.txt \
	   mapComplex2.txt \
	   mapComplex3.txt \
	   mapComplex4.txt \
	   mapComplex5.txt \
	   mapComplex6.txt


# Posiciones del agente y del objetivo
AGENTX = 99
AGENTY = 99
SANTAX  = 0
SANTAY  = 0

.PHONY: all compile clean 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19

all: compile

compile:
	@echo "Compilando proyecto..."
	@mkdir -p $(BUILD_DIR)
	@find $(SRC_DIR) -name "*.java" > sources.txt
	@javac -cp $(JADE_LIB) -d $(BUILD_DIR) @sources.txt && \
		printf "$(GREEN)✅ Compilación finalizada correctamente$(RESET)\n" || \
		printf "$(RED)❌ Error en la compilación$(RESET)\n"
	@rm -f sources.txt

clean:
	@rm -rf $(BUILD_DIR)/* && \
		printf "$(YELLOW)🧹 Archivos compilados eliminados$(RESET)\n"

# Ejecutar mapas según índice
2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19:
	@IDX=$@ ; \
	MAPFILE=$$(echo $(MAPS) | cut -d' ' -f$$((IDX+1))) ; \
	printf "$(YELLOW)Ejecutando mapa: $$MAPFILE$(RESET)\n"; \
	java -cp $(BUILD_DIR):$(JADE_LIB):resources $(MAIN_CLASS) resources/maps/$$MAPFILE $(AGENTX) $(AGENTY) $(SANTAX) $(SANTAY)


# --- Cómo ejecutar el Makefile ---
#
# Compilar:
#   make
#
# Limpiar:
#   make clean
#
# Ejecutar un mapa (ej. mapa 0) con posiciones por defecto:
#   make 0
#


# ====================================== MAPAS PARA LA DEFENSA ======================================

# Coordenadas de los Renos (x1 y1 x2 y2 ...)
# Mapa Sin Obstáculos:
RENOS_SIN = 5 7 11 71 23 39 42 61 46 17 59 39 86 84 90 4

# Mapa Con Obstáculos:
RENOS_CON = 11 12 19 41 41 60 52 33 63 34 66 56 92 74 95 10

# Objetivos específicos para la defensa
# Formato: java ... ArchivoMapa AgenteX AgenteY SantaX SantaY [LISTA_RENOS]

0:
	@printf "$(YELLOW)Ejecutando SIN obstaculos (Agente: 99,99 | Santa: 0,0 | +Renos)$(RESET)\n"
	java -cp $(BUILD_DIR):$(JADE_LIB):resources $(MAIN_CLASS) resources/maps/100x100-sinObstaculos.txt 99 99 0 0 $(RENOS_SIN)

1:
	@printf "$(YELLOW)Ejecutando CON obstaculos (Agente: 99,99 | Santa: 0,0 | +Renos)$(RESET)\n"
	java -cp $(BUILD_DIR):$(JADE_LIB):resources $(MAIN_CLASS) resources/maps/100x100-conObstaculos.txt 99 99 0 0 $(RENOS_CON)
