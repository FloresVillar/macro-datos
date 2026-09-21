package SalesCountry;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class SalesMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable one = new IntWritable(1);   // crea un objeto IntWritable (el envoltorio que usa Hadoop para representar un entero, porque necesita que los datos sean serializables para mandarlos por red/disco — un int de Java normal no sirve directo para eso) que representa el valor 1.; para no desperdiciar memoria creando el mismo objeto millones de veces.

	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

		String valueString = value.toString();
		String[] SingleCountryData = valueString.split(",");
		output.collect(new Text(SingleCountryData[7]), one);
	}
}
/**
el argumento que reeemplaza Text Value será un linea completa del csv (u otro archivo)
Hadoop llamará a este método map() para cada línea del archivo de entrada.
- Key : la posicion en bytes de esa dentro del archivo (LongWritable)

 */
