#!/bin/bash
# parcial.sh - "recetas" tipo Makefile para correr las consultas del parcial en el
# clúster (master + worker1 + worker2). Equivalente de parcial.ps1 para Linux.
# Copiar a master: scp parcial.sh hadoop@192.168.56.10:~/   y la primera vez: chmod +x ~/parcial.sh
# Uso (en master, con Hadoop ya arrancado: start-dfs.sh y start-yarn.sh):
#   ./parcial.sh help
#   ./parcial.sh status
#   ./parcial.sh run SeveridadPorVia
#   ./parcial.sh run PuntosNegrosPorTramo

TARGET="${1:-help}"
PAQUETE="$2"

# CAMBIAR DE ACUERDO A LA RUTA QUE SE USE
JAR="$HOME/PARCIAL.jar"
CSV_LOCAL="$HOME/dataSSE_17122021_c_utf8.csv"
INPUT_DIR="/parcial_input"
INPUT_FILE="$INPUT_DIR/dataSSE_17122021_c_utf8.csv"

show_help() {
    echo "Targets disponibles:"
    echo "  help         Muestra esta ayuda (default)"
    echo "  status       Procesos Java en los 3 nodos (jps) y DataNodes vivos"
    echo "  run <Pkg>    Corre <Pkg>.Driver contra PARCIAL.jar, mide el tiempo y muestra el resultado"
    echo ""
    echo "Ejemplo: ./parcial.sh run SeveridadPorVia"
    echo "Ejemplo: ./parcial.sh run PuntosNegrosPorTramo"
}

invoke_status() {
    # Master debe tener NameNode, SecondaryNameNode y ResourceManager;
    # cada worker, DataNode y NodeManager
    for nodo in master worker1 worker2; do
        echo "== $nodo"
        ssh "$nodo" jps
    done
    hdfs dfsadmin -report 2>/dev/null | grep "Live datanodes"   # debe decir (2)
}

invoke_run() {
    if [ -z "$PAQUETE" ]; then
        echo "Uso: ./parcial.sh run <NombrePaquete>"
        echo "Ejemplo: ./parcial.sh run PuntosNegrosPorTramo"
        return 1
    fi
    if [ ! -f "$JAR" ]; then
        echo "No se encontro $JAR - copialo desde Windows con scp despues del Build."
        return 1
    fi
    OUTPUT_DIR="/parcial_output/$PAQUETE"

    # Verifica si el CSV ya esta en HDFS; si no, lo sube.
    if ! hdfs dfs -test -e "$INPUT_FILE" 2>/dev/null; then
        echo "CSV no encontrado en $INPUT_DIR - subiendolo..."
        if [ ! -f "$CSV_LOCAL" ]; then
            echo "No se encontro el CSV local en $CSV_LOCAL"
            return 1
        fi
        hdfs dfs -mkdir -p "$INPUT_DIR"
        hdfs dfs -put "$CSV_LOCAL" "$INPUT_DIR/"
    fi

    # Borra la salida anterior: si existe, el job falla
    hdfs dfs -rm -r -f -skipTrash "$OUTPUT_DIR" 2>/dev/null

    echo "Ejecutando $PAQUETE.Driver ..."
    # java -cp y no hadoop jar: el manifiesto de NetBeans trae Main-Class: parcial.PARCIAL
    # y hadoop jar ejecutaria esa clase en vez del Driver.
    # time mide el tiempo total de los jobs encadenados (para la tabla clúster vs. nodo unico)
    inicio=$(date +%s.%N)
    java -cp "$JAR:$(hadoop classpath)" "$PAQUETE.Driver" "$INPUT_DIR" "$OUTPUT_DIR"
    codigo=$?
    fin=$(date +%s.%N)
    if [ $codigo -ne 0 ]; then
        echo "El job termino con error (exit code $codigo)."
        return 1
    fi

    # Guarda el resultado en ~/resultados/<Paquete>_cluster.txt y a la vez lo
    # muestra en pantalla (tee). Para traerlo a Windows, desde PowerShell:
    #   scp hadoop@192.168.56.10:~/resultados/*.txt C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\resultados\
    mkdir -p "$HOME/resultados"
    ARCHIVO="$HOME/resultados/${PAQUETE}_cluster.txt"
    echo ""
    {
        echo "Consulta: $PAQUETE"
        echo "Ejecucion: cluster (master + worker1 + worker2, Hadoop 3.3.6)"
        echo "Fecha: $(date '+%Y-%m-%d %H:%M:%S')"
        awk -v a="$inicio" -v b="$fin" 'BEGIN { printf "Tiempo total: %.2f s\n", b - a }'
        echo ""
        echo "Resultado ($OUTPUT_DIR):"
        hdfs dfs -cat "$OUTPUT_DIR/part-00000" 2>/dev/null

        # El agrupamiento deja sus métricas (Silhouette, Davies-Bouldin) en <salida>_metricas
        if hdfs dfs -test -e "${OUTPUT_DIR}_metricas/part-00000" 2>/dev/null; then
            echo ""
            echo "Metricas (${OUTPUT_DIR}_metricas):"
            hdfs dfs -cat "${OUTPUT_DIR}_metricas/part-00000" 2>/dev/null
        fi
    } | tee "$ARCHIVO"
    echo ""
    echo "Guardado en $ARCHIVO"
}

case "${TARGET,,}" in
    help)   show_help ;;
    status) invoke_status ;;
    run)    invoke_run ;;
    *)      echo "Target desconocido: $TARGET"; show_help ;;
esac
