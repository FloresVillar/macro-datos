package PuntosNegrosPorTramo;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 1 - Mapper: lee el CSV de accidentes de SUTRAN (separador ";").
public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Tamaño del tramo en km: un accidente en el km 604.5 cae en el tramo 60 (km 600-610)
	private static final double TAMANO_TRAMO_KM = 10.0;

	// Emite "Vía|tramo" como key y "fallecidos|heridos|lat|lon|departamento"
	// como value, un registro por accidente.
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
		String latitudTexto = columnas[7].trim();    // columna 7 = LATITUD
		String longitudTexto = columnas[8].trim();   // columna 8 = LONGITUD
		String progresivaTexto = columnas[9].trim(); // columna 9 = PROGRESIVA (km de la vía)
		String via = columnas[10].trim();            // columna 10 = CODIGO_VIA
		String departamento = columnas[11].trim();   // columna 11 = DEPARTAMENTO
		if (via.isEmpty() || progresivaTexto.isEmpty()) {
			return; // 80 filas sin PROGRESIVA y 31 sin CODIGO_VIA
		}
		if (via.equals("VIA URBANA")) {
			return; // calles urbanas: el km no tiene sentido como tramo de carretera
		}
		try {
			int fallecidos = Integer.parseInt(fallecidosTexto);
			int heridos = Integer.parseInt(heridosTexto);
			double latitud = Double.parseDouble(latitudTexto);
			double longitud = Double.parseDouble(longitudTexto);
			double progresiva = Double.parseDouble(progresivaTexto);
			int tramo = (int) Math.floor(progresiva / TAMANO_TRAMO_KM);
			output.collect(new Text(via + "|" + tramo),
					new Text(fallecidos + "|" + heridos + "|" + latitud + "|" + longitud + "|" + departamento));
		} catch (NumberFormatException e) {
			// valor no numérico, se ignora
		}
	}
}
