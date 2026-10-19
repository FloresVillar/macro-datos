package AgrupamientoDBSCAN;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de resumen - Reducer: por grupo calcula cantidad de accidentes, centroide
// (lat/lon promedio), fallecidos, heridos y departamento más frecuente.
public class ReducerResumen extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	public void reduce(Text grupo, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		int accidentes = 0;
		int fallecidos = 0;
		int heridos = 0;
		double sumaLat = 0;
		double sumaLon = 0;
		Map<String, Integer> conteoDepartamento = new HashMap<String, Integer>();

		while (valores.hasNext()) {
			String[] v = valores.next().toString().split("\\|", -1); // [0] lat, [1] lon, [2] fall, [3] her, [4] depto
			sumaLat += Double.parseDouble(v[0]);
			sumaLon += Double.parseDouble(v[1]);
			fallecidos += Integer.parseInt(v[2]);
			heridos += Integer.parseInt(v[3]);
			Integer c = conteoDepartamento.get(v[4]);
			conteoDepartamento.put(v[4], c == null ? 1 : c + 1);
			accidentes++;
		}

		String departamento = "";
		int maximo = 0;
		for (Map.Entry<String, Integer> e : conteoDepartamento.entrySet()) {
			if (e.getValue() > maximo) {
				maximo = e.getValue();
				departamento = e.getKey();
			}
		}

		// Locale.US para que el decimal salga con punto en Windows y en Linux
		String centroide = String.format(Locale.US, "%.5f|%.5f", sumaLat / accidentes, sumaLon / accidentes);
		output.collect(grupo, new Text(accidentes + "|" + centroide + "|" + fallecidos + "|" + heridos + "|" + departamento));
	}
}
