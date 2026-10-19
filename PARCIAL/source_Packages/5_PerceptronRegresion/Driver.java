package PerceptronRegresion;

// Consulta 5: perceptrón multicapa de regresión entrenado con gradiente
// descendente clásico en MapReduce. Pregunta: ¿cuántos heridos tendrá el accidente?
// Se predice log(1 + heridos) (muchos ceros y valores de hasta 71).
// La red, los jobs y el ciclo de épocas están en el paquete PerceptronComun.
// Uso: <Driver> <entrada> <salida> [epocas] [tasa]
public class Driver {

	// Valores por defecto (ver la guía, sección 2.3)
	private static final int EPOCAS = 50;
	private static final double TASA = 0.3;

	public static void main(String[] args) {
		// El peso de clase no se usa en regresión
		PerceptronComun.Entrenamiento.ejecutar(args, "regresion", EPOCAS, TASA, 1.0);
	}
}
