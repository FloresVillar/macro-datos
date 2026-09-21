package RecursosPorRegionCategoria;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, IntWritable, Text, IntWritable> {

	// Hadoop ya agrupó todos los pares con la misma key (región+categoría),
	// acá solo se suman los 1's para obtener el conteo de cada grupo.
	public void reduce(Text llave, Iterator<IntWritable> valores, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		int total = 0;
		while (valores.hasNext()) {
			total += valores.next().get();
		}
		output.collect(llave, new IntWritable(total));
	}
}
