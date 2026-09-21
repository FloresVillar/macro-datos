package PromedioLatitudPorRegion;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	// Calcula el promedio de latitud de cada región. Este resultado (chico,
	// solo 1 fila por región) es el que va a leer el segundo job de la
	// cadena (ClasificarNorteSurRegion) vía DistributedCache.
	public void reduce(Text region, Iterator<DoubleWritable> valores, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
		int cantidad = 0;
		double suma = 0;
		while (valores.hasNext()) {
			suma += valores.next().get();
			cantidad++;
		}
		output.collect(region, new DoubleWritable(suma / cantidad));
	}
}
