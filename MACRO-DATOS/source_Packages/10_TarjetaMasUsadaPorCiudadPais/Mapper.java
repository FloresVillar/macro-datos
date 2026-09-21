package TarjetaMasUsadaPorCiudadPais;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Arma una key compuesta "Ciudad|Pais" y emite como value el tipo de tarjeta
	// usado en esa transacción — el Reducer va a contar cuál tarjeta se repite más
	// dentro de cada grupo ciudad+país.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String linea = value.toString();
		String[] columnas = linea.split(",");
		if (!columnas[7].trim().equals("Country")) { // saltar la fila de cabecera
			String ciudad = columnas[5].trim();  // columna 5 = City
			String pais = columnas[7].trim();    // columna 7 = Country
			String tarjeta = columnas[3].trim(); // columna 3 = Payment_Type
			output.collect(new Text(ciudad + "|" + pais), new Text(tarjeta));
		}
	}
}
