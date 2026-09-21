package RankingRegionCategoria;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// Como todo llegó bajo la misma key ("ALL"), acá adentro se cuenta cuántas
	// veces aparece cada combinación región+categoría (con un HashMap), y al
	// final se ordena de mayor a menor para armar el top 5.
	public void reduce(Text llave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		HashMap<String, Integer> conteoPorCombinacion = new HashMap<String, Integer>();
		while (valores.hasNext()) {
			String combinacion = valores.next().toString();
			Integer contador = conteoPorCombinacion.get(combinacion);
			conteoPorCombinacion.put(combinacion, (contador == null ? 0 : contador) + 1);
		}

		// pasar el HashMap a una lista para poder ordenarla por conteo descendente
		List<Map.Entry<String, Integer>> lista = new ArrayList<Map.Entry<String, Integer>>(conteoPorCombinacion.entrySet());
		Collections.sort(lista, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
				return b.getValue() - a.getValue(); // descendente
			}
		});

		int puesto = 1;
		int limite = Math.min(5, lista.size()); // top 5
		for (int i = 0; i < limite; i++) {
			Map.Entry<String, Integer> entrada = lista.get(i);
			output.collect(new Text(puesto + ". " + entrada.getKey()), new Text(entrada.getValue().toString()));
			puesto++;
		}
	}
}
