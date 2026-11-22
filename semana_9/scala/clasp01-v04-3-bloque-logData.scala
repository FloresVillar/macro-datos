// Cuaderno ClaSp01_V04 - spark_02.txt, 3.er bloque: Dataset (spark.read.textFile) con filter
// Correr desde semana_9: .\spark.ps1 clasp01-v04-3-bloque-logData
// Al correr un archivo, spark-shell no imprime el valor de cada linea: por eso println.
// collect se cambio por take(n): collect imprime todo en pantalla.

val logFile = "shake.txt"
val logData = spark.read.textFile(logFile).cache()
println(logData)
println("logData.collect (primeras 5):")
logData.take(5).foreach(println)
println(s"logData.count = ${logData.count}")

val contA = logData.filter(line => line.contains("a")).count()
val contB = logData.filter(line => line.contains("b")).count()
println(s"Lineas con a: $contA, lineas con b: $contB")
println(s"logData.first() = ${logData.first()}")
val contW = logData.filter(line => line.contains("without"))
println("contW.collect (primeras 5):")
contW.take(5).foreach(println)
println(s"contW.first = ${contW.first}")
println(s"contW.count = ${contW.count}")

val contThe = logData.filter(line => line.contains("the"))
println("contThe.collect (primeras 5):")
contThe.take(5).foreach(println)
println(s"contThe.first = ${contThe.first}")
println(s"contThe.count = ${contThe.count}")

System.exit(0)
