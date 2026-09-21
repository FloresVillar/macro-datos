package ClasificarPorCentroideRegion;

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
	private HashMap<String, double[]> centroidePorRegion = new HashMap<String, double[]>();

	// Lee los 25 centroides (uno por región) que dejó CentroidesRegion.
	public void configure(JobConf job) {
		try {
			Path[] archivosCache = DistributedCache.getLocalCacheFiles(job);
			if (archivosCache != null && archivosCache.length > 0) {
				BufferedReader lector = new BufferedReader(new FileReader(archivosCache[0].toString()));
				String linea;
				while ((linea = lector.readLine()) != null) {
					String[] partes = linea.split("\t");
					if (partes.length == 2) {
						String[] coords = partes[1].split(",");
						centroidePorRegion.put(partes[0], new double[]{Double.parseDouble(coords[0]), Double.parseDouble(coords[1])});
					}
				}
				lector.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("No se pudo leer el archivo de centroides (¿corriste antes CentroidesRegion?)", e);
		}
	}

	// Mismo clasificador de centroide más cercano que 6a, pero prediciendo
	// la Región (25 clases) en vez de la Categoría (5 clases) — de esperar,
	// debería dar una exactitud (accuracy) más alta, porque las regiones son
	// zonas geográficas compactas por definición, mientras que las
	// categorías turísticas no tienen por qué agruparse geográficamente.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 10) {
			return;
		}
		String regionReal = columnas[0].trim();
		String latTexto = columnas[9].trim();
		String lonTexto = columnas[10].trim();
		if (latTexto.isEmpty() || lonTexto.isEmpty()) {
			return; // fila sin coordenadas
		}
		double lat, lon;
		try {
			lat = Double.parseDouble(latTexto);
			lon = Double.parseDouble(lonTexto);
		} catch (NumberFormatException e) {
			return;
		}

		String regionPredicha = "";
		double distanciaMinima = Double.POSITIVE_INFINITY;
		for (String region : centroidePorRegion.keySet()) {
			double[] centroide = centroidePorRegion.get(region);
			double dLat = lat - centroide[0];
			double dLon = lon - centroide[1];
			double distancia = Math.sqrt(dLat * dLat + dLon * dLon);
			if (distancia < distanciaMinima) {
				distanciaMinima = distancia;
				regionPredicha = region;
			}
		}

		String resultado = regionPredicha.equals(regionReal) ? "correcto" : "incorrecto";
		output.collect(new Text(resultado), uno);
	}
}
