package ClasificarPorCentroideCategoria;

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
	// centroide[0] = latitud promedio, centroide[1] = longitud promedio de esa categoría
	private HashMap<String, double[]> centroidePorCategoria = new HashMap<String, double[]>();

	// Lee los 5 centroides (uno por categoría) que dejó el primer job de la
	// cadena (CentroidesCategoria), vía DistributedCache.
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
						centroidePorCategoria.put(partes[0], new double[]{Double.parseDouble(coords[0]), Double.parseDouble(coords[1])});
					}
				}
				lector.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("No se pudo leer el archivo de centroides (¿corriste antes CentroidesCategoria?)", e);
		}
	}

	// Clasificador de "centroide más cercano" (Nearest Centroid): para cada
	// recurso, calcula la distancia euclidiana entre sus propias coordenadas
	// y el centroide de CADA una de las 5 categorías, y predice como
	// categoría la del centroide más cercano. Después compara esa predicción
	// contra la categoría real que ya trae el CSV, y emite "correcto" o
	// "incorrecto" — el Reducer va a sumar esos conteos para calcular la
	// exactitud (accuracy) del modelo.
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		if (key.get() == 0) {
			return; // fila de cabecera
		}
		String linea = value.toString();
		String[] columnas = linea.split(";");
		if (columnas.length <= 10) {
			return;
		}
		String categoriaReal = columnas[5].trim();
		String latTexto = columnas[10].trim(); // columna 10: la cabecera del CSV dice LONGITUD, pero los valores son latitudes (las columnas vienen invertidas en el dataset)
		String lonTexto = columnas[9].trim();  // columna 9: la cabecera del CSV dice LATITUD, pero los valores son longitudes (las columnas vienen invertidas en el dataset)
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

		String categoriaPredicha = "";
		double distanciaMinima = Double.POSITIVE_INFINITY;
		for (String categoria : centroidePorCategoria.keySet()) {
			double[] centroide = centroidePorCategoria.get(categoria);
			double dLat = lat - centroide[0];
			double dLon = lon - centroide[1];
			double distancia = Math.sqrt(dLat * dLat + dLon * dLon); // distancia euclidiana
			if (distancia < distanciaMinima) {
				distanciaMinima = distancia;
				categoriaPredicha = categoria;
			}
		}

		String resultado = categoriaPredicha.equals(categoriaReal) ? "correcto" : "incorrecto";
		output.collect(new Text(resultado), uno);
	}
}
