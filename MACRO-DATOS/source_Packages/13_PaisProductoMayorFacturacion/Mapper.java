package PaisProductoMayorFacturacion;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 1 - Mapper: lee el CSV de ventas.
public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {

	// Arma una key compuesta "Pais|Producto" y emite como value el precio de esa
	// transacción — el Reducer va a sumar todos los precios de cada par país+producto.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		String linea = value.toString();
		String[] columnas = linea.split(",");
		if (columnas.length >= 8 && !columnas[7].trim().equals("Country")) { // saltar la fila de cabecera
			try {
				String producto = columnas[1].trim();                // columna 1 = Product
				int precio = Integer.parseInt(columnas[2].trim());   // columna 2 = Price
				String pais = columnas[7].trim();                    // columna 7 = Country
				output.collect(new Text(pais + "|" + producto), new IntWritable(precio));
			} catch (NumberFormatException e) {
				// fila con datos sucios (precio "13,000" con coma embebida), se ignora
			}
		}
	}
}
