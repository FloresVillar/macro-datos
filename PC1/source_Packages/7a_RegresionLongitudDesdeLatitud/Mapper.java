package RegresionLongitudDesdeLatitud;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Emite cada par (latitud, longitud) válido bajo una key fija ("ALL"),
	// para que el Reducer reciba todos los puntos juntos y pueda calcular
	// la regresión lineal sobre el conjunto completo.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 10) {
			return;
		}
		String lat = columnas[9].trim();
		String lon = columnas[10].trim();
		if (lat.isEmpty() || lon.isEmpty()) {
			return; // fila sin coordenadas
		}
		try {
			Double.parseDouble(lat);
			Double.parseDouble(lon);
			output.collect(new Text("ALL"), new Text(lat + "," + lon));
		} catch (NumberFormatException e) {
			// valores no numéricos, se ignora
		}
	}
}
