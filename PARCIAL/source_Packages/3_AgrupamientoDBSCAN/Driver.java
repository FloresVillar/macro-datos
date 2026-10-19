package AgrupamientoDBSCAN;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

// Agrupamiento por densidad (DBSCAN) de los accidentes según sus coordenadas,
// hecho íntegramente con jobs MapReduce encadenados en un solo Driver:
//   Job 1      Vecindad: rejilla de celdas de lado eps; vecinos a <= eps km y puntos núcleo -> args[1] + "_vecindad"
//   Jobs 2..k  Propagación: los núcleos pasan su etiqueta mínima a sus vecinos; una vuelta
//              por job hasta que ninguna etiqueta cambia (contador CAMBIOS = 0)            -> args[1] + "_iter<n>"
//   Job k+1    Etiquetado (solo map): grupo de cada accidente; ruido = -1                     -> args[1] + "_puntos"
//   Job k+2    Resumen por grupo: accidentes, centroide, fallecidos, heridos, departamento    -> args[1] + "_resumen"
//   Job k+3    Ranking de grupos por tamaño                                                    -> args[1]
//   Job k+4    Métricas: Silhouette y Davies-Bouldin                                           -> args[1] + "_metricas"
// Uso: <Driver> <entrada> <salida> [eps_km] [minPts]
public class Driver {

	// Valores por defecto, elegidos el 2026-10-18 al comparar eps 1/2/3 km y minPts 5/10
	// (ver la guía, sección 2.2): 30 zonas críticas, Silhouette 0.644, Davies-Bouldin 0.210, 19 jobs
	private static final String EPS_KM = "2";
	private static final String MIN_PTS = "10";
	// Tope de vueltas de propagación por si algo no converge
	private static final int MAX_VUELTAS = 50;

