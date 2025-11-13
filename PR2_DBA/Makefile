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


# mapWithoutObstacle.txt        10  /   10
# mapWithVerticalWall.txt       42  /   22
# mapWithHorizontalWall.txt     10  /   10
# mapWithDiagonalWall.txt       42  /   34
# mapWithComplexObstacle1.txt   14  /   14
# mapWithComplexObstacle2.txt   14  /   12
# mapWithComplexObstacle3.txt   40  /   22
# mapWithComplexObstacle4.txt   1740/   1386
# mapita.txt                    51  /   51



# MAPA POSICIONES PROFESOR                      Profesor    Nuestro
#   make 0 AGENTX=9 AGENTY=9 GOALX=0 GOALY=0    18          18
#   make 1 AGENTX=9 AGENTY=9 GOALX=0 GOALY=7    23          23
#   make 2 AGENTX=5 AGENTY=9 GOALX=4 GOALY=0    14          14
#   make 3 AGENTX=9 AGENTY=9 GOALX=9 GOALY=0    31          87

#   make 4 AGENTX=6 AGENTY=9 GOALX=6 GOALY=5    16          16
#   make 5 AGENTX=6 AGENTY=9 GOALX=6 GOALY=3    20          50
#   make 6 AGENTX=9 AGENTY=9 GOALX=4 GOALY=7    25          33
#   make 7 AGENTX=40 AGENTY=49 GOALX=11 GOALY=21    117     177
#   make 7 AGENTX=35 AGENTY=49 GOALX=40 GOALY=4    130      128

