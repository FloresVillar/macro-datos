package PaisProductoMayorFacturacion;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 1 - Reducer: facturación total de cada par país+producto.
public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, IntWritable, Text, IntWritable> {

	// Suma todos los precios que llegan con la misma key "Pais|Producto".
	// Salida (archivo intermedio): "Pais|Producto <TAB> montoTotal"
	public void reduce(Text llave, Iterator<IntWritable> valores, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		int suma = 0;
		while (valores.hasNext()) {
			suma += valores.next().get();
		}
		output.collect(llave, new IntWritable(suma));
	}
}
