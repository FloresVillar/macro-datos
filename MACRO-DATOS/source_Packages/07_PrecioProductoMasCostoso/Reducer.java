package PrecioProductoMasCostoso;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, IntWritable, Text, IntWritable> {

	// Recorre todos los precios que llegaron y se queda con el más alto.
	public void reduce(Text llave, Iterator<IntWritable> valores, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		int maximo = Integer.MIN_VALUE;
		while (valores.hasNext()) {
			int valor = valores.next().get();
			if (valor > maximo) {
				maximo = valor;
			}
		}
		output.collect(llave, new IntWritable(maximo));
	}
}
