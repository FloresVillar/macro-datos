package BusquedaPorPalabraClave;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable uno = new IntWritable(1);
	private String palabraClave;

	// configure() se ejecuta una sola vez, antes de que arranquen los map(),
	// y lee el parámetro "palabraClave" que el Driver guardó en el JobConf —
	// así el término de búsqueda no queda fijo en el código, se puede
	// cambiar en cada ejecución sin recompilar.
	public void configure(JobConf job) {
		this.palabraClave = job.get("palabraClave", "Laguna").toLowerCase();
	}

	// Revisa varios campos de texto (Región, Provincia, Distrito, Nombre del
	// Recurso, Categoría, Tipo y Sub Tipo) buscando la palabra clave en
	// cualquiera de ellos. Si aparece en al menos uno, emite la línea
	// completa — no agrupa ni suma nada, es un filtro de texto.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 7) {
			return;
		}
		String camposDeTexto = (columnas[0] + " " + columnas[1] + " " + columnas[2] + " "
				+ columnas[4] + " " + columnas[5] + " " + columnas[6] + " " + columnas[7]).toLowerCase();
		if (camposDeTexto.contains(palabraClave)) {
			output.collect(new Text(linea), uno);
		}
	}
}
