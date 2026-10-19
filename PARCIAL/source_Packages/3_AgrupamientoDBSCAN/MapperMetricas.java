package AgrupamientoDBSCAN;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de métricas - Mapper: lee los puntos etiquetados y, con todos los puntos
// agrupados cargados en memoria (configure), calcula para cada punto:
//   - su silueta s(i) = (b - a) / max(a, b)
//       a = distancia media a los puntos de su grupo
//       b = menor distancia media a los puntos de otro grupo
//   - su distancia al centroide de su grupo (para Davies-Bouldin)
public class MapperMetricas extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	private Puntos puntos;
	private Map<String, Integer> indiceGrupo = new HashMap<String, Integer>();
	private double[] centroLat;
	private double[] centroLon;

	public void configure(JobConf job) {
		try {
			puntos = Puntos.cargar(job);
		} catch (IOException e) {
			throw new RuntimeException("No se pudieron cargar los puntos etiquetados", e);
		}
		// Índice numérico por grupo y centroides (lat/lon promedio)
		for (String g : puntos.grupo) {
			if (!indiceGrupo.containsKey(g)) {
				indiceGrupo.put(g, indiceGrupo.size());
			}
		}
		int k = indiceGrupo.size();
		centroLat = new double[k];
		centroLon = new double[k];
		int[] n = new int[k];
		for (int i = 0; i < puntos.grupo.size(); i++) {
			int g = indiceGrupo.get(puntos.grupo.get(i));
			centroLat[g] += puntos.lat.get(i);
			centroLon[g] += puntos.lon.get(i);
			n[g]++;
		}
		for (int g = 0; g < k; g++) {
			centroLat[g] /= n[g];
			centroLon[g] /= n[g];
		}
	}

	// Salida, todo con la key fija "Metricas" para que un solo Reducer junte los resultados:
	//   "S|silueta"                    por cada punto agrupado
	//   "D|grupo|distancia al centroide" por cada punto agrupado
	//   "R|1"                          por cada punto de ruido (solo se cuenta)
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t", 2);
		if (partes.length != 2) {
			return;
		}
		String[] v = partes[1].split("\\|", -1); // [0] lat, [1] lon, [2] grupo
		if (v[2].equals("-1")) {
			output.collect(new Text("Metricas"), new Text("R|1"));
			return;
		}
		double lat = Double.parseDouble(v[0]);
		double lon = Double.parseDouble(v[1]);
		int propio = indiceGrupo.get(v[2]);

		// Suma y cantidad de distancias de este punto a cada grupo
		int k = indiceGrupo.size();
		double[] suma = new double[k];
		int[] cantidad = new int[k];
		for (int j = 0; j < puntos.grupo.size(); j++) {
			int g = indiceGrupo.get(puntos.grupo.get(j));
			suma[g] += Distancia.km(lat, lon, puntos.lat.get(j), puntos.lon.get(j));
			cantidad[g]++;
		}
		cantidad[propio]--; // el propio punto (distancia 0) no cuenta en a

		double silueta = 0; // por convención, 0 si el punto está solo en su grupo
		if (cantidad[propio] > 0 && k > 1) {
			double a = suma[propio] / cantidad[propio];
			double b = Double.MAX_VALUE;
			for (int g = 0; g < k; g++) {
				if (g != propio && cantidad[g] > 0) {
					b = Math.min(b, suma[g] / cantidad[g]);
				}
			}
			double mayor = Math.max(a, b);
			silueta = mayor == 0 ? 0 : (b - a) / mayor;
		}
		output.collect(new Text("Metricas"), new Text("S|" + silueta));
		output.collect(new Text("Metricas"), new Text("D|" + v[2] + "|" + Distancia.km(lat, lon, centroLat[propio], centroLon[propio])));
	}
}
