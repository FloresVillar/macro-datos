package PromedioLongitudPorRegion;

import java.io.IOException;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, DoubleWritable> {

	// Primer job de la cadena (5b). Igual que PromedioLatitudPorRegion pero
	// con la columna Longitud [10] en vez de Latitud [9].
	public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 10) {
			return;
		}
		String region = columnas[0].trim();         // columna 0 = REGIÓN
		String longitudTexto = columnas[9].trim(); // columna 9: la cabecera del CSV dice LATITUD, pero los valores son longitudes (las columnas vienen invertidas en el dataset)
		if (longitudTexto.isEmpty()) {
			return; // fila sin coordenadas
		}
		try {
			double longitud = Double.parseDouble(longitudTexto);
			output.collect(new Text(region), new DoubleWritable(longitud));
		} catch (NumberFormatException e) {
			// valor no numérico, se ignora
		}
	}
}
