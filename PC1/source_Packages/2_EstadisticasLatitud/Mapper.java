package EstadisticasLatitud;

import java.io.IOException;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, DoubleWritable> {

	// Emite todas las latitudes bajo la misma key fija ("Latitudes"), para que
	// el Reducer las reciba todas juntas y calcule media + desviación estándar
	// en una sola pasada (mismo patrón que DesviacionEstandarPrecios de SalesJam,
	// pero con valores decimales en vez de enteros).
	public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 9) {
			return;
		}
		String latitudTexto = columnas[9].trim(); // columna 9 = LATITUD
		if (latitudTexto.isEmpty()) {
			return; // ~1257 de las 6204 filas no tienen coordenadas registradas
		}
		try {
			double latitud = Double.parseDouble(latitudTexto);
			output.collect(new Text("Latitudes"), new DoubleWritable(latitud));
		} catch (NumberFormatException e) {
			// valor no numérico, se ignora
		}
	}
}
