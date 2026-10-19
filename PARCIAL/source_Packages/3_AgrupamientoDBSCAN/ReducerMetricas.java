package AgrupamientoDBSCAN;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de métricas - Reducer (uno solo): junta las siluetas y las distancias al
// centroide y calcula:
//   - Silhouette = promedio de s(i) de los puntos agrupados (de -1 a 1; mejor cerca de 1)
//   - Davies-Bouldin = promedio sobre grupos de max_j (S_i + S_j) / d(c_i, c_j)
//       S_i = distancia media de los puntos del grupo i a su centroide c_i (mejor cerca de 0)
public class ReducerMetricas extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	private Map<String, double[]> centroides = new HashMap<String, double[]>();

	// Los centroides se recalculan aquí con los mismos puntos que usó el Mapper
	public void configure(JobConf job) {
		Puntos puntos;
		try {
			puntos = Puntos.cargar(job);
		} catch (IOException e) {
			throw new RuntimeException("No se pudieron cargar los puntos etiquetados", e);
		}
		Map<String, Integer> n = new HashMap<String, Integer>();
		for (int i = 0; i < puntos.grupo.size(); i++) {
			String g = puntos.grupo.get(i);
			double[] c = centroides.get(g);
			if (c == null) {
				c = new double[2];
				centroides.put(g, c);
				n.put(g, 0);
			}
			c[0] += puntos.lat.get(i);
			c[1] += puntos.lon.get(i);
			n.put(g, n.get(g) + 1);
		}
		for (Map.Entry<String, double[]> e : centroides.entrySet()) {
			e.getValue()[0] /= n.get(e.getKey());
			e.getValue()[1] /= n.get(e.getKey());
		}
	}

	public void reduce(Text clave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		double sumaSilueta = 0;
		int agrupados = 0;
		int ruido = 0;
		Map<String, double[]> dispersion = new HashMap<String, double[]>(); // grupo -> {suma distancias, cantidad}

		while (valores.hasNext()) {
			String[] v = valores.next().toString().split("\\|");
			if (v[0].equals("S")) {
				sumaSilueta += Double.parseDouble(v[1]);
				agrupados++;
			} else if (v[0].equals("D")) {
				double[] d = dispersion.get(v[1]);
				if (d == null) {
					d = new double[2];
					dispersion.put(v[1], d);
				}
				d[0] += Double.parseDouble(v[2]);
				d[1]++;
			} else {
				ruido++;
			}
		}

		List<String> grupos = new ArrayList<String>(dispersion.keySet());
		double sumaDB = 0;
		for (String gi : grupos) {
			double si = dispersion.get(gi)[0] / dispersion.get(gi)[1];
			double peor = 0;
			for (String gj : grupos) {
				if (gi.equals(gj)) {
					continue;
				}
				double sj = dispersion.get(gj)[0] / dispersion.get(gj)[1];
				double[] ci = centroides.get(gi);
				double[] cj = centroides.get(gj);
				double separacion = Distancia.km(ci[0], ci[1], cj[0], cj[1]);
				if (separacion > 0) {
					peor = Math.max(peor, (si + sj) / separacion);
				}
			}
			sumaDB += peor;
		}

		output.collect(new Text("grupos"), new Text(String.valueOf(grupos.size())));
		output.collect(new Text("puntos_agrupados"), new Text(String.valueOf(agrupados)));
		output.collect(new Text("puntos_ruido"), new Text(String.valueOf(ruido)));
		output.collect(new Text("silhouette"), new Text(String.format(Locale.US, "%.4f", agrupados > 0 ? sumaSilueta / agrupados : 0)));
		output.collect(new Text("davies_bouldin"), new Text(String.format(Locale.US, "%.4f", grupos.size() > 1 ? sumaDB / grupos.size() : 0)));
	}
}
