package AgrupamientoDBSCAN;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

// JOBS 2..k - Reducer (una vuelta de propagación): cada punto se queda con la
// etiqueta más baja entre la suya y las que le mandaron sus vecinos núcleo.
// Repitiendo vueltas, todos los núcleos conectados terminan con la misma etiqueta
// (la menor del grupo) y cada punto de borde toma la de un núcleo vecino.
public class ReducerPropagacion extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// El Driver lee este contador al final de cada vuelta: 0 = ya no cambió nada, terminar
	public static enum Contador { CAMBIOS }

	public void reduce(Text id, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] estado = null;     // [0] lat, [1] lon, [2] fall, [3] her, [4] depto, [5] tipo, [6] etiqueta, [7] vecinos
		String minimaRecibida = null;

		while (valores.hasNext()) {
			String valor = valores.next().toString();
			if (valor.startsWith("S|")) {
				estado = valor.substring(2).split("\\|", -1);
			} else {
				String etiqueta = valor.substring(2);
				if (minimaRecibida == null || etiqueta.compareTo(minimaRecibida) < 0) {
					minimaRecibida = etiqueta;
				}
			}
		}
		if (estado == null) {
			return;
		}

		String actual = estado[6];
		String nueva = actual;
		if (minimaRecibida != null) {
			// "-" = punto de borde/ruido todavía sin grupo: toma la primera etiqueta que le llegue
			if (actual.equals("-") || minimaRecibida.compareTo(actual) < 0) {
				nueva = minimaRecibida;
			}
		}
		if (!nueva.equals(actual)) {
			reporter.incrCounter(Contador.CAMBIOS, 1);
		}

		output.collect(id, new Text(estado[0] + "|" + estado[1] + "|" + estado[2] + "|" + estado[3] + "|" + estado[4]
				+ "|" + estado[5] + "|" + nueva + "|" + estado[7]));
	}
}
