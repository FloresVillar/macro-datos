package ClasificarPorCentroideRegion;

import java.net.URI;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class Driver {
	public static void main(String[] args) {
		JobClient cliente_job = new JobClient();
		JobConf configuracion_job = new JobConf(Driver.class);
		configuracion_job.setJobName("ClasificarPorCentroideRegion");

		try {
			DistributedCache.addCacheFile(new URI("/pc1_output/CentroidesRegion/part-00000"), configuracion_job);
		} catch (Exception e) {
			e.printStackTrace();
		}

		configuracion_job.setOutputKeyClass(Text.class);
		configuracion_job.setOutputValueClass(IntWritable.class);
		configuracion_job.setMapperClass(ClasificarPorCentroideRegion.Mapper.class);
		configuracion_job.setReducerClass(ClasificarPorCentroideRegion.Reducer.class);
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
