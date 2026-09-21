package SumaPreciosPorProductoYPago;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {

	// Arma una key compuesta "Producto-TipoDePago" (ej. "Product1-Visa") para que
	// el Reducer sume el precio agrupando por esa combinación específica.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		String linea = value.toString();
		String[] columnas = linea.split(",");
		if (!columnas[2].trim().equals("Price")) { // saltar la fila de cabecera
			try {
				String producto = columnas[1].trim(); // columna 1 = Product
				String tipoPago = columnas[3].trim(); // columna 3 = Payment_Type
				int precio = Integer.parseInt(columnas[2].trim()); // columna 2 = Price
				output.collect(new Text(producto + "-" + tipoPago), new IntWritable(precio));
			} catch (NumberFormatException e) {
				// fila con datos sucios (columna corrida), se ignora
			}
		}
	}
}
