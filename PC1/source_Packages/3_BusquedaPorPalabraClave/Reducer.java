package BusquedaPorPalabraClave;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, IntWritable, Text, IntWritable> {

	// Como la key es la línea completa, cada grupo normalmente tiene un solo
	// "1" (salvo líneas duplicadas exactas) — se suma igual, el trabajo real
	// ya lo hizo el filtro del Mapper.
	public void reduce(Text llave, Iterator<IntWritable> valores, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		int total = 0;
		while (valores.hasNext()) {
			total += valores.next().get();
		}
		output.collect(llave, new IntWritable(total));
	}
}
