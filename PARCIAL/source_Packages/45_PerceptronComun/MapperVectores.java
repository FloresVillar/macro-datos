package PerceptronComun;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de preparación 2 (solo Mapper): arma el vector normalizado de cada accidente.
// En configure() lee las medias y desviaciones del job anterior.
// Salida: key = "train" o "test", value = "yClasificacion|yRegresion|x0,x1,...,x17".
public class MapperVectores extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	private double[] media = new double[Variables.CANTIDAD];
	private double[] desviacion = new double[Variables.CANTIDAD];

	public void configure(JobConf job) {
		try {
			for (String linea : Entrenamiento.leerSalida(job, job.get("mlp.estadisticas"))) {
				String[] partes = linea.split("\t");
				String[] md = partes[1].split("\\|");
				int j = Integer.parseInt(partes[0]);
				media[j] = Double.parseDouble(md[0]);
				desviacion[j] = Double.parseDouble(md[1]);
			}
		} catch (IOException e) {
			throw new RuntimeException("No se pudieron leer las estadísticas de normalización", e);
		}
	}

	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		Variables v = Variables.desdeCsv(value.toString());
		if (v == null) {
			return;
		}
		double[] z = new double[Variables.CANTIDAD];
		for (int j = 0; j < Variables.CANTIDAD; j++) {
			// z-score; un valor faltante (progresiva vacía) queda en 0 = la media
			z[j] = Double.isNaN(v.x[j]) ? 0 : (v.x[j] - media[j]) / desviacion[j];
		}
		output.collect(new Text(v.esTest ? "test" : "train"),
				new Text(v.yClasificacion + "|" + v.yRegresion + "|" + Red.aTexto(z)));
	}
}
