package RecursosPorProvinciaTipo;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable uno = new IntWritable(1);

	// Misma idea que RecursosPorRegionCategoria, pero agrupando por Provincia
	// [1] + Tipo de Categoría [6] en vez de Región + Categoría general.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		String provincia = columnas[1].trim();      // columna 1 = PROVINCIA
		String tipoCategoria = columnas[6].trim();  // columna 6 = TIPO DE CATEGORÍA
		output.collect(new Text(provincia + "|" + tipoCategoria), uno);
	}
}
