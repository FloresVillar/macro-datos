package AgrupamientoDBSCAN;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOBS 2..k - Mapper (una vuelta de propagación): lee el estado de cada punto
// ("id <TAB> lat|lon|fall|her|depto|tipo|etiqueta|vecinos").
public class MapperPropagacion extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Reenvía el estado del punto a sí mismo (marca "S") y, si el punto es núcleo,
	// le manda su etiqueta a cada vecino (marca "L"). Solo los núcleos propagan:
	// así un grupo crece solo a través de puntos densos, como en DBSCAN.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t", 2); // [0] = id, [1] = estado
		if (partes.length != 2) {
			return;
		}
		output.collect(new Text(partes[0]), new Text("S|" + partes[1]));
		String[] estado = partes[1].split("\\|", -1); // [5] tipo, [6] etiqueta, [7] vecinos
		if (estado[5].equals("N") && !estado[7].isEmpty()) {
			for (String vecino : estado[7].split(",")) {
				output.collect(new Text(vecino), new Text("L|" + estado[6]));
			}
		}
	}
}
