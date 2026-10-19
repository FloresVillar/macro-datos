package PuntosNegrosPorTramo;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 2 - Reducer: para cada vía calcula la media y la desviación estándar de
// accidentes por tramo, y el z-score de cada tramo.
public class ReducerEstadisticas extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// Con menos de 3 tramos la media y la desviación no dicen nada de la vía
	private static final int MIN_TRAMOS = 3;

	// z = (accidentes del tramo - media de la vía) / desviación de la vía:
	// cuántas desviaciones está el tramo por encima de lo normal en su propia vía.
	// Solo cuentan los tramos con al menos 1 accidente (los demás no salen del job 1).
	public void reduce(Text via, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		// Cada elemento: [0] tramo, [1] accidentes, [2] fall, [3] her, [4] lat, [5] lon, [6] depto
		List<String[]> tramos = new ArrayList<String[]>();
		double suma = 0;
		double sumaCuadrados = 0;
		while (valores.hasNext()) {
			String[] partes = valores.next().toString().split("\\|");
			double accidentes = Double.parseDouble(partes[1]);
			suma += accidentes;
			sumaCuadrados += accidentes * accidentes;
			tramos.add(partes);
		}

		int n = tramos.size();
		if (n < MIN_TRAMOS) {
			return;
		}
		double media = suma / n;
		// Desviación estándar poblacional (se tienen todos los tramos con accidentes de la vía)
		double desviacion = Math.sqrt(sumaCuadrados / n - media * media);
		if (desviacion == 0) {
			return; // todos los tramos iguales: ninguno destaca
		}

		for (String[] t : tramos) {
			double z = (Double.parseDouble(t[1]) - media) / desviacion;
			String estadisticas = String.format(Locale.US, "%.4f|%.4f|%.4f", media, desviacion, z);
			output.collect(new Text(via.toString() + "|" + t[0]),
					new Text(t[1] + "|" + t[2] + "|" + t[3] + "|" + t[4] + "|" + t[5] + "|" + t[6] + "|" + estadisticas));
		}
	}
}
