package PerceptronClasificacion;

// Consulta 4: perceptrón multicapa de clasificación entrenado con gradiente
// descendente clásico en MapReduce. Pregunta: ¿el accidente tendrá fallecidos?
// La red, los jobs y el ciclo de épocas están en el paquete PerceptronComun.
// Uso: <Driver> <entrada> <salida> [epocas] [tasa]
public class Driver {

	// Valores por defecto (ver la guía, sección 2.3)
	private static final int EPOCAS = 50;
	private static final double TASA = 0.5;
	// Hay unos 4.8 accidentes sin fallecidos por cada uno con fallecidos (3 172 / 660):
	// un error en un accidente con fallecidos pesa 4.8 veces más, para que la red
	// no aprenda a decir siempre "no hay fallecidos"
	private static final double PESO_POSITIVO = 4.8;

	public static void main(String[] args) {
		PerceptronComun.Entrenamiento.ejecutar(args, "clasificacion", EPOCAS, TASA, PESO_POSITIVO);
	}
}
