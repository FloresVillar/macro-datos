#
## Powershell modo administrador
**ir a la carpeta de trabajo**
- cd C:\Users\esauf\Desktop\uni\macro-datos

**ejecutar el target que dirige a "C:\Hadoop3\sbin"**
- Es donde estan los .sh
- ejecutar .\hadoop.ps1 start  (revisa el codigo de ser necesario)

hadoop fs -mkdir /input_dir

**ese .txt debe estar en C**
hadoop fs -put C:/input_file.txt /input_dir
hadoop fs -ls /input_dir/

 /input_dir no es una carpeta de Windows (no es relativo a C:\ ni a la carpeta donde estás parado). Es una ruta dentro del namespace de HDFS, un sistema de archivos completamente separado y virtual que vive "encima" de Windows.

Por eso no importa desde qué carpeta corras el comando (macro-datos, sbin, o cualquier otra) — hadoop fs -mkdir /input_dir siempre apunta al mismo lugar: la raíz / de HDFS, definida en tu core-site.xml:
<value>hdfs://0.0.0.0:19000</value>

**ese .jar debe estar en C**

hadoop dfs -cat /input_dir/input_file.txt
hadoop jar C:/MapReduceClient.jar wordcount /input_dir /output_dir
hadoop dfs -cat /output_dir/*

**mas comandos en**

semana_1\02 TestHadoop.md