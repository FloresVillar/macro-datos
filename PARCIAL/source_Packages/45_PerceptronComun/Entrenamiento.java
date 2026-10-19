package PerceptronComun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

// Encadena todos los jobs MapReduce del perceptrón multicapa. Lo usan los Driver de
// PerceptronClasificacion y PerceptronRegresion (cambia solo el tipo de salida y la pérdida):
//   Job 1      Estadísticas: media y desviación de cada variable (solo train)   -> args[1] + "_estadisticas"
//   Job 2      Vectores (solo map): vector normalizado y marca train/test        -> args[1] + "_vectores"
//   Jobs 3..   Épocas: gradiente descendente clásico por lotes completos. Cada época es
//              un job: los map suman el gradiente de su bloque, el reduce los junta y
//              el Driver actualiza w <- w - tasa * gradiente / ejemplos       -> args[1] + "_epoca<n>" (se borra al leerlo)
//   Último     Evaluación sobre test con los pesos finales                      -> args[1]
//   Además el Driver deja la curva de pérdida por época                          -> args[1] + "_curva"
public class Entrenamiento {

	private static final long SEMILLA = 42;

	public static void ejecutar(String[] args, String tipo, int epocasPorDefecto, double tasaPorDefecto, double pesoPositivo) {
		int epocas = args.length > 2 ? Integer.parseInt(args[2]) : epocasPorDefecto;
		double tasa = args.length > 3 ? Double.parseDouble(args[3]) : tasaPorDefecto;

		Path entrada = new Path(args[0]);
		Path estadisticas = new Path(args[1] + "_estadisticas");
		Path vectores = new Path(args[1] + "_vectores");
		Path salida = new Path(args[1]);
		Path curva = new Path(args[1] + "_curva");

		JobClient cliente_job = new JobClient();
		try {
			// ---------------- JOB 1: ESTADÍSTICAS ----------------
			JobConf configuracion_job1 = nuevoJob("Perceptron_" + tipo + "_Estadisticas", tipo, pesoPositivo);
			configuracion_job1.setMapperClass(PerceptronComun.MapperEstadisticas.class);
			configuracion_job1.setReducerClass(PerceptronComun.ReducerEstadisticas.class);
			FileInputFormat.setInputPaths(configuracion_job1, entrada);
			FileOutputFormat.setOutputPath(configuracion_job1, estadisticas);
			cliente_job.setConf(configuracion_job1);

			// Borrar los directorios intermedios de una corrida anterior (args[1] + "_...")
			FileSystem fs = FileSystem.get(configuracion_job1);
			FileStatus[] viejos = fs.globStatus(new Path(args[1] + "_*"));
			if (viejos != null) {
				for (FileStatus st : viejos) {
					fs.delete(st.getPath(), true);
				}
			}

			System.out.println("Perceptron " + tipo + ": 18-" + Red.OCULTAS + "-1, epocas=" + epocas + ", tasa=" + tasa
					+ (tipo.equals("clasificacion") ? ", peso de la clase con fallecidos=" + pesoPositivo : ""));
			JobClient.runJob(configuracion_job1);

			// ---------------- JOB 2: VECTORES (solo map) ----------------
			JobConf configuracion_job2 = nuevoJob("Perceptron_" + tipo + "_Vectores", tipo, pesoPositivo);
			configuracion_job2.setMapperClass(PerceptronComun.MapperVectores.class);
			configuracion_job2.setNumReduceTasks(0);
			configuracion_job2.set("mlp.estadisticas", fs.makeQualified(estadisticas).toString());
			FileInputFormat.setInputPaths(configuracion_job2, entrada);
			FileOutputFormat.setOutputPath(configuracion_job2, vectores);
			JobClient.runJob(configuracion_job2);

			// ---------------- JOBS 3..: ÉPOCAS ----------------
			double[] pesos = Red.inicializar(SEMILLA);
			List<String> curvaPerdida = new ArrayList<String>();
			for (int e = 1; e <= epocas; e++) {
				Path salidaEpoca = new Path(args[1] + "_epoca" + e);
				JobConf configuracion_epoca = nuevoJob("Perceptron_" + tipo + "_Epoca_" + e, tipo, pesoPositivo);
				configuracion_epoca.setMapperClass(PerceptronComun.MapperEpoca.class);
				configuracion_epoca.setReducerClass(PerceptronComun.ReducerEpoca.class);
				configuracion_epoca.setNumReduceTasks(1); // un solo vector de gradiente total
				configuracion_epoca.set("mlp.pesos", Red.aTexto(pesos));
				FileInputFormat.setInputPaths(configuracion_epoca, vectores);
				FileOutputFormat.setOutputPath(configuracion_epoca, salidaEpoca);
				JobClient.runJob(configuracion_epoca);

				// "gradiente <TAB> perdida_total|ejemplos|g0,...,g160"
				String linea = leerSalida(configuracion_epoca, fs.makeQualified(salidaEpoca).toString()).get(0);
				String[] v = linea.split("\t")[1].split("\\|");
				double perdidaTotal = Double.parseDouble(v[0]);
				long ejemplos = Long.parseLong(v[1]);
				double[] gradiente = Red.desdeTexto(v[2]);
				for (int k = 0; k < Red.PESOS; k++) {
					pesos[k] -= tasa * gradiente[k] / ejemplos; // gradiente descendente clásico
				}
				double perdidaMedia = perdidaTotal / ejemplos;
				curvaPerdida.add(e + "\t" + String.format(Locale.US, "%.6f", perdidaMedia));
				System.out.println(String.format(Locale.US, "Epoca %d: perdida de entrenamiento %.6f (%d ejemplos)", e, perdidaMedia, ejemplos));
				fs.delete(salidaEpoca, true); // ya se leyó: no acumular 50 directorios en HDFS
			}

			// Curva de pérdida (para el gráfico del informe) y pesos finales
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs.create(new Path(curva, "part-00000")), "UTF-8"));
			for (String l : curvaPerdida) {
				bw.write(l);
				bw.newLine();
			}
			bw.write("pesos_finales\t" + Red.aTexto(pesos));
			bw.newLine();
			bw.close();

