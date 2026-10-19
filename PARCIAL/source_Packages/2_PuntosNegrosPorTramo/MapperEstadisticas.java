package PuntosNegrosPorTramo;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 2 - Mapper: lee la salida del job 1
// ("Vía|tramo <TAB> accidentes|fallecidos|heridos|lat|lon|departamento").
public class MapperEstadisticas extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Cambia la key a solo la vía, para que el Reducer reciba juntos todos los
	// tramos de esa vía. El value se reenvía como "tramo|valores del job 1".
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t"); // [0] = "Vía|tramo", [1] = valores
		if (partes.length != 2) {
			return;
		}
		String[] clave = partes[0].split("\\|", 2); // [0] = vía, [1] = tramo
		if (clave.length == 2) {
			output.collect(new Text(clave[0]), new Text(clave[1] + "|" + partes[1].trim()));
		}
	}
}
