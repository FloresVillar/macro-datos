package PaisProductoMayorFacturacion;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

// Consulta encadenada (2 MapReduce en un solo Driver):
//   Job 1: suma de ventas por "Pais|Producto"          -> args[1] + "_temp"
//   Job 2: producto estrella de cada país y, entre todos,
//          el país con el mayor monto                   -> args[1]
public class Driver {
	public static void main(String[] args) {
		// Directorio intermedio donde el job 1 deja su resultado para el job 2
		Path entrada = new Path(args[0]);
		Path intermedio = new Path(args[1] + "_temp");
		Path salida = new Path(args[1]);

		// ---------------- JOB 1 ----------------
		JobClient cliente_job = new JobClient();
		// Crear un objeto de configuración para el job 1
		JobConf configuracion_job1 = new JobConf(Driver.class);

		// Establecer un nombre para el Job
		configuracion_job1.setJobName("PaisProductoMayorFacturacion_Suma");

		// Especificar el tipo de dato de la key y el value de salida
		configuracion_job1.setOutputKeyClass(Text.class);
		configuracion_job1.setOutputValueClass(IntWritable.class);

		// Especificar los nombres de las clases Mapper y Reducer
		configuracion_job1.setMapperClass(PaisProductoMayorFacturacion.Mapper.class);
		configuracion_job1.setReducerClass(PaisProductoMayorFacturacion.Reducer.class);

		// Especificar los formatos del tipo de dato de entrada y salida
		configuracion_job1.setInputFormat(TextInputFormat.class);
		configuracion_job1.setOutputFormat(TextOutputFormat.class);

		// Entrada = CSV en HDFS, salida = directorio intermedio
		FileInputFormat.setInputPaths(configuracion_job1, entrada);
		FileOutputFormat.setOutputPath(configuracion_job1, intermedio);

		// ---------------- JOB 2 ----------------
		// Crear un objeto de configuración para el job 2
		JobConf configuracion_job2 = new JobConf(Driver.class);
		configuracion_job2.setJobName("PaisProductoMayorFacturacion_Maximo");

		// Salida final: key = país, value = "Producto <TAB> Monto" (ambos texto)
		configuracion_job2.setOutputKeyClass(Text.class);
		configuracion_job2.setOutputValueClass(Text.class);

		configuracion_job2.setMapperClass(PaisProductoMayorFacturacion.MapperMaximo.class);
		configuracion_job2.setReducerClass(PaisProductoMayorFacturacion.ReducerMaximo.class);

		configuracion_job2.setInputFormat(TextInputFormat.class);
		configuracion_job2.setOutputFormat(TextOutputFormat.class);

		// Entrada = resultado del job 1, salida = directorio final
		FileInputFormat.setInputPaths(configuracion_job2, intermedio);
		FileOutputFormat.setOutputPath(configuracion_job2, salida);

		cliente_job.setConf(configuracion_job1);
		try {
			// Borrar el directorio intermedio si quedó de una corrida anterior
			FileSystem fs = FileSystem.get(configuracion_job1);
			if (fs.exists(intermedio)) {
				fs.delete(intermedio, true);
			}

			// Ejecutar el job 1 y, cuando termine, el job 2
			JobClient.runJob(configuracion_job1);
			JobClient.runJob(configuracion_job2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
