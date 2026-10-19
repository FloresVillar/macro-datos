package SeveridadPorVia;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 3 - Mapper: lee la salida del job 2
// ("Departamento <TAB> Vía|accidentes|fallecidos|heridos|indice").
public class MapperRanking extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Emite todas las líneas con la misma key fija "Ranking", para que un solo
	// Reducer las reciba todas juntas y pueda ordenar entre departamentos.
	// El value se reenvía como "Departamento|Vía|accidentes|fallecidos|heridos|indice".
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t"); // [0] = departamento, [1] = valores
		if (partes.length == 2) {
			output.collect(new Text("Ranking"), new Text(partes[0] + "|" + partes[1].trim()));
		}
	}
}
