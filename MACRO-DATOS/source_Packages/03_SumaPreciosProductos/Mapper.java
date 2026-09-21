package SumaPreciosProductos;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {

	// Por cada línea del CSV, toma la columna 3 [2] (Price) y emite (Total, precio).
	// Todos los precios van a la misma key fija "Total", para que el Reducer
	// los sume todos juntos en un único resultado global.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		String linea = value.toString();
		String[] columnas = linea.split(",");
		if (!columnas[2].trim().equals("Price")) { // saltar la fila de cabecera
			try {
				int precio = Integer.parseInt(columnas[2].trim()); // columna 2 = Price
				output.collect(new Text("Total"), new IntWritable(precio));
			} catch (NumberFormatException e) {
				// fila con datos sucios (columna corrida), se ignora
			}
		}
	}
}
