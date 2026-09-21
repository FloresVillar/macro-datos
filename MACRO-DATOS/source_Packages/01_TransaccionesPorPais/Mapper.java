package TransaccionesPorPais;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable uno = new IntWritable(1);

	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		String linea = value.toString();
		String[] columnas = linea.split(",");
		output.collect(new Text(columnas[7]), uno); // columna 7 = Country
	}
}
//Nota: como llamamos a la clase Mapper igual que la interfaz org.apache.hadoop.mapred.Mapper, hay que usar el nombre completo (org.apache.hadoop.mapred.Mapper<...>) en el
// implements para que no choquen — por eso esa línea es más larga que en SalesMapper.java
