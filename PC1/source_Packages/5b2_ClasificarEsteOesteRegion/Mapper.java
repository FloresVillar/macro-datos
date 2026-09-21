package ClasificarEsteOesteRegion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable uno = new IntWritable(1);
	private HashMap<String, Double> promedioPorRegion = new HashMap<String, Double>();

	// Segundo job de la cadena (5b). Igual que ClasificarNorteSurRegion pero
	// leyendo el archivo de promedios de LONGITUD que dejó PromedioLongitudPorRegion.
	public void configure(JobConf job) {
		try {
			Path[] archivosCache = DistributedCache.getLocalCacheFiles(job);
			if (archivosCache != null && archivosCache.length > 0) {
				BufferedReader lector = new BufferedReader(new FileReader(archivosCache[0].toString()));
				String linea;
				while ((linea = lector.readLine()) != null) {
					String[] partes = linea.split("\t");
					if (partes.length == 2) {
						promedioPorRegion.put(partes[0], Double.parseDouble(partes[1]));
					}
				}
				lector.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("No se pudo leer el archivo de promedios por región (¿corriste antes PromedioLongitudPorRegion?)", e);
		}
	}

	// Por cada recurso, compara su propia longitud contra el promedio de SU
	// región y lo clasifica como "Este" u "Oeste" respecto a ese promedio regional.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 10) {
			return;
		}
		String region = columnas[0].trim();
		String longitudTexto = columnas[10].trim();
		if (longitudTexto.isEmpty()) {
			return; // fila sin coordenadas
		}
		Double promedioRegion = promedioPorRegion.get(region);
		if (promedioRegion == null) {
			return; // región sin promedio calculado, no debería pasar
		}
		try {
			double longitud = Double.parseDouble(longitudTexto);
			String clasificacion = (longitud > promedioRegion) ? "Este del promedio" : "Oeste del promedio";
			output.collect(new Text(region + "|" + clasificacion), uno);
		} catch (NumberFormatException e) {
			// valor no numérico, se ignora
		}
	}
}
