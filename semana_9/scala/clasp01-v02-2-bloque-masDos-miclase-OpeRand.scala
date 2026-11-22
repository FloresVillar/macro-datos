// Cuaderno ClaSp01_V02 - spark_01_base.txt, 2.o bloque: bases de Scala
// Correr desde semana_9: .\spark.ps1 clasp01-v02-2-bloque-masDos-miclase-OpeRand
// Al correr un archivo, spark-shell lo compila completo y no deja repetir un val:
// los nombres repetidos del cuaderno se cambiaron (x -> x2, ab1/ab2 -> ab3/ab4).

// Declarar Variable
val x = 6 + 4
println(s"x = $x")

// Imprimir
print({
	val x = 3 + 2
})
println()

print({
	val x = 3 + 2
        x + 7
})
println()

(x :Int) => x + 2

val masDos = (x: Int) => x + 2
println(masDos(3))

val mulCinco = (x: Int) => x * 5
println(mulCinco(7))
println(mulCinco(5))

val larespuesta = () => 12 * 21
println(larespuesta())

print(12)
print("Hola Mundo")
print("Hola" + " Spark")
println()

val x2 = 7 * 3                   // en el cuaderno: val x = 7 * 3
println(x2)
println("la solución es: " + x2)

val mult1 = (x: Int, y: Int) => x * y
println(mult1(3,7))

def mult2(x: Int, y: Int)(aux: Int):
Int = (x * y)*aux
println(mult2(3,5)(2))

def mult3(x: Int, y: Int):
Int = (x * y)
println(mult3(3,9))

class miclase(nombre: String, apellido: String){
	def imprimo(apodo: String):
	Unit = println(nombre + apodo + apellido)
}

val claseA = new miclase("hola","amiga").imprimo("maria")
val claseB = new miclase("hello","amigo")
claseB.imprimo("Juan")

object Colegio{
	private var alumno = 10
	def crear():Int = {
		alumno += 1
		alumno
	}
}

val alumno1: Int = Colegio.crear()
val alumno2: Int = Colegio.crear()
println(s"alumno1 = $alumno1, alumno2 = $alumno2")

object OpeRand{
	private var ale = 1
	def porDos():Int = {ale *= 2; ale}
	def masDos():Int = {ale += 2; ale}
}

val ab1: Int = OpeRand.porDos()
val ab2: Int = OpeRand.masDos()
val ab3: Int = OpeRand.porDos()  // en el cuaderno: val ab1
val ab4: Int = OpeRand.masDos()  // en el cuaderno: val ab2
println(s"object OpeRand: $ab1 $ab2 $ab3 $ab4")

// Tarea: pasar el object OpeRand a class
class OpeRandC{
	private var ale = 1
	def porDos():Int = {ale *= 2; ale}
	def masDos():Int = {ale += 2; ale}
}
val op1 = new OpeRandC
val op2 = new OpeRandC
println(s"class OpeRandC: op1.porDos = ${op1.porDos()}, op1.masDos = ${op1.masDos()}, op2.porDos = ${op2.porDos()}")

// En el cuaderno: :quit
System.exit(0)
