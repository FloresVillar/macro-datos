package PaisProductoMayorFacturacion;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOB 2 - Reducer: país con el mayor monto en su producto de mayor facturación.
public class ReducerMaximo extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	public void reduce(Text llave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		// Paso 1: para cada país, quedarse con su producto de mayor facturación
		// (HashMap país -> {producto, monto})
		HashMap<String, String> mejorProductoPorPais = new HashMap<String, String>();
		HashMap<String, Integer> mejorMontoPorPais = new HashMap<String, Integer>();
		while (valores.hasNext()) {
			String[] campos = valores.next().toString().split("\\|"); // [0]=Pais, [1]=Producto, [2]=monto
			String pais = campos[0];
			String producto = campos[1];
			int monto = Integer.parseInt(campos[2]);
			Integer montoActual = mejorMontoPorPais.get(pais);
			if (montoActual == null || monto > montoActual) {
				mejorMontoPorPais.put(pais, monto);
				mejorProductoPorPais.put(pais, producto);
			}
		}

		// Paso 2: entre todos los países, quedarse con el que tenga el mayor monto
		// en su producto estrella
		String paisGanador = "";
		int maximo = -1;
		for (Map.Entry<String, Integer> entrada : mejorMontoPorPais.entrySet()) {
			if (entrada.getValue() > maximo) {
				maximo = entrada.getValue();
				paisGanador = entrada.getKey();
			}
		}

		// Resultado final: "Pais <TAB> Producto <TAB> MontoTotal"
		output.collect(new Text(paisGanador), new Text(mejorProductoPorPais.get(paisGanador) + "\t" + maximo));
	}
}
