// Cuaderno ClaSp01_V03 - spark_01_base.txt, 3.er bloque: if, while, foreach, for/yield, funciones
// Correr desde semana_9: .\spark.ps1 clasp01-v03-3-bloque-if-while-area
// Al correr un archivo no se puede repetir una declaracion: las variables que el cuaderno
// vuelve a declarar se reasignan (a, arg son var) o se renombran (s -> s2, i -> i2).

var a = 8
val s = if (a>1 && a<9) 1 else 0
println(s"a = $a -> s = $s")

a = 9                            // en el cuaderno: var a = 9
val s2 = if (a>1 && a<9) 1 else 0   // en el cuaderno: val s
println(s"a = $a -> s = $s2")

var arg = "dogo"
var i = 0
while(i<arg.length){
	println(arg(i))
	i += 1
}

arg = "SCALA"                    // en el cuaderno: var arg = "SCALA"
arg.foreach(println)

val mi_array = Array(1,2,3,4,5)
println((for (x <- mi_array) yield x * 2).mkString("Array(", ", ", ")"))

def area(radius:Int):Double = {
	7.20*radius*radius
}
var hola = area(4)
println(s"area(4) = $hola")

def concatenar(arg1: String, arg2: String="CLASE", arg3: String="labo") = println (arg1+arg2+arg3)
concatenar("BigData")
concatenar("BigData", arg3 = "spark")
concatenar("")

def rect_area(length: Float, breadth:Float){val area = length*breadth; println(area)}
rect_area(6,4)

var i2 = Array("Hello","Bye")    // en el cuaderno: var i (i ya es un Int)
println(i2(0))
println(i2(1))

System.exit(0)
