package SumaPreciosProductos;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, IntWritable, Text, IntWritable> {

	// Como todos los precios vinieron con la misma key ("Total"), acá llegan
	// todos juntos en un solo grupo — se suman todos y da el total general.
	public void reduce(Text llave, Iterator<IntWritable> valores, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		int total = 0;
		while (valores.hasNext()) {
			total += valores.next().get();
		}
		output.collect(llave, new IntWritable(total));
	}
}
