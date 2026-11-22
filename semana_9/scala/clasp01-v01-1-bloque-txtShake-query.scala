// Cuaderno ClaSp01_V01 - spark_01_base.txt, 1.er bloque: wordcount del manual (partiendo por comas)
// Correr desde semana_9: .\spark.ps1 clasp01-v01-1-bloque-txtShake-query
// Al correr un archivo, spark-shell no imprime el valor de cada linea: por eso println.
// collect se cambio por take(n): collect imprime las 124456 lineas.

val txtShake = sc.textFile("shake.txt")      // el original usa repos.txt, que no existe
println("txtShake.collect (primeras 5 lineas):")
txtShake.take(5).foreach(println)

val query = txtShake.flatMap(line => line.split(",")).map(word => (word,1)).reduceByKey(_ + _)
println("query.toDebugString:")
println(query.toDebugString)
query.cache()

query.saveAsTextFile("output_shake_")
println("query.collect (primeros 10 pares):")
query.take(10).foreach(println)

System.exit(0)
