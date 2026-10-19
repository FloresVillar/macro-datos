package AgrupamientoDBSCAN;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 1 - Reducer: por cada celda, busca los vecinos (a <= eps km) de cada punto
// de la celda y lo marca como núcleo si tiene al menos minPts puntos en su vecindad.
public class ReducerVecindad extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	private double epsKm;
	private int minPts;

	public void configure(JobConf job) {
		epsKm = Double.parseDouble(job.get("dbscan.eps.km"));
		minPts = Integer.parseInt(job.get("dbscan.minpts"));
	}

	// Salida, una línea por punto de la celda:
	//   key = id, value = "lat|lon|fallecidos|heridos|departamento|tipo|etiqueta|vecinos"
	//   tipo = N (núcleo) o B (no núcleo); etiqueta inicial = su propio id si es núcleo, "-" si no;
	//   vecinos = ids a <= eps separados por coma (sin el propio punto).
	public void reduce(Text celda, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		// Cada elemento: [0] marca H/G, [1] id, [2] lat, [3] lon, [4] fall, [5] her, [6] depto
		List<String[]> puntos = new ArrayList<String[]>();
		while (valores.hasNext()) {
			puntos.add(valores.next().toString().split("\\|", -1));
		}

		for (String[] p : puntos) {
			if (!p[0].equals("H")) {
				continue; // las copias de celdas vecinas solo sirven como posibles vecinos
			}
			double lat = Double.parseDouble(p[2]);
			double lon = Double.parseDouble(p[3]);
			int enVecindad = 1; // DBSCAN cuenta el propio punto
			StringBuilder vecinos = new StringBuilder();
			for (String[] q : puntos) {
				if (q[1].equals(p[1])) {
					continue;
				}
				if (Distancia.km(lat, lon, Double.parseDouble(q[2]), Double.parseDouble(q[3])) <= epsKm) {
					enVecindad++;
					if (vecinos.length() > 0) {
						vecinos.append(",");
					}
					vecinos.append(q[1]);
				}
			}
			boolean nucleo = enVecindad >= minPts;
			output.collect(new Text(p[1]), new Text(p[2] + "|" + p[3] + "|" + p[4] + "|" + p[5] + "|" + p[6]
					+ "|" + (nucleo ? "N" : "B") + "|" + (nucleo ? p[1] : "-") + "|" + vecinos));
		}
	}
}
