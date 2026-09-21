package RecursosPorDistritoSubtipo;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable uno = new IntWritable(1);

	// Un nivel más de detalle que 1b: agrupa por Distrito [2] + Sub Tipo de
	// Categoría [7] (la clasificación más específica del dataset).
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		String distrito = columnas[2].trim();       // columna 2 = DISTRITO
		String subTipo = columnas[7].trim();        // columna 7 = SUB TIPO CATEGORÍA
		output.collect(new Text(distrito + "|" + subTipo), uno);
	}
}
