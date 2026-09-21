package ExtremosGeograficosPorRegion;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Emite la Región como key, y como value un texto compuesto
	// "latitud|nombreDelRecurso" — el Reducer necesita ambos datos juntos:
	// la latitud para comparar, y el nombre para poder decir CUÁL recurso es
	// el más al norte/sur, no solo el número.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 9) {
			return;
		}
		String region = columnas[0].trim();       // columna 0 = REGIÓN
		String nombre = columnas[4].trim();        // columna 4 = NOMBRE DEL RECURSO
		String latitudTexto = columnas[9].trim();  // columna 9 = LATITUD
		if (latitudTexto.isEmpty()) {
			return; // fila sin coordenadas registradas
		}
		try {
			Double.parseDouble(latitudTexto); // solo para validar que es numérico
			output.collect(new Text(region), new Text(latitudTexto + "|" + nombre));
		} catch (NumberFormatException e) {
			// valor no numérico, se ignora
		}
	}
}
