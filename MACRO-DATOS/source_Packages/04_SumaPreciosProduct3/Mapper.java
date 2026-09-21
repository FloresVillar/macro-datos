package SumaPreciosProduct3;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {

	// Filtra: solo procesa la línea si la columna Product es exactamente "Product3".
	// Las demás líneas se descartan (no se emite nada para ellas).
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		String linea = value.toString();
		String[] columnas = linea.split(",");
		if (columnas[1].trim().equals("Product3")) { // columna 1 = Product
			try {
				int precio = Integer.parseInt(columnas[2].trim()); // columna 2 = Price
				output.collect(new Text("Product3"), new IntWritable(precio));
			} catch (NumberFormatException e) {
				// fila con datos sucios (columna corrida), se ignora
			}
		}
	}
}
