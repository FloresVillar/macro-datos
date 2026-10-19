package AgrupamientoDBSCAN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;

// Carga en memoria los puntos ya etiquetados (salida del job de etiquetado,
// "id <TAB> lat|lon|grupo|fallecidos|heridos|departamento") para las métricas.
// Mismo recurso que la PC1 (5a2, 6a2): el job lee en configure() el resultado
// de un job anterior. Con 3 832 puntos cabe de sobra en memoria.
// Solo guarda los puntos agrupados: el ruido (grupo -1) no entra en las métricas.
public class Puntos {

	public List<String> grupo = new ArrayList<String>();
	public List<Double> lat = new ArrayList<Double>();
	public List<Double> lon = new ArrayList<Double>();

	public static Puntos cargar(JobConf job) throws IOException {
		Puntos p = new Puntos();
		Path dir = new Path(job.get("dbscan.puntos"));
		FileSystem fs = dir.getFileSystem(job);
		for (FileStatus st : fs.listStatus(dir)) {
			if (!st.getPath().getName().startsWith("part-")) {
				continue; // _SUCCESS y similares
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(st.getPath()), "UTF-8"));
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\t", 2);
				if (partes.length != 2) {
					continue;
				}
				String[] v = partes[1].split("\\|"); // [0] lat, [1] lon, [2] grupo
				if (v[2].equals("-1")) {
					continue;
				}
				p.grupo.add(v[2]);
				p.lat.add(Double.parseDouble(v[0]));
				p.lon.add(Double.parseDouble(v[1]));
			}
			br.close();
		}
		return p;
	}
}
