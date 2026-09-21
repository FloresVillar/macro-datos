package PromedioLatitudPorRegion;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class Driver {
	public static void main(String[] args) {
		JobClient cliente_job = new JobClient();
		// Crear un objeto de configuración para el job
		JobConf configuracion_job = new JobConf(Driver.class);

		// Establecer un nombre para el Job
		configuracion_job.setJobName("PromedioLatitudPorRegion");

		// Especificar el tipo de dato de la key y el value de salida
		configuracion_job.setOutputKeyClass(Text.class);
		configuracion_job.setOutputValueClass(DoubleWritable.class);

		// Especificar los nombres de las clases Mapper y Reducer
		configuracion_job.setMapperClass(PromedioLatitudPorRegion.Mapper.class);
		configuracion_job.setReducerClass(PromedioLatitudPorRegion.Reducer.class);

		// Un solo Reducer: como el segundo job de la cadena (ClasificarNorteSurRegion)
		// va a leer este resultado como UN SOLO archivo de cache, conviene que
		// todo el output quede junto en un único part-file, en vez de repartido
		// en varios (que pasaría con más de 1 reducer).
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
