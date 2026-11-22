# instalacion spark3.3.2.pdf

## Manual de instalación de Spark 3.3.2 con Hadoop 3.3.0

1. INGRESAR A LA PAGINA DE APACHE SPARK PARA DESCARGAR SPARK • https://archive.apache.org/dist/spark/spark-3.3.2/ (Spark spark-3.3.2- bin-hadoop3.tgz) • https://archive.apache.org/dist/spark/spark-3.0.1/ (spark-3.0.1-bin- hadoop2.7.tgz) • Descargar el archive y descomprimir

Copiar a la Unidad C bajo la estructura

c:- +spark +bin +conf +...

2. Ingresar a la web de Scala para descargar Scala

• Instalar scala-2.12.15 para Hadoop3.3 • Instalar scala-2.12.10 para Hadoop 2.7

3. VARIABLES DE USUARIO • JAVA_HOME C:\java • SCALA_HOME C:\Program Files (x86)\scala • SPARK_HOME C:\spark

4. VARIABLES DEL SISTEMA PATH • C:\java\bin • C:\spark\bin

5. Ejecutar los siguientes commandos:

cd C:\spark\bin run-example SparkPi 10

val txtShake = sc.textFile("repos.txt") txtShake.collect

val query = txtShake.flatMap(line => line.split(",")).map(word => (word,1)).reduceByKey(_ + _) query.toDebugString query.cache()

query.saveAsTextFile("output_shake_") query.collect
