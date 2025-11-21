#!/bin/bash

JAR_NAME="refrescar-cache-hechos.jar"

# El primer argumento ($1) debe ser la ruta al proyecto.
PROJECT_PATH=$1

if [ -z "$PROJECT_PATH" ]; then
    echo "ERROR: Falta la ruta al directorio del proyecto."
    echo "Uso: $(basename "$0") /ruta/completa/a/tu/proyecto"
    exit 1
fi

mvn -f "${PROJECT_PATH}/pom.xml" clean package -DskipTests

echo "Build finalizado con éxito."

# Buscamos el JAR
EXPECTED_JAR_PATH="${PROJECT_PATH}/target/${JAR_NAME}"
JAR_ABSOLUTE_PATH=$(realpath "$EXPECTED_JAR_PATH")

echo "JAR encontrado en: ${JAR_ABSOLUTE_PATH}"

# Frecuencia: Cada hora, en el minuto 0.
CRON_SCHEDULE="0 * * * *"
LOG_FILE_PATH="$HOME/refrescar-cache-hechos.log"

# Comando que se ejecutará. Es crucial usar rutas absolutas.
JAVA_COMMAND="/usr/bin/java -jar ${JAR_ABSOLUTE_PATH} >> ${LOG_FILE_PATH} 2>&1"

# Línea completa que se añadirá al crontab
CRON_JOB="${CRON_SCHEDULE} ${JAVA_COMMAND}"

echo "La tarea a agendar es:"
echo "   ${CRON_JOB}"

(crontab -l 2>/dev/null | grep -v -F "${JAVA_COMMAND}" ; echo "${CRON_JOB}") | crontab -

echo "Cron exitoso"
echo "======================================================"