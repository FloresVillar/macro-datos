package ConteoPorProvincia;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// Por cada provincia: cuenta el total de recursos (un valor por cada
	// vez que apareció la provincia) y, con un HashSet, cuántos distritos
	// DISTINTOS hay entre esos recursos. Este resultado chico (~190 filas,
	// una por provincia) es el que va a leer el segundo job de la cadena
	// (RegresionRecursosPorDistritos) directamente como su input.
	public void reduce(Text provincia, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		int cantidadRecursos = 0;
		HashSet<String> distritosDistintos = new HashSet<String>();
		while (valores.hasNext()) {
			distritosDistintos.add(valores.next().toString());
			cantidadRecursos++;
		}
		output.collect(provincia, new Text(cantidadRecursos + "," + distritosDistintos.size()));
	}
}
