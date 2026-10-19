package PerceptronComun;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de época - Reducer (uno solo): suma los gradientes parciales de todas las
// map tasks. Salida: "gradiente <TAB> perdida_total|ejemplos|g0,...,g160".
// El Driver lee esta línea, divide por la cantidad de ejemplos y actualiza los pesos.
public class ReducerEpoca extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	public void reduce(Text clave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		double[] gradiente = new double[Red.PESOS];
		double perdida = 0;
		long ejemplos = 0;
		while (valores.hasNext()) {
			String[] v = valores.next().toString().split("\\|");
			perdida += Double.parseDouble(v[0]);
			ejemplos += Long.parseLong(v[1]);
			double[] parcial = Red.desdeTexto(v[2]);
			for (int k = 0; k < Red.PESOS; k++) {
				gradiente[k] += parcial[k];
			}
		}
		output.collect(clave, new Text(perdida + "|" + ejemplos + "|" + Red.aTexto(gradiente)));
	}
}
