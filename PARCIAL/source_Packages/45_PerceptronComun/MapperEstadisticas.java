package PerceptronComun;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB de preparación 1 - Mapper: lee el CSV y emite, solo de los accidentes de
// entrenamiento, cada variable con su número como key ("j" -> valor), para que
// el Reducer calcule su media y desviación (normalización z-score).
public class MapperEstadisticas extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		Variables v = Variables.desdeCsv(value.toString());
		if (v == null || v.esTest) {
			return; // cabecera, fila inválida o de test: el test no participa en la normalización
		}
		for (int j = 0; j < Variables.CANTIDAD; j++) {
			if (!Double.isNaN(v.x[j])) {
				output.collect(new Text(String.valueOf(j)), new Text(String.valueOf(v.x[j])));
			}
		}
	}
}
