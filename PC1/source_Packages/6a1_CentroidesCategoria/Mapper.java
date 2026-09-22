package CentroidesCategoria;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Primer job de la cadena 6a. Por cada recurso con coordenadas válidas,
	// emite (Categoría, "lat,long") — el Reducer va a promediar esas
	// coordenadas para encontrar el "centro geográfico" de cada categoría.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 10) {
			return;
		}
		String categoria = columnas[5].trim();
		String lat = columnas[10].trim(); // columna 10: la cabecera del CSV dice LONGITUD, pero los valores son latitudes (las columnas vienen invertidas en el dataset)
		String lon = columnas[9].trim();  // columna 9: la cabecera del CSV dice LATITUD, pero los valores son longitudes (las columnas vienen invertidas en el dataset)
		if (lat.isEmpty() || lon.isEmpty()) {
			return; // fila sin coordenadas
		}
		try {
			Double.parseDouble(lat);
			Double.parseDouble(lon);
			output.collect(new Text(categoria), new Text(lat + "," + lon));
		} catch (NumberFormatException e) {
			// valores no numéricos, se ignora
		}
	}
}
