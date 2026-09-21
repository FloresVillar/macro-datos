package DesviacionEstandarPrecios;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, IntWritable, Text, Text> {

	// Acumula cantidad, suma y suma de cuadrados en una sola pasada, y al final
	// calcula media y desviación estándar con la fórmula:
	// desviacion = sqrt( (sumaCuadrados/n) - media^2 )
	public void reduce(Text llave, Iterator<IntWritable> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		int cantidad = 0;
		double suma = 0;
		double sumaCuadrados = 0;

		while (valores.hasNext()) {
			int precio = valores.next().get();
			cantidad++;
			suma += precio;
			sumaCuadrados += (double) precio * precio;
		}

		double media = suma / cantidad;
		double varianza = (sumaCuadrados / cantidad) - (media * media);
		double desviacionEstandar = Math.sqrt(varianza);

		output.collect(llave, new Text("media=" + media + " desviacion_estandar=" + desviacionEstandar + " n=" + cantidad));
	}
}
