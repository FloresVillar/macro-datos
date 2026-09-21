
package SalesCountry;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.JobClient;			
import org.apache.hadoop.mapred.JobConf;

public class SalesCountryDriver {
	public static void main(String[] args) {
		JobClient cliente_job = new JobClient();
		// Crear un objeto de configuración para el job
		JobConf configuracion_job = new JobConf(SalesCountryDriver.class);

		// Establecer un nombre para el Job
		configuracion_job.setJobName("SalePerCountry");

		// Especificar el tipo de dato de la key y el value de salida
		configuracion_job.setOutputKeyClass(Text.class);
		configuracion_job.setOutputValueClass(IntWritable.class);

		// Especificar los nombres de las clases 
		// Mapper:(procesa input linea a linea saca el dato que interesa ,transforma cada linea en par clave/valor  (país, 1))
		//  y Reducer:recibe los pares clave/valor   ("United States", 1) juntos, todos los ("France", 1) y por cada grupo los combina en un solo resultado,sumando 1's para obtener el conteo total por pais
		configuracion_job.setMapperClass(SalesCountry.SalesMapper.class);
		configuracion_job.setReducerClass(SalesCountry.SalesCountryReducer.class);

		// Especificar los formatos del tipo de dato de entrada y salida
		configuracion_job.setInputFormat(TextInputFormat.class);
		configuracion_job.setOutputFormat(TextOutputFormat.class);

		// Establecer los directorios de entrada y salida usando los argumentos de línea de comandos,
		//arg[0] = nombre del directorio de entrada en HDFS   " /sales_input"
		//arg[1] = nombre del directorio de salida             "/sales_output"
		// java -cp "<RUTA_AL_JAR>;$cp" SalesCountry.SalesCountryDriver /sales_input /sales_output
		
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
