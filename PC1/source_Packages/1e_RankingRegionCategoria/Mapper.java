package RankingRegionCategoria;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// A diferencia de 1a-1d, acá no alcanza con contar por grupo — para armar
	// un ranking hay que COMPARAR los conteos entre todas las combinaciones.
	// Por eso todo se manda a una única key fija ("ALL"): así todos los pares
	// región+categoría terminan en el mismo Reducer, que es el único lugar
	// donde se puede comparar todo junto.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		String region = columnas[0].trim();    // columna 0 = REGIÓN
		String categoria = columnas[5].trim(); // columna 5 = CATEGORÍA
		output.collect(new Text("ALL"), new Text(region + "|" + categoria));
	}
}
