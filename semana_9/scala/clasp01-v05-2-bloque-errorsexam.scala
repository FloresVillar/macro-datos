// Cuaderno ClaSp01_V05 - spark_02.txt, 2.o bloque: filter con startsWith / contains
// Correr desde semana_9: .\spark.ps1 clasp01-v05-2-bloque-errorsexam
// Al correr un archivo, spark-shell no imprime el valor de cada linea: por eso println.
// collect se cambio por take(n): collect imprime todo en pantalla.

val lines = sc.textFile("shake.txt")
println("lines.collect (primeras 5):")
lines.take(5).foreach(println)
val errorsexam = lines.filter(_.startsWith("The"))
println(errorsexam)
println("errorsexam.collect (primeras 5):")
errorsexam.take(5).foreach(println)
errorsexam.persist()
println(s"errorsexam.count() = ${errorsexam.count()}")

val lineas = sc.textFile("shake.txt")
println(s"lineas.count = ${lineas.count}")

val errorsexam02 = lineas.filter(_.contains("they"))
println("errorsexam02.collect (primeras 5):")
errorsexam02.take(5).foreach(println)
println(s"errorsexam02.count = ${errorsexam02.count}")

val errorsexam03 = lineas.filter(_.contains("They"))
println("errorsexam03.collect (primeras 5):")
errorsexam03.take(5).foreach(println)
println(s"errorsexam03.count = ${errorsexam03.count}")

// (2) y (4) = 3a y 5a palabra: si una linea tiene menos palabras da ArrayIndexOutOfBoundsException.
// Try atrapa ese error para mostrarlo; sin Try el archivo se corta ahi y no llega a System.exit.
import scala.util.Try

val errorsexam04 = errorsexam02.map(_.split(" ")(2))
println("errorsexam04.collect (primeras 5):")
Try(errorsexam04.take(5).foreach(println)).failed.foreach(e => println("fallo: " + e.getClass.getSimpleName))
println(s"errorsexam04.count = " + Try(errorsexam04.count).getOrElse("ERROR"))
Try(errorsexam04.collect).failed.foreach(e => println("errorsexam04.collect fallo: " + e.getClass.getSimpleName))

val errorsexam05 = errorsexam02.map(_.split(" ")(4))
println("errorsexam05.collect (primeras 5):")
Try(errorsexam05.take(5).foreach(println)).failed.foreach(e => println("fallo: " + e.getClass.getSimpleName))
println(s"errorsexam05.count = " + Try(errorsexam05.count).getOrElse("ERROR"))
Try(errorsexam05.collect).failed.foreach(e => println("errorsexam05.collect fallo: " + e.getClass.getSimpleName))

System.exit(0)
