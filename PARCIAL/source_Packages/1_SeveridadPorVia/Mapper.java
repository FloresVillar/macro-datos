package SeveridadPorVia;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 1 - Mapper: lee el CSV de accidentes de SUTRAN (separador ";").
public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Emite "Departamento|Vía" como key y "fallecidos|heridos" como value,
	// un registro por accidente, para que el Reducer cuente y sume por vía.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";", -1); // -1 conserva los campos vacíos
		if (columnas.length < 14) {
			return;
		}
		String fallecidosTexto = columnas[5].trim(); // columna 5 = CANT_FALLECIDOS
		String heridosTexto = columnas[6].trim();    // columna 6 = CANT_HERIDOS
		String via = columnas[10].trim();            // columna 10 = CODIGO_VIA
		String departamento = columnas[11].trim();   // columna 11 = DEPARTAMENTO
		if (via.isEmpty() || departamento.isEmpty()) {
			return; // 31 filas no tienen CODIGO_VIA
		}
		try {
			int fallecidos = Integer.parseInt(fallecidosTexto);
			int heridos = Integer.parseInt(heridosTexto);
			output.collect(new Text(departamento + "|" + via), new Text(fallecidos + "|" + heridos));
		} catch (NumberFormatException e) {
			// valor no numérico, se ignora
		}
	}
}