	public static void main(String[] args) {
		String eps = args.length > 2 ? args[2] : EPS_KM;
		String minPts = args.length > 3 ? args[3] : MIN_PTS;

		Path entrada = new Path(args[0]);
		Path vecindad = new Path(args[1] + "_vecindad");
		Path puntos = new Path(args[1] + "_puntos");
		Path resumen = new Path(args[1] + "_resumen");
		Path salida = new Path(args[1]);
		Path metricas = new Path(args[1] + "_metricas");

		JobClient cliente_job = new JobClient();
		try {
			// ---------------- JOB 1: VECINDAD ----------------
			JobConf configuracion_job1 = nuevoJob("AgrupamientoDBSCAN_Vecindad", eps, minPts);
			configuracion_job1.setMapperClass(AgrupamientoDBSCAN.MapperVecindad.class);
			configuracion_job1.setReducerClass(AgrupamientoDBSCAN.ReducerVecindad.class);
			FileInputFormat.setInputPaths(configuracion_job1, entrada);
			FileOutputFormat.setOutputPath(configuracion_job1, vecindad);
			cliente_job.setConf(configuracion_job1);

			// Borrar los directorios intermedios de una corrida anterior (args[1] + "_...")
			FileSystem fs = FileSystem.get(configuracion_job1);
			FileStatus[] viejos = fs.globStatus(new Path(args[1] + "_*"));
			if (viejos != null) {
				for (FileStatus st : viejos) {
					fs.delete(st.getPath(), true);
				}
			}

			System.out.println("DBSCAN con eps=" + eps + " km y minPts=" + minPts);
			JobClient.runJob(configuracion_job1);

			// ---------------- JOBS 2..k: PROPAGACIÓN (iterativo) ----------------
			Path actual = vecindad;
			for (int vuelta = 1; vuelta <= MAX_VUELTAS; vuelta++) {
				Path siguiente = new Path(args[1] + "_iter" + vuelta);
				JobConf configuracion_iter = nuevoJob("AgrupamientoDBSCAN_Propagacion_" + vuelta, eps, minPts);
				configuracion_iter.setMapperClass(AgrupamientoDBSCAN.MapperPropagacion.class);
				configuracion_iter.setReducerClass(AgrupamientoDBSCAN.ReducerPropagacion.class);
				FileInputFormat.setInputPaths(configuracion_iter, actual);
				FileOutputFormat.setOutputPath(configuracion_iter, siguiente);

				RunningJob job = JobClient.runJob(configuracion_iter);
				long cambios = job.getCounters().getCounter(ReducerPropagacion.Contador.CAMBIOS);
				System.out.println("Vuelta " + vuelta + ": " + cambios + " etiquetas cambiaron");
				actual = siguiente;
				if (cambios == 0) {
					break; // convergió: todos los grupos tienen su etiqueta final
				}
			}

			// ---------------- JOB ETIQUETADO (solo map) ----------------
			JobConf configuracion_etiq = nuevoJob("AgrupamientoDBSCAN_Etiquetado", eps, minPts);
			configuracion_etiq.setMapperClass(AgrupamientoDBSCAN.MapperEtiquetado.class);
			configuracion_etiq.setNumReduceTasks(0); // cada map escribe directo su salida
			FileInputFormat.setInputPaths(configuracion_etiq, actual);
			FileOutputFormat.setOutputPath(configuracion_etiq, puntos);
			JobClient.runJob(configuracion_etiq);

			// ---------------- JOB RESUMEN POR GRUPO ----------------
			JobConf configuracion_res = nuevoJob("AgrupamientoDBSCAN_Resumen", eps, minPts);
			configuracion_res.setMapperClass(AgrupamientoDBSCAN.MapperResumen.class);
			configuracion_res.setReducerClass(AgrupamientoDBSCAN.ReducerResumen.class);
			FileInputFormat.setInputPaths(configuracion_res, puntos);
			FileOutputFormat.setOutputPath(configuracion_res, resumen);
			JobClient.runJob(configuracion_res);

			// ---------------- JOB RANKING DE GRUPOS ----------------
			JobConf configuracion_rank = nuevoJob("AgrupamientoDBSCAN_Ranking", eps, minPts);
			configuracion_rank.setMapperClass(AgrupamientoDBSCAN.MapperRanking.class);
			configuracion_rank.setReducerClass(AgrupamientoDBSCAN.ReducerRanking.class);
			configuracion_rank.setNumReduceTasks(1); // un solo archivo, ordenado
			FileInputFormat.setInputPaths(configuracion_rank, resumen);
			FileOutputFormat.setOutputPath(configuracion_rank, salida);
			JobClient.runJob(configuracion_rank);

			// ---------------- JOB MÉTRICAS ----------------
			JobConf configuracion_met = nuevoJob("AgrupamientoDBSCAN_Metricas", eps, minPts);
			configuracion_met.setMapperClass(AgrupamientoDBSCAN.MapperMetricas.class);
			configuracion_met.setReducerClass(AgrupamientoDBSCAN.ReducerMetricas.class);
			configuracion_met.setNumReduceTasks(1); // las métricas son globales: un solo Reducer
			// Mapper y Reducer cargan en configure() todos los puntos etiquetados
			configuracion_met.set("dbscan.puntos", fs.makeQualified(puntos).toString());
			FileInputFormat.setInputPaths(configuracion_met, puntos);
			FileOutputFormat.setOutputPath(configuracion_met, metricas);
			JobClient.runJob(configuracion_met);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Configuración común de todos los jobs: key y value Text, entrada y salida de texto,
	// y los parámetros de DBSCAN que leen los configure() de Mapper y Reducer.
	private static JobConf nuevoJob(String nombre, String eps, String minPts) {
		JobConf configuracion = new JobConf(Driver.class);
		configuracion.setJobName(nombre);
		configuracion.setOutputKeyClass(Text.class);
		configuracion.setOutputValueClass(Text.class);
		configuracion.setInputFormat(TextInputFormat.class);
		configuracion.setOutputFormat(TextOutputFormat.class);
		configuracion.set("dbscan.eps.km", eps);
		configuracion.set("dbscan.minpts", minPts);
		return configuracion;
	}
}
