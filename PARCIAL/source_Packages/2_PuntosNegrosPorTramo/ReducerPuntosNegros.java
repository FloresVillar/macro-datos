package PuntosNegrosPorTramo;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 3 - Reducer: ordena los puntos negros de mayor a menor z-score y escribe
// el ranking con el puesto como key (incluye lat/lon para el mapa de Power BI).
public class ReducerPuntosNegros extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	public void reduce(Text clave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		// Cada elemento: [0] vía, [1] tramo, [2] accidentes, [3] fall, [4] her, [5] lat, [6] lon,
		//                [7] depto, [8] media, [9] desviación, [10] z
		List<String[]> filas = new ArrayList<String[]>();
		while (valores.hasNext()) {
			filas.add(valores.next().toString().split("\\|"));
		}

		// Mayor z primero
		Collections.sort(filas, new Comparator<String[]>() {
			public int compare(String[] a, String[] b) {
				return Double.compare(Double.parseDouble(b[10]), Double.parseDouble(a[10]));
			}
		});

		int puesto = 1;
		for (String[] f : filas) {
			int tramo = Integer.parseInt(f[1]);
			output.collect(new Text(String.valueOf(puesto)),
					new Text(f[0] + "  km=" + (tramo * 10) + "-" + (tramo * 10 + 10) + "  departamento=" + f[7]
							+ "  accidentes=" + f[2] + "  fallecidos=" + f[3] + "  heridos=" + f[4]
							+ "  media_via=" + f[8] + "  desv_via=" + f[9] + "  z=" + f[10]
							+ "  lat=" + f[5] + "  lon=" + f[6]));
			puesto++;
		}
	}
}
