package PuntosNegrosPorTramo;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 1 - Reducer: por cada "Vía|tramo" cuenta los accidentes, suma fallecidos
// y heridos, promedia las coordenadas y toma el departamento más frecuente.
public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	public void reduce(Text clave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		int accidentes = 0;
		int fallecidos = 0;
		int heridos = 0;
		double sumaLatitud = 0;
		double sumaLongitud = 0;
		// Un tramo puede cruzar un límite departamental: se reporta el que más se repite
		Map<String, Integer> conteoDepartamento = new HashMap<String, Integer>();

		while (valores.hasNext()) {
			String[] partes = valores.next().toString().split("\\|"); // [0] fall, [1] her, [2] lat, [3] lon, [4] depto
			fallecidos += Integer.parseInt(partes[0]);
			heridos += Integer.parseInt(partes[1]);
			sumaLatitud += Double.parseDouble(partes[2]);
			sumaLongitud += Double.parseDouble(partes[3]);
			Integer c = conteoDepartamento.get(partes[4]);
			conteoDepartamento.put(partes[4], c == null ? 1 : c + 1);
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
		String latitud = String.format(Locale.US, "%.5f", sumaLatitud / accidentes);
		String longitud = String.format(Locale.US, "%.5f", sumaLongitud / accidentes);
		output.collect(clave, new Text(accidentes + "|" + fallecidos + "|" + heridos + "|" + latitud + "|" + longitud + "|" + departamento));
	}
}
