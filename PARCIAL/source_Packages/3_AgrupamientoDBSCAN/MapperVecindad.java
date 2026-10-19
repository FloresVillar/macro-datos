package AgrupamientoDBSCAN;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 1 - Mapper: lee el CSV de accidentes de SUTRAN (separador ";") y reparte
// cada accidente en una rejilla de celdas de lado >= eps.
public class MapperVecindad extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	private double celdaGrados;

	// Lado de la celda en grados tal que mida al menos eps km en latitud y en
	// longitud en todo el Perú (latitud hasta 18.3° S; se usa cos(19°) como cota):
	// así dos puntos a menos de eps siempre están en la misma celda o en celdas vecinas.
	public void configure(JobConf job) {
		double epsKm = Double.parseDouble(job.get("dbscan.eps.km"));
		celdaGrados = epsKm / (111.32 * Math.cos(Math.toRadians(19.0)));
	}

	// Emite el accidente a su celda con la marca "H" (de casa) y a las 8 celdas
	// vecinas con la marca "G" (copia): así el Reducer de cada celda tiene sus
	// puntos y todos los que podrían estar a menos de eps de ellos.
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String[] columnas = value.toString().split(";", -1); // -1 conserva los campos vacíos
		if (columnas.length < 14) {
			return;
		}
		String id = columnas[1].trim();             // columna 1 = NRO_REPORTE (único)
		String fallecidos = columnas[5].trim();     // columna 5 = CANT_FALLECIDOS
		String heridos = columnas[6].trim();        // columna 6 = CANT_HERIDOS
		String latitudTexto = columnas[7].trim();   // columna 7 = LATITUD
		String longitudTexto = columnas[8].trim();  // columna 8 = LONGITUD
		String departamento = columnas[11].trim();  // columna 11 = DEPARTAMENTO
		try {
			double latitud = Double.parseDouble(latitudTexto);
			double longitud = Double.parseDouble(longitudTexto);
			Integer.parseInt(fallecidos); // solo para validar que son numéricos
			Integer.parseInt(heridos);
			long cx = (long) Math.floor(longitud / celdaGrados);
			long cy = (long) Math.floor(latitud / celdaGrados);
			String datos = id + "|" + latitudTexto + "|" + longitudTexto + "|" + fallecidos + "|" + heridos + "|" + departamento;
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					String marca = (dx == 0 && dy == 0) ? "H" : "G";
					output.collect(new Text((cx + dx) + "," + (cy + dy)), new Text(marca + "|" + datos));
				}
			}
		} catch (NumberFormatException e) {
			// valor no numérico, se ignora
		}
	}
}
