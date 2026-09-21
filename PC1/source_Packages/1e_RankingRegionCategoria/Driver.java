package RankingRegionCategoria;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class Driver {
	public static void main(String[] args) {
		JobClient cliente_job = new JobClient();
		// Crear un objeto de configuración para el job
		JobConf configuracion_job = new JobConf(Driver.class);

		// Establecer un nombre para el Job
		configuracion_job.setJobName("RankingRegionCategoria");

		// Especificar el tipo de dato de la key y el value de salida
		// (Text/Text porque el resultado es "puesto. region|categoria" -> "conteo")
		configuracion_job.setOutputKeyClass(Text.class);
		configuracion_job.setOutputValueClass(Text.class);

		// Especificar los nombres de las clases Mapper y Reducer
		configuracion_job.setMapperClass(RankingRegionCategoria.Mapper.class);
		configuracion_job.setReducerClass(RankingRegionCategoria.Reducer.class);

		// Solo 1 reducer: hace falta que TODOS los pares lleguen al mismo lugar
		// para poder comparar y armar el ranking. Con más de 1 reducer, cada uno
		// vería solo una parte de los datos y el ranking saldría incompleto.
		configuracion_job.setNumReduceTasks(1);

		// Especificar los formatos del tipo de dato de entrada y salida
		configuracion_job.setInputFormat(TextInputFormat.class);
		configuracion_job.setOutputFormat(TextOutputFormat.class);

		// Establecer los directorios de entrada y salida usando los argumentos de línea de comandos,
		//arg[0] = nombre del directorio de entrada en HDFS, y arg[1] = nombre del directorio de salida que se creará para guardar el archivo de resultado.
		FileInputFormat.setInputPaths(configuracion_job, new Path(args[0]));
		FileOutputFormat.setOutputPath(configuracion_job, new Path(args[1]));

		cliente_job.setConf(configuracion_job);
		try {
			// Ejecutar el job
			JobClient.runJob(configuracion_job);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
