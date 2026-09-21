package PrecioProductoMasCostoso;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {

	// Emite todos los precios con la misma key fija "Max", para que el Reducer
	// los compare todos juntos y se quede con el más alto.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		String linea = value.toString();
		String[] columnas = linea.split(",");
		if (!columnas[2].trim().equals("Price")) { // saltar la fila de cabecera
			try {
				int precio = Integer.parseInt(columnas[2].trim()); // columna 2 = Price
				output.collect(new Text("Max"), new IntWritable(precio));
			} catch (NumberFormatException e) {
				// fila con datos sucios (columna corrida), se ignora
			}
		}
	}
}
