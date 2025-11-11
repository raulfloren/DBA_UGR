# Makefile para PR2_DBA - elegir mapa con make <indice> y JADE externo

SRC_DIR = src
BUILD_DIR = build/classes
MAIN_CLASS = main.PR2_DBA
JADE_LIB = lib/jade.jar

# Colores
GREEN = \033[0;32m
RED = \033[0;31m
YELLOW = \033[0;33m
RESET = \033[0m

# Lista de mapas
MAPS = mapWithoutObstacle.txt \
       mapWithVerticalWall.txt \
       mapWithHorizontalWall.txt \
       mapWithDiagonalWall.txt \
       mapWithComplexObstacle1.txt \
       mapWithComplexObstacle2.txt \
       mapWithComplexObstacle3.txt \
       mapWithComplexObstacle4.txt \
       mapita.txt

# Posiciones del agente y del objetivo
AGENTX = 2
AGENTY = 2
GOALX  = 7
GOALY  = 7

.PHONY: all compile clean 0 1 2 3 4 5 6 7 8

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
0 1 2 3 4 5 6 7 8:
	@IDX=$@ ; \
	MAPFILE=$$(echo $(MAPS) | cut -d' ' -f$$((IDX+1))) ; \
	printf "$(YELLOW)Ejecutando mapa: $$MAPFILE$(RESET)\n"; \
	java -cp $(BUILD_DIR):$(JADE_LIB):resources $(MAIN_CLASS) resources/maps/$$MAPFILE $(AGENTX) $(AGENTY) $(GOALX) $(GOALY)


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
# Ejecutar un mapa (ej. mapa 1) con posiciones PERSONALIZADAS:
#   make 2 AGENTX=4 AGENTY=2 GOALX=5 GOALY=7
#   make 4 AGENTX=2 AGENTY=7 GOALX=6 GOALY=5
#   make 7 AGENTX=2 AGENTY=2 GOALX=37 GOALY=47
#   make 8 AGENTX=29 AGENTY=14 GOALX=14 GOALY=14


# mapWithoutObstacle.txt        10
# mapWithVerticalWall.txt       42
# mapWithHorizontalWall.txt     10
# mapWithDiagonalWall.txt       42
# mapWithComplexObstacle1.txt   14
# mapWithComplexObstacle2.txt   14
# mapWithComplexObstacle3.txt   40
# mapWithComplexObstacle4.txt   2488
# mapita.txt                    51
