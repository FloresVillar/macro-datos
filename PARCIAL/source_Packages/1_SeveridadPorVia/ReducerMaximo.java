package SeveridadPorVia;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 2 - Reducer: para cada departamento elige la vía con el mayor índice.
public class ReducerMaximo extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// Vías con menos accidentes se descartan: con 1 o 2 accidentes el índice
	// depende de un solo evento y no representa a la vía.
	private static final int MIN_ACCIDENTES = 5;

	// En empate de índice gana la vía con más accidentes (más evidencia).
	public void reduce(Text departamento, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String mejor = null;
		double mejorIndice = -1;
		int mejorAccidentes = 0;

		while (valores.hasNext()) {
			String valor = valores.next().toString();
			String[] partes = valor.split("\\|"); // [0] vía, [1] accidentes, [2] fallecidos, [3] heridos, [4] índice
			int accidentes = Integer.parseInt(partes[1]);
			double indice = Double.parseDouble(partes[4]);
			if (accidentes < MIN_ACCIDENTES) {
				continue;
			}
			if (indice > mejorIndice || (indice == mejorIndice && accidentes > mejorAccidentes)) {
				mejor = valor;
				mejorIndice = indice;
				mejorAccidentes = accidentes;
			}
		}

		// Si ninguna vía del departamento llega al mínimo, el departamento no sale
		if (mejor != null) {
			output.collect(departamento, new Text(mejor));
		}
	}
}
