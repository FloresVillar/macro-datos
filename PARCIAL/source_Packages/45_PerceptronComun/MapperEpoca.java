package PerceptronComun;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de época - Mapper: recibe los pesos actuales por la configuración del job,
// recorre los vectores de entrenamiento de su bloque y va sumando el gradiente de
// cada ejemplo (forward + backpropagation). Al terminar su bloque (close) emite una
// sola línea con la suma: así cada map task (una por worker) aporta su parte del
// gradiente y por la red viaja un vector de 161 números, no uno por ejemplo.
public class MapperEpoca extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	private double[] pesos;
	private boolean clasificacion;
	private double pesoPositivo;
	private double[] gradiente = new double[Red.PESOS];
	private double perdida = 0;
	private long ejemplos = 0;
	private OutputCollector<Text, Text> salida;

	public void configure(JobConf job) {
		pesos = Red.desdeTexto(job.get("mlp.pesos"));
		clasificacion = job.get("mlp.tipo").equals("clasificacion");
		pesoPositivo = Double.parseDouble(job.get("mlp.peso.positivo"));
	}

	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		salida = output;
		String[] partes = value.toString().split("\t"); // [0] train/test, [1] "yC|yR|x..."
		if (partes.length != 2 || !partes[0].equals("train")) {
			return;
		}
		String[] v = partes[1].split("\\|");
		double y = Double.parseDouble(clasificacion ? v[0] : v[1]);
		perdida += Red.acumularGradiente(pesos, Red.desdeTexto(v[2]), y, clasificacion, pesoPositivo, gradiente);
		ejemplos++;
	}

	// Una línea por map task: "perdida|ejemplos|g0,g1,...,g160"
	public void close() throws IOException {
		if (salida != null && ejemplos > 0) {
			salida.collect(new Text("gradiente"), new Text(perdida + "|" + ejemplos + "|" + Red.aTexto(gradiente)));
		}
	}
}
