package RecursosPorRegionCategoria;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable uno = new IntWritable(1);

	// El CSV de turismo usa ";" como separador (no ","), a diferencia del de SalesJam.
	// key.get() == 0 es la posición en bytes de la primera línea del archivo — es la
	// forma más segura de saltar la cabecera, sin depender de comparar texto (que
	// podría fallar si hay problemas de codificación de acentos).
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		String region = columnas[0].trim();    // columna 0 = REGIÓN
		String categoria = columnas[5].trim(); // columna 5 = CATEGORÍA
		output.collect(new Text(region + "|" + categoria), uno);
	}
}
