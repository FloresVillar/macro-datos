package PaisProductoMayorFacturacion;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 2 - Mapper: lee la salida del job 1 ("Pais|Producto <TAB> monto").
public class MapperMaximo extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	// Emite todas las líneas con la misma key fija "Max", para que un solo Reducer
	// las reciba todas juntas y pueda comparar entre países.
	// El value se reenvía como "Pais|Producto|monto".
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] partes = value.toString().split("\t"); // [0] = "Pais|Producto", [1] = monto
		if (partes.length == 2) {
			output.collect(new Text("Max"), new Text(partes[0] + "|" + partes[1].trim()));
		}
	}
}
