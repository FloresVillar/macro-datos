package RegresionRecursosPorDistritos;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// A diferencia de los otros Mappers, este NO lee el CSV original — lee
	// directamente el resultado del job anterior (ConteoPorProvincia), que
	// ya viene como "Provincia\tcantidadRecursos,cantidadDistritos". Este es
	// el segundo job de la cadena, encadenado por INPUT (el output de un job
	// es el input del siguiente), en vez de por DistributedCache.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String linea = value.toString();
		String[] partes = linea.split("\t");
		if (partes.length < 2) {
			return;
		}
		String[] numeros = partes[1].split(",");
		String cantidadRecursos = numeros[0];
		String cantidadDistritos = numeros[1];
		// x = cantidad de distritos distintos, y = cantidad de recursos —
		// la pregunta de regresión es "¿más distritos implica más recursos?"
		output.collect(new Text("ALL"), new Text(cantidadDistritos + "," + cantidadRecursos));
	}
}
