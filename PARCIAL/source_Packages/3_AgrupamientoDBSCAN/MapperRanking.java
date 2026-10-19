package AgrupamientoDBSCAN;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de ranking - Mapper: lee el resumen por grupo
// ("grupo <TAB> accidentes|lat|lon|fallecidos|heridos|departamento").
public class MapperRanking extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Emite todos los grupos con la misma key fija "Grupos", para que un solo
	// Reducer los ordene por tamaño y les ponga número.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t", 2);
		if (partes.length == 2) {
			output.collect(new Text("Grupos"), new Text(partes[0] + "|" + partes[1].trim()));
		}
	}
}
