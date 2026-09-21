package ManifestacionesCulturalesPorRegion;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable uno = new IntWritable(1);

	// Filtra: solo procesa la línea si la Categoría [5] es exactamente
	// "2. MANIFESTACIONES CULTURALES". Las demás categorías se descartan.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		String categoria = columnas[5].trim(); // columna 5 = CATEGORÍA
		if (categoria.equals("2. MANIFESTACIONES CULTURALES")) {
			String region = columnas[0].trim(); // columna 0 = REGIÓN
			output.collect(new Text(region), uno);
		}
	}
}
