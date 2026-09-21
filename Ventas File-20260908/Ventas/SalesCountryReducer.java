package SalesCountry;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class SalesCountryReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

	public void reduce(Text t_llave, Iterator<IntWritable> values, OutputCollector<Text,IntWritable> output, Reporter reporter) throws IOException {
		Text llave = t_llave;
		int frecuenciaPais = 0;
		while (values.hasNext()) {
			// reemplazar el tipo de value por el tipo real de nuestro value
			IntWritable valor = (IntWritable) values.next();   //casteo (Tipo) variable
			frecuenciaPais += valor.get();
		}
		output.collect(llave, new IntWritable(frecuenciaPais));
	}
}
