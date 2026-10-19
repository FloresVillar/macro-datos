package AgrupamientoDBSCAN;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de resumen - Mapper: lee los puntos etiquetados
// ("id <TAB> lat|lon|grupo|fallecidos|heridos|departamento").
public class MapperResumen extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Cambia la key al grupo, para que el Reducer reciba juntos todos sus puntos.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t", 2);
		if (partes.length != 2) {
			return;
		}
		String[] v = partes[1].split("\\|", -1); // [0] lat, [1] lon, [2] grupo, [3] fall, [4] her, [5] depto
		output.collect(new Text(v[2]), new Text(v[0] + "|" + v[1] + "|" + v[3] + "|" + v[4] + "|" + v[5]));
	}
}
