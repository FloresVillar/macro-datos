package PerceptronComun;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de preparación 1 - Reducer: media y desviación estándar de cada variable.
// Salida: key = número de variable, value = "media|desviacion".
public class ReducerEstadisticas extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	public void reduce(Text variable, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		double suma = 0;
		double sumaCuadrados = 0;
		int n = 0;
		while (valores.hasNext()) {
			double x = Double.parseDouble(valores.next().toString());
			suma += x;
			sumaCuadrados += x * x;
			n++;
		}
		double media = suma / n;
		double desviacion = Math.sqrt(Math.max(sumaCuadrados / n - media * media, 0));
		if (desviacion == 0) {
			desviacion = 1; // variable constante: solo se le resta la media
		}
		output.collect(variable, new Text(Double.toString(media) + "|" + Double.toString(desviacion)));
	}
}
