package TarjetaMasUsadaPorCiudadPais;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// Para cada grupo ciudad+país, cuenta cuántas veces aparece cada tipo de
	// tarjeta (con un HashMap), y al final se queda con la que más se repitió.
	public void reduce(Text llave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		HashMap<String, Integer> conteoPorTarjeta = new HashMap<String, Integer>();
		while (valores.hasNext()) {
			String tarjeta = valores.next().toString();
			Integer contador = conteoPorTarjeta.get(tarjeta);
			conteoPorTarjeta.put(tarjeta, (contador == null ? 0 : contador) + 1);
		}

		// recorre el HashMap y se queda con la key (tarjeta) de mayor conteo
		String tarjetaMasUsada = "";
		int maximo = -1;
		for (Map.Entry<String, Integer> entrada : conteoPorTarjeta.entrySet()) {
			if (entrada.getValue() > maximo) {
				maximo = entrada.getValue();
				tarjetaMasUsada = entrada.getKey();
			}
		}
		output.collect(llave, new Text(tarjetaMasUsada + " (" + maximo + ")"));
	}
}
