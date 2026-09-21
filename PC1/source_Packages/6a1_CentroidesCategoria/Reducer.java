package CentroidesCategoria;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// Promedia lat/long de todos los recursos de cada categoría, dando el
	// "centroide" (centro geográfico promedio) de esa categoría. Este
	// resultado chico (solo 5 filas, una por categoría) es el que va a leer
	// el segundo job de la cadena (ClasificarPorCentroideCategoria).
	public void reduce(Text categoria, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		int n = 0;
		double sumaLat = 0, sumaLon = 0;
		while (valores.hasNext()) {
			String[] partes = valores.next().toString().split(",");
			sumaLat += Double.parseDouble(partes[0]);
			sumaLon += Double.parseDouble(partes[1]);
			n++;
		}
		output.collect(categoria, new Text((sumaLat / n) + "," + (sumaLon / n)));
	}
}
