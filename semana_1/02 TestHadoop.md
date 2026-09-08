# Test Hadoop

Fuente: GitHub — MuhammadBilalYar/Hadoop-On-Window

## Testing

1. Abrir cmd y escribir el comando:
   ```
   hdfs namenode –format
   ```

2. Abrir cmd, cambiar de directorio a `C:\Hadoop-2.8.0\sbin` y escribir `start-all.cmd` para iniciar apache.

3. Verificar que estas apps estén corriendo:
   - Hadoop Namenode
   - Hadoop datanode
   - YARN Resource Manager
   - YARN Node Manager

4. Abrir: http://localhost:8088

5. Abrir: http://localhost:50070

   > **Nota:** `50070` es el puerto de la UI del NameNode en **Hadoop 2.x** (la versión de este PDF). En **Hadoop 3.3.0** (la versión instalada, `C:\Hadoop3`) ese puerto cambió a **`9870`**. Si `50070` da `ERR_CONNECTION_REFUSED`, usa http://localhost:9870 en su lugar.

**Congratulations, Hadoop installed.**

## How to Run Hadoop wordcount MapReduce Example on Windows 10

### Prepare

1. Descargar `MapReduceClient.jar` (Link: https://github.com/MuhammadBilalYar/HADOOP-INSTALLATION-ON-WINDOW-10/blob/master/MapReduceClient.jar)
2. Descargar `Input_file.txt` (Link: https://github.com/MuhammadBilalYar/HADOOP-INSTALLATION-ON-WINDOW-10/blob/master/input_file.txt)

Colocar ambos archivos en `C:/`

### Hadoop Operation

1. Abrir cmd en modo Administrador, moverse a `C:/Hadoop-2.8.0/sbin` e iniciar el clúster:
   ```
   Start-all.cmd
   ```

2. Crear un directorio de input en HDFS:
   ```
   hadoop fs -mkdir /input_dir
   ```

3. Copiar el archivo de texto `input_file.txt` al directorio de input (`input_dir`) de HDFS:
   ```
   hadoop fs -put C:/input_file.txt /input_dir
   ```

4. Verificar que `input_file.txt` esté disponible en el directorio de input de HDFS:
   ```
   hadoop fs -ls /input_dir/
   ```

5. Verificar el contenido del archivo copiado:
   ```
   hadoop dfs -cat /input_dir/input_file.txt
   ```

6. Correr `MapReduceClient.jar` proveyendo los directorios de input y output:
   ```
   hadoop jar C:/MapReduceClient.jar wordcount /input_dir /output_dir
   ```

7. Verificar el contenido del archivo de output generado:
   ```
   hadoop dfs -cat /output_dir/*
   ```

## Algunos otros comandos útiles

Salir de Safe mode:
```
hadoop dfsadmin –safemode leave
```

Borrar un archivo del directorio HDFS:
```
hadoop fs -rm -r /input_dir/input_file.txt
```

Borrar un directorio de HDFS:
```
hadoop fs -rm -r /input_dir
```
