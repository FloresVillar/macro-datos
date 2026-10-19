package AgrupamientoDBSCAN;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de etiquetado (solo Mapper, sin Reducer): lee el estado final de la
// propagación y deja una línea por accidente con su grupo, que usan el resumen,
// las métricas y el mapa de Power BI.
public class MapperEtiquetado extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Salida: key = id, value = "lat|lon|grupo|fallecidos|heridos|departamento".
	// El punto que terminó sin etiqueta ("-") no es núcleo ni vecino de un núcleo: ruido = -1.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t", 2); // [0] = id, [1] = estado
		if (partes.length != 2) {
			return;
		}
		String[] e = partes[1].split("\\|", -1); // [0] lat, [1] lon, [2] fall, [3] her, [4] depto, [5] tipo, [6] etiqueta
		String grupo = e[6].equals("-") ? "-1" : e[6];
		output.collect(new Text(partes[0]), new Text(e[0] + "|" + e[1] + "|" + grupo + "|" + e[2] + "|" + e[3] + "|" + e[4]));
	}
}
