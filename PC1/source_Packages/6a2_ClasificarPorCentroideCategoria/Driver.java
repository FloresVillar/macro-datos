package ClasificarPorCentroideCategoria;

import java.net.URI;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class Driver {
	public static void main(String[] args) {
		JobClient cliente_job = new JobClient();
		JobConf configuracion_job = new JobConf(Driver.class);
		configuracion_job.setJobName("ClasificarPorCentroideCategoria");

		// Segundo job de la cadena: depende de que CentroidesCategoria ya
		// se haya ejecutado antes y haya dejado su resultado en esta ruta.
		try {
			DistributedCache.addCacheFile(new URI("/pc1_output/CentroidesCategoria/part-00000"), configuracion_job);
		} catch (Exception e) {
			e.printStackTrace();
		}

		configuracion_job.setOutputKeyClass(Text.class);
		configuracion_job.setOutputValueClass(IntWritable.class);
		configuracion_job.setMapperClass(ClasificarPorCentroideCategoria.Mapper.class);
		configuracion_job.setReducerClass(ClasificarPorCentroideCategoria.Reducer.class);
		configuracion_job.setInputFormat(TextInputFormat.class);
		configuracion_job.setOutputFormat(TextOutputFormat.class);
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
