package RegresionRecursosPorDistritos;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class Driver {
	public static void main(String[] args) {
		JobClient cliente_job = new JobClient();
		JobConf configuracion_job = new JobConf(Driver.class);
		configuracion_job.setJobName("RegresionRecursosPorDistritos");
		configuracion_job.setOutputKeyClass(Text.class);
		configuracion_job.setOutputValueClass(Text.class);
		configuracion_job.setMapperClass(RegresionRecursosPorDistritos.Mapper.class);
		configuracion_job.setReducerClass(RegresionRecursosPorDistritos.Reducer.class);
		configuracion_job.setInputFormat(TextInputFormat.class);
		configuracion_job.setOutputFormat(TextOutputFormat.class);
		// arg[0] acá debe ser la carpeta de OUTPUT de ConteoPorProvincia (7b1),
		// no el CSV original — este job encadena leyendo el resultado del anterior.
		FileInputFormat.setInputPaths(configuracion_job, new Path(args[0]));
		FileOutputFormat.setOutputPath(configuracion_job, new Path(args[1]));
		cliente_job.setConf(configuracion_job);
		try {
			JobClient.runJob(configuracion_job);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
