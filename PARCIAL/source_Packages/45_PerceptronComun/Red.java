package PerceptronComun;

import java.util.Locale;
import java.util.Random;

// Perceptrón multicapa de 18 entradas -> 8 neuronas ocultas (tanh) -> 1 salida.
//   clasificación: salida sigmoide (probabilidad de que haya fallecidos), entropía cruzada con peso
//   regresión:     salida lineal (log(1 + heridos)), error cuadrático
// Todos los pesos van en un solo arreglo, para pasarlos a los Mappers como texto:
//   [0 .. 143]   W1 (8 x 18), índice j*18 + i
//   [144 .. 151] b1 (8)
//   [152 .. 159] W2 (8)
//   [160]        b2
public class Red {

	public static final int OCULTAS = 8;
	public static final int ENTRADAS = Variables.CANTIDAD;
	public static final int PESOS = OCULTAS * ENTRADAS + 2 * OCULTAS + 1; // 161

	private static final int B1 = OCULTAS * ENTRADAS;
	private static final int W2 = B1 + OCULTAS;
	private static final int B2 = W2 + OCULTAS;

	// Pesos iniciales al azar en [-0.5, 0.5] con semilla fija: el entrenamiento es reproducible
	public static double[] inicializar(long semilla) {
		Random r = new Random(semilla);
		double[] w = new double[PESOS];
		for (int k = 0; k < PESOS; k++) {
			w[k] = r.nextDouble() - 0.5;
		}
		return w;
	}

	// Salida de la red para un ejemplo (probabilidad en clasificación, valor en regresión)
	public static double predecir(double[] w, double[] x, boolean clasificacion) {
		double z2 = w[B2];
		for (int j = 0; j < OCULTAS; j++) {
			z2 += w[W2 + j] * Math.tanh(oculta(w, x, j));
		}
		return clasificacion ? sigmoide(z2) : z2;
	}

	// Suma en g el gradiente de la pérdida de un ejemplo (backpropagation) y devuelve su pérdida.
	// pesoPositivo: cuánto más pesa un accidente con fallecidos en la pérdida (clases desbalanceadas).
	public static double acumularGradiente(double[] w, double[] x, double y, boolean clasificacion, double pesoPositivo, double[] g) {
		double[] a = new double[OCULTAS];
		double z2 = w[B2];
		for (int j = 0; j < OCULTAS; j++) {
			a[j] = Math.tanh(oculta(w, x, j));
			z2 += w[W2 + j] * a[j];
		}

		double perdida;
		double dz2; // derivada de la pérdida respecto de z2
		if (clasificacion) {
			double p = sigmoide(z2);
			double peso = y == 1 ? pesoPositivo : 1;
			double pAcotada = Math.min(Math.max(p, 1e-12), 1 - 1e-12); // evita log(0)
			perdida = -peso * (y * Math.log(pAcotada) + (1 - y) * Math.log(1 - pAcotada));
			dz2 = peso * (p - y);
		} else {
			perdida = (z2 - y) * (z2 - y);
			dz2 = z2 - y; // gradiente de 0.5·(z2 - y)²
		}

		g[B2] += dz2;
		for (int j = 0; j < OCULTAS; j++) {
			g[W2 + j] += dz2 * a[j];
			double dz1 = dz2 * w[W2 + j] * (1 - a[j] * a[j]); // tanh'(z) = 1 - tanh(z)²
			g[B1 + j] += dz1;
			for (int i = 0; i < ENTRADAS; i++) {
				g[j * ENTRADAS + i] += dz1 * x[i];
			}
		}
		return perdida;
	}

	public static String aTexto(double[] v) {
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < v.length; k++) {
			if (k > 0) {
				sb.append(",");
			}
			sb.append(String.format(Locale.US, "%.17g", v[k]));
		}
		return sb.toString();
	}

	public static double[] desdeTexto(String texto) {
		String[] partes = texto.split(",");
		double[] v = new double[partes.length];
		for (int k = 0; k < partes.length; k++) {
			v[k] = Double.parseDouble(partes[k]);
		}
		return v;
	}

	private static double oculta(double[] w, double[] x, int j) {
		double z = w[B1 + j];
		for (int i = 0; i < ENTRADAS; i++) {
			z += w[j * ENTRADAS + i] * x[i];
		}
		return z;
	}

	private static double sigmoide(double z) {
		return 1 / (1 + Math.exp(-z));
	}
}
