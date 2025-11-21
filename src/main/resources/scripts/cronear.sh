#!/bin/bash

# Función para agendar una tarea cron
# Argumentos:
# 1. Nombre del JAR (ej: "refrescar-cache-hechos.jar")
# 2. Expresión Cron (ej: "0 * * * *")
# 3. Ruta al proyecto (variable global)
agendar_cron() {
    local JAR_NAME=$1
    local CRON_SCHEDULE=$2
    local LOG_FILE_NAME=$(basename "$JAR_NAME" .jar)

    echo "------------------------------------------------------"
    echo "Agendando tarea para: ${JAR_NAME}"

    # Buscamos el JAR
    local EXPECTED_JAR_PATH="${PROJECT_PATH}/target/${JAR_NAME}"
    if [ ! -f "$EXPECTED_JAR_PATH" ]; then
        echo "ERROR: No se encontró el JAR en ${EXPECTED_JAR_PATH}"
        return 1
    fi
    local JAR_ABSOLUTE_PATH=$(realpath "$EXPECTED_JAR_PATH")

    echo "JAR encontrado en: ${JAR_ABSOLUTE_PATH}"

    local LOG_FILE_PATH="$HOME/${LOG_FILE_NAME}.log"

    # Comando que se ejecutará. Es crucial usar rutas absolutas.
    local JAVA_COMMAND="/usr/bin/java -jar ${JAR_ABSOLUTE_PATH} >> ${LOG_FILE_PATH} 2>&1"

    # Línea completa que se añadirá al crontab
    local CRON_JOB="${CRON_SCHEDULE} ${JAVA_COMMAND}"

    echo "La tarea a agendar es:"
    echo "   ${CRON_JOB}"

    # Añade la tarea al crontab, evitando duplicados.
    (crontab -l 2>/dev/null | grep -v -F "${JAVA_COMMAND}" ; echo "${CRON_JOB}") | crontab -

    echo "Tarea para ${JAR_NAME} agendada con éxito."
}

# --- Script Principal ---

# El primer argumento ($1) debe ser la ruta al proyecto.
PROJECT_PATH=$1

if [ -z "$PROJECT_PATH" ]; then
    echo "ERROR: Falta la ruta al directorio del proyecto."
    echo "Uso: $(basename "$0") /ruta/completa/a/tu/proyecto"
    exit 1
fi

# Llamamos a la función para cada tarea
agendar_cron "refrescar-cache-hechos.jar" "0 * * * *"
agendar_cron "actualizador-fuente-demo.jar" "0 * * * *"
agendar_cron "calcular-consenso.jar" "0 3 * * *"

echo "======================================================"
echo "Todas las tareas cron han sido agendadas."