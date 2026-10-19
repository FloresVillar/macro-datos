package SeveridadPorVia;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

// Consulta encadenada (MapReduce triple en un solo Driver):
//   Job 1: por "Departamento|Vía" -> accidentes, fallecidos, heridos e
//          índice de severidad = (3·fallecidos + heridos) / accidentes   -> args[1] + "_temp1"
//   Job 2: por departamento, la vía con el mayor índice
//          (solo vías con al menos 5 accidentes)                       -> args[1] + "_temp2"
//   Job 3: ranking nacional de departamentos según ese índice            -> args[1]
public class Driver {
	public static void main(String[] args) {
		// Directorios intermedios donde cada job deja su resultado para el siguiente
		Path entrada = new Path(args[0]);
		Path intermedio1 = new Path(args[1] + "_temp1");
		Path intermedio2 = new Path(args[1] + "_temp2");
		Path salida = new Path(args[1]);

		// ---------------- JOB 1 ----------------
		JobClient cliente_job = new JobClient();
		// Crear un objeto de configuración para el job 1
		JobConf configuracion_job1 = new JobConf(Driver.class);

		// Establecer un nombre para el Job
		configuracion_job1.setJobName("SeveridadPorVia_Indice");

		// Salida: key = "Departamento|Vía", value = "accidentes|fallecidos|heridos|indice"
		configuracion_job1.setOutputKeyClass(Text.class);
		configuracion_job1.setOutputValueClass(Text.class);

		// Especificar los nombres de las clases Mapper y Reducer
		configuracion_job1.setMapperClass(SeveridadPorVia.Mapper.class);
		configuracion_job1.setReducerClass(SeveridadPorVia.Reducer.class);

		// Especificar los formatos del tipo de dato de entrada y salida
		configuracion_job1.setInputFormat(TextInputFormat.class);
		configuracion_job1.setOutputFormat(TextOutputFormat.class);

		// Entrada = CSV en HDFS, salida = directorio intermedio 1
		FileInputFormat.setInputPaths(configuracion_job1, entrada);
		FileOutputFormat.setOutputPath(configuracion_job1, intermedio1);

		// ---------------- JOB 2 ----------------
		// Crear un objeto de configuración para el job 2
		JobConf configuracion_job2 = new JobConf(Driver.class);
		configuracion_job2.setJobName("SeveridadPorVia_MaximoPorDepartamento");

		// Salida: key = departamento, value = "Vía|accidentes|fallecidos|heridos|indice"
		configuracion_job2.setOutputKeyClass(Text.class);
		configuracion_job2.setOutputValueClass(Text.class);

		configuracion_job2.setMapperClass(SeveridadPorVia.MapperMaximo.class);
		configuracion_job2.setReducerClass(SeveridadPorVia.ReducerMaximo.class);

		configuracion_job2.setInputFormat(TextInputFormat.class);
		configuracion_job2.setOutputFormat(TextOutputFormat.class);

		// Entrada = resultado del job 1, salida = directorio intermedio 2
		FileInputFormat.setInputPaths(configuracion_job2, intermedio1);
		FileOutputFormat.setOutputPath(configuracion_job2, intermedio2);

		// ---------------- JOB 3 ----------------
		// Crear un objeto de configuración para el job 3
		JobConf configuracion_job3 = new JobConf(Driver.class);
		configuracion_job3.setJobName("SeveridadPorVia_Ranking");

		// Salida final: key = puesto, value = departamento, vía y sus valores
		configuracion_job3.setOutputKeyClass(Text.class);
		configuracion_job3.setOutputValueClass(Text.class);

		configuracion_job3.setMapperClass(SeveridadPorVia.MapperRanking.class);
		configuracion_job3.setReducerClass(SeveridadPorVia.ReducerRanking.class);

		// Un solo Reducer para que el ranking salga completo y ordenado en un archivo
		configuracion_job3.setNumReduceTasks(1);

		configuracion_job3.setInputFormat(TextInputFormat.class);
		configuracion_job3.setOutputFormat(TextOutputFormat.class);

		// Entrada = resultado del job 2, salida = directorio final
		FileInputFormat.setInputPaths(configuracion_job3, intermedio2);
		FileOutputFormat.setOutputPath(configuracion_job3, salida);

		cliente_job.setConf(configuracion_job1);
		try {
			// Borrar los directorios intermedios si quedaron de una corrida anterior
			FileSystem fs = FileSystem.get(configuracion_job1);
			if (fs.exists(intermedio1)) {
				fs.delete(intermedio1, true);
			}
			if (fs.exists(intermedio2)) {
				fs.delete(intermedio2, true);
			}

			// Ejecutar los 3 jobs en orden; cada uno espera a que termine el anterior
			JobClient.runJob(configuracion_job1);
			JobClient.runJob(configuracion_job2);
			JobClient.runJob(configuracion_job3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
