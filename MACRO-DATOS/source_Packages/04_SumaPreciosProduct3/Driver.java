package SumaPreciosProduct3;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class Driver {
	public static void main(String[] args) {
		JobClient cliente_job = new JobClient();
		// Crear un objeto de configuración para el job
		JobConf configuracion_job = new JobConf(Driver.class);

		// Establecer un nombre para el Job
		configuracion_job.setJobName("SumaPreciosProduct3");

		// Especificar el tipo de dato de la key y el value de salida
		configuracion_job.setOutputKeyClass(Text.class);
		configuracion_job.setOutputValueClass(IntWritable.class);

		// Especificar los nombres de las clases Mapper y Reducer
		configuracion_job.setMapperClass(SumaPreciosProduct3.Mapper.class);
		configuracion_job.setReducerClass(SumaPreciosProduct3.Reducer.class);

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
