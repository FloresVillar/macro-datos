package ExtremosGeograficosPorRegion;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// Para cada región, recorre todos los pares "latitud|nombre" que le
	// llegaron y se queda con el de mayor latitud (el recurso más al norte)
	// y el de menor latitud (el más al sur), guardando en cada caso también
	// el nombre del recurso, no solo el número.
	public void reduce(Text region, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		double maxLat = Double.NEGATIVE_INFINITY;
		double minLat = Double.POSITIVE_INFINITY;
		String nombreMax = "";
		String nombreMin = "";

		while (valores.hasNext()) {
			String[] partes = valores.next().toString().split("\\|", 2);
			double latitud = Double.parseDouble(partes[0]);
			String nombre = partes.length > 1 ? partes[1] : "";
			if (latitud > maxLat) {
				maxLat = latitud;
				nombreMax = nombre;
			}
			if (latitud < minLat) {
				minLat = latitud;
				nombreMin = nombre;
			}
		}

		output.collect(region, new Text("Norte=" + nombreMax + " (" + maxLat + ")  Sur=" + nombreMin + " (" + minLat + ")"));
	}
}
