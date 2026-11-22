// sales1spark.txt: cantidad de ventas por pais
// Correr desde semana_9: .\spark.ps1 sales1spark-venpa-ventas-por-pais
// sales1spark.txt lee SalesJan2009_04.csv, que no esta en el repo; se usa el de la carpeta Ventas
// (mismas columnas: la 7 es Country). CAMBIAR DE ACUERDO A LA RUTA QUE SE USE.
// Al correr un archivo, spark-shell no imprime el valor de cada linea: por eso println.

val bdsales = sc.textFile("../Ventas File-20260908/Ventas/SalesJan2009.csv")
println(s"bdsales.collect: ${bdsales.count} lineas (con cabecera)")
val mp = bdsales.mapPartitionsWithIndex{ (idx,iter) => if(idx == 0) iter.drop(1) else iter }
println("mp.take(2):")
mp.take(2).foreach(println)
// split por comas FUERA de comillas: con split(",") la fila de precio "13,000"
// corre las columnas y cuenta "CO" (Colorado) como pais en vez de United States.
val venpa = mp.map(s => s.split(""",(?=(?:[^"]*"[^"]*")*[^"]*$)""")(7)).map(word => (word,1)).reduceByKey(_ + _)
println("venpa.collect:")
venpa.collect.foreach(println)
venpa.saveAsTextFile("01output_venta")

System.exit(0)
