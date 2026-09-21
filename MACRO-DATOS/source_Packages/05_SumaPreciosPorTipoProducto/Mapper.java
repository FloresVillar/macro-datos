package SumaPreciosPorTipoProducto;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {

	// Por cada línea, toma el nombre del producto (columna 1) como key,
	// y el precio (columna 2) como value — el Reducer va a sumar por producto.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		String linea = value.toString();
		String[] columnas = linea.split(",");
		if (!columnas[2].trim().equals("Price")) { // saltar la fila de cabecera
			try {
				String producto = columnas[1].trim(); // columna 1 = Product
				int precio = Integer.parseInt(columnas[2].trim()); // columna 2 = Price
				output.collect(new Text(producto), new IntWritable(precio));
			} catch (NumberFormatException e) {
				// fila con datos sucios (columna corrida), se ignora
			}
		}
	}
}
