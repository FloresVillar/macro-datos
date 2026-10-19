package SeveridadPorVia;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 2 - Mapper: lee la salida del job 1
// ("Departamento|Vía <TAB> accidentes|fallecidos|heridos|indice").
public class MapperMaximo extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Cambia la key a solo el departamento, para que el Reducer reciba juntas
	// todas las vías de ese departamento. El value se reenvía como
	// "Vía|accidentes|fallecidos|heridos|indice".
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t"); // [0] = "Departamento|Vía", [1] = valores
		if (partes.length != 2) {
			return;
		}
		String[] clave = partes[0].split("\\|", 2); // [0] = departamento, [1] = vía
		if (clave.length == 2) {
			output.collect(new Text(clave[0]), new Text(clave[1] + "|" + partes[1].trim()));
		}
	}
}
