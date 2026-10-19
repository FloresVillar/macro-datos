package PerceptronComun;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de evaluación - Mapper: con los pesos finales, predice cada accidente de
// test y emite "real|predicho" con una key fija, para que un solo Reducer calcule las métricas.
public class MapperEvaluacion extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	private double[] pesos;
	private boolean clasificacion;

	public void configure(JobConf job) {
		pesos = Red.desdeTexto(job.get("mlp.pesos"));
		clasificacion = job.get("mlp.tipo").equals("clasificacion");
	}

	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t");
		if (partes.length != 2 || !partes[0].equals("test")) {
			return;
		}
		String[] v = partes[1].split("\\|");
		double y = Double.parseDouble(clasificacion ? v[0] : v[1]);
		double prediccion = Red.predecir(pesos, Red.desdeTexto(v[2]), clasificacion);
		output.collect(new Text("test"), new Text(y + "|" + prediccion));
	}
}
