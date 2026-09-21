package ConteoPorProvincia;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Emite (Provincia, Distrito) por cada recurso — el Reducer va a contar
	// cuántos recursos hay en total por provincia, y cuántos DISTINTOS
	// distritos aparecen dentro de esa provincia.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 2) {
			return;
		}
		String provincia = columnas[1].trim(); // columna 1 = PROVINCIA
		String distrito = columnas[2].trim();  // columna 2 = DISTRITO
		output.collect(new Text(provincia), new Text(distrito));
	}
}
