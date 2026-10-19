package SeveridadPorVia;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 3 - Reducer: ordena los departamentos de mayor a menor índice de su
// vía más severa y escribe el ranking con el puesto como key.
public class ReducerRanking extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	public void reduce(Text clave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		// Cada elemento: [0] departamento, [1] vía, [2] accidentes, [3] fallecidos, [4] heridos, [5] índice
		List<String[]> filas = new ArrayList<String[]>();
		while (valores.hasNext()) {
			filas.add(valores.next().toString().split("\\|"));
		}

		// Mayor índice primero
		Collections.sort(filas, new Comparator<String[]>() {
			public int compare(String[] a, String[] b) {
				return Double.compare(Double.parseDouble(b[5]), Double.parseDouble(a[5]));
			}
		});

		int puesto = 1;
		for (String[] f : filas) {
			output.collect(new Text(String.valueOf(puesto)),
					new Text(f[0] + "  via=" + f[1] + "  accidentes=" + f[2] + "  fallecidos=" + f[3]
							+ "  heridos=" + f[4] + "  indice=" + f[5]));
			puesto++;
		}
	}
}