			// ---------------- JOB FINAL: EVALUACIÓN EN TEST ----------------
			JobConf configuracion_eval = nuevoJob("Perceptron_" + tipo + "_Evaluacion", tipo, pesoPositivo);
			configuracion_eval.setMapperClass(PerceptronComun.MapperEvaluacion.class);
			configuracion_eval.setReducerClass(PerceptronComun.ReducerEvaluacion.class);
			configuracion_eval.setNumReduceTasks(1); // métricas globales
			configuracion_eval.set("mlp.pesos", Red.aTexto(pesos));
			FileInputFormat.setInputPaths(configuracion_eval, vectores);
			FileOutputFormat.setOutputPath(configuracion_eval, salida);
			JobClient.runJob(configuracion_eval);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Lee todas las líneas de los archivos part-* de un directorio de salida en HDFS
	public static List<String> leerSalida(JobConf job, String directorio) throws IOException {
		List<String> lineas = new ArrayList<String>();
		Path dir = new Path(directorio);
		FileSystem fs = dir.getFileSystem(job);
		for (FileStatus st : fs.listStatus(dir)) {
			if (!st.getPath().getName().startsWith("part-")) {
				continue;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(st.getPath()), "UTF-8"));
			String linea;
			while ((linea = br.readLine()) != null) {
				if (!linea.isEmpty()) {
					lineas.add(linea);
				}
			}
			br.close();
		}
		return lineas;
	}

	// Configuración común: key y value Text, entrada y salida de texto, y los parámetros del modelo
	private static JobConf nuevoJob(String nombre, String tipo, double pesoPositivo) {
		JobConf configuracion = new JobConf(Entrenamiento.class);
		configuracion.setJobName(nombre);
		configuracion.setOutputKeyClass(Text.class);
		configuracion.setOutputValueClass(Text.class);
		configuracion.setInputFormat(TextInputFormat.class);
		configuracion.setOutputFormat(TextOutputFormat.class);
		configuracion.set("mlp.tipo", tipo);
		configuracion.set("mlp.peso.positivo", String.valueOf(pesoPositivo));
		return configuracion;
	}
}
