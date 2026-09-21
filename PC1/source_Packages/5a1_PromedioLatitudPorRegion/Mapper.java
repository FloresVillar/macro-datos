package PromedioLatitudPorRegion;

import java.io.IOException;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, DoubleWritable> {

	// Primer job de la cadena (5a). Agrupa por Región y emite cada latitud
	// individual — el promedio real se calcula en el Reducer, no acá.
	public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 9) {
			return;
		}
		String region = columnas[0].trim();       // columna 0 = REGIÓN
		String latitudTexto = columnas[9].trim();  // columna 9 = LATITUD
		if (latitudTexto.isEmpty()) {
			return; // fila sin coordenadas
		}
		try {
			double latitud = Double.parseDouble(latitudTexto);
			output.collect(new Text(region), new DoubleWritable(latitud));
		} catch (NumberFormatException e) {
			// valor no numérico, se ignora
		}
	}
}
