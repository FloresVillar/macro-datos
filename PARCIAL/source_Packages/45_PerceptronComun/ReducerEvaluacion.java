package PerceptronComun;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de evaluación - Reducer (uno solo): métricas sobre el conjunto de test.
//   clasificación (umbral 0.5): pérdida (entropía cruzada sin peso), accuracy,
//     precisión, recall, F1, matriz de confusión y accuracy de la línea base
//     "siempre predecir que no hay fallecidos"
//   regresión (en escala log(1 + heridos)): MSE, MAE, R², MSE de la línea base
//     "siempre predecir la media" y MAE en cantidad de heridos
public class ReducerEvaluacion extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	private boolean clasificacion;

	public void configure(JobConf job) {
		clasificacion = job.get("mlp.tipo").equals("clasificacion");
	}

	public void reduce(Text clave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		List<double[]> pares = new ArrayList<double[]>(); // {real, predicho}
		while (valores.hasNext()) {
			String[] v = valores.next().toString().split("\\|");
			pares.add(new double[]{Double.parseDouble(v[0]), Double.parseDouble(v[1])});
		}
		int n = pares.size();
		output.collect(new Text("ejemplos_test"), new Text(String.valueOf(n)));

		if (clasificacion) {
			int vp = 0, fp = 0, vn = 0, fn = 0;
			double perdida = 0;
			for (double[] par : pares) {
				double p = Math.min(Math.max(par[1], 1e-12), 1 - 1e-12);
				perdida += -(par[0] * Math.log(p) + (1 - par[0]) * Math.log(1 - p));
				boolean predicePositivo = par[1] >= 0.5;
				if (par[0] == 1 && predicePositivo) vp++;
				else if (par[0] == 0 && predicePositivo) fp++;
				else if (par[0] == 0) vn++;
				else fn++;
			}
			double precision = vp + fp > 0 ? (double) vp / (vp + fp) : 0;
			double recall = vp + fn > 0 ? (double) vp / (vp + fn) : 0;
			double f1 = precision + recall > 0 ? 2 * precision * recall / (precision + recall) : 0;
			escribir(output, "perdida_test", perdida / n);
			escribir(output, "accuracy", (double) (vp + vn) / n);
			escribir(output, "precision", precision);
			escribir(output, "recall", recall);
			escribir(output, "f1", f1);
			escribir(output, "accuracy_base_siempre_no", (double) (vn + fp) / n);
			output.collect(new Text("matriz_confusion"), new Text("VP=" + vp + " FP=" + fp + " VN=" + vn + " FN=" + fn));
		} else {
			double sumaY = 0;
			for (double[] par : pares) {
				sumaY += par[0];
			}
			double mediaY = sumaY / n;
			double sse = 0, sae = 0, sst = 0, saeHeridos = 0;
			for (double[] par : pares) {
				double error = par[1] - par[0];
				sse += error * error;
				sae += Math.abs(error);
				sst += (par[0] - mediaY) * (par[0] - mediaY);
				saeHeridos += Math.abs(Math.expm1(par[1]) - Math.expm1(par[0])); // de vuelta a cantidad de heridos
			}
			escribir(output, "mse", sse / n);
			escribir(output, "mae", sae / n);
			escribir(output, "r2", sst > 0 ? 1 - sse / sst : 0);
			escribir(output, "mse_base_media", sst / n);
			escribir(output, "mae_heridos", saeHeridos / n);
		}
	}

	private void escribir(OutputCollector<Text, Text> output, String nombre, double valor) throws IOException {
		output.collect(new Text(nombre), new Text(String.format(Locale.US, "%.4f", valor)));
	}
}
