// Cuaderno ClaSp01_V06 - spark_02.txt, 1.er bloque: conteo de palabras (partiendo por espacios)
// Correr desde semana_9: .\spark.ps1 clasp01-v06-1-bloque-contar
// Al correr un archivo, spark-shell no imprime el valor de cada linea: por eso println.
// collect se cambio por take(n): collect imprime todo en pantalla.

val txtShake = sc.textFile("shake.txt")
println("txtShake.collect (primeras 5 lineas):")
txtShake.take(5).foreach(println)

val contar = txtShake.flatMap(line => line.split(" ")).map(word => (word,1)).reduceByKey(_ + _)

println(s"contar.count = ${contar.count}")
println("contar.collect (primeros 10 pares):")
contar.take(10).foreach(println)
println("contar.toDebugString:")
println(contar.toDebugString)
contar.cache()
contar.saveAsTextFile("output_shake")

System.exit(0)
