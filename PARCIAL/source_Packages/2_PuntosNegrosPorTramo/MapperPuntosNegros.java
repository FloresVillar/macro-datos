package PuntosNegrosPorTramo;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 3 - Mapper: lee la salida del job 2
// ("Vía|tramo <TAB> accidentes|fall|her|lat|lon|depto|media|desviacion|z").
public class MapperPuntosNegros extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Un tramo es punto negro si está más de 2 desviaciones por encima de la media de su vía
	private static final double Z_UMBRAL = 2.0;

	// Deja pasar solo los puntos negros y los emite con la misma key fija
	// "PuntosNegros", para que un solo Reducer los ordene todos juntos.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t"); // [0] = "Vía|tramo", [1] = valores
		if (partes.length != 2) {
			return;
		}
		String[] valores = partes[1].trim().split("\\|");
		double z = Double.parseDouble(valores[8]);
		if (z > Z_UMBRAL) {
			output.collect(new Text("PuntosNegros"), new Text(partes[0] + "|" + partes[1].trim()));
		}
	}
}
