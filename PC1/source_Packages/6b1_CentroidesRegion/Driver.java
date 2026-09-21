package CentroidesRegion;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class Driver {
	public static void main(String[] args) {
		JobClient cliente_job = new JobClient();
		JobConf configuracion_job = new JobConf(Driver.class);
		configuracion_job.setJobName("CentroidesRegion");
		configuracion_job.setOutputKeyClass(Text.class);
		configuracion_job.setOutputValueClass(Text.class);
		configuracion_job.setMapperClass(CentroidesRegion.Mapper.class);
		configuracion_job.setReducerClass(CentroidesRegion.Reducer.class);
		configuracion_job.setNumReduceTasks(1);
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
