package SeveridadPorVia;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 1 - Reducer: por cada "Departamento|Vía" cuenta los accidentes, suma
// fallecidos y heridos, y calcula el índice de severidad (decimal).
public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// Índice = (3·fallecidos + heridos) / accidentes: un fallecido pesa 3 veces
	// más que un herido; dividir entre accidentes da víctimas ponderadas por accidente.
	public void reduce(Text clave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		int accidentes = 0;
		int fallecidos = 0;
		int heridos = 0;

		while (valores.hasNext()) {
			String[] partes = valores.next().toString().split("\\|");
			fallecidos += Integer.parseInt(partes[0]);
			heridos += Integer.parseInt(partes[1]);
			accidentes++;
		}

		double indice = (3.0 * fallecidos + heridos) / accidentes;
		// Locale.US para que el decimal salga con punto en Windows y en Linux
		String indiceTexto = String.format(Locale.US, "%.4f", indice);
		output.collect(clave, new Text(accidentes + "|" + fallecidos + "|" + heridos + "|" + indiceTexto));
	}
}
