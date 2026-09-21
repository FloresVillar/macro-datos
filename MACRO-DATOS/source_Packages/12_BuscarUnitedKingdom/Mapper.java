package BuscarUnitedKingdom;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable uno = new IntWritable(1);

	// No agrupa ni suma nada de verdad — solo filtra: si la línea contiene
	// "United Kingdom", la emite completa como key (así se ve el registro entero).
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		String linea = value.toString();
		if (linea.contains("United Kingdom")) {
			output.collect(new Text(linea), uno);
		}
	}
}
