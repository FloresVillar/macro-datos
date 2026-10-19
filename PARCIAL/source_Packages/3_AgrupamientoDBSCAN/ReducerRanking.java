package AgrupamientoDBSCAN;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de ranking - Reducer: ordena los grupos de mayor a menor cantidad de
// accidentes, los numera (grupo 1 = el más grande) y deja el ruido al final.
public class ReducerRanking extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	public void reduce(Text clave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		// Cada elemento: [0] etiqueta DBSCAN, [1] accidentes, [2] lat, [3] lon, [4] fall, [5] her, [6] depto
		List<String[]> grupos = new ArrayList<String[]>();
		String[] ruido = null;
		while (valores.hasNext()) {
			String[] g = valores.next().toString().split("\\|", -1);
			if (g[0].equals("-1")) {
				ruido = g;
			} else {
				grupos.add(g);
			}
		}

		// Más accidentes primero
		Collections.sort(grupos, new Comparator<String[]>() {
			public int compare(String[] a, String[] b) {
				return Integer.compare(Integer.parseInt(b[1]), Integer.parseInt(a[1]));
			}
		});

		int numero = 1;
		for (String[] g : grupos) {
			output.collect(new Text("grupo " + numero),
					new Text("accidentes=" + g[1] + "  centroide_lat=" + g[2] + "  centroide_lon=" + g[3]
							+ "  departamento=" + g[6] + "  fallecidos=" + g[4] + "  heridos=" + g[5]
							+ "  etiqueta_dbscan=" + g[0]));
			numero++;
		}
		if (ruido != null) {
			output.collect(new Text("ruido"), new Text("accidentes=" + ruido[1] + "  fallecidos=" + ruido[4] + "  heridos=" + ruido[5]));
		}
	}
}
