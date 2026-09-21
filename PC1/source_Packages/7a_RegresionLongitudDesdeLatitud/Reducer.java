package RegresionLongitudDesdeLatitud;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// Regresión lineal simple: Longitud = pendiente*Latitud + intercepto.
	// En vez de guardar todos los puntos en memoria, se acumulan 6 sumas en
	// una sola pasada (n, sumaX, sumaY, sumaXY, sumaX2, sumaY2), y con esas
	// sumas se puede calcular por fórmula cerrada tanto la pendiente/intercepto
	// (mínimos cuadrados) como el R² y el error cuadrático medio (loss), sin
	// necesitar una segunda pasada sobre los datos.
	public void reduce(Text llave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		int n = 0;
		double sumaX = 0, sumaY = 0, sumaXY = 0, sumaX2 = 0, sumaY2 = 0;
		while (valores.hasNext()) {
			String[] partes = valores.next().toString().split(",");
			double x = Double.parseDouble(partes[0]); // latitud
			double y = Double.parseDouble(partes[1]); // longitud
			n++;
			sumaX += x;
			sumaY += y;
			sumaXY += x * y;
			sumaX2 += x * x;
			sumaY2 += y * y;
		}

		double pendiente = (n * sumaXY - sumaX * sumaY) / (n * sumaX2 - sumaX * sumaX);
		double intercepto = (sumaY - pendiente * sumaX) / n;

		// R^2 por fórmula cerrada (coeficiente de correlación de Pearson al cuadrado)
		double numeradorR = n * sumaXY - sumaX * sumaY;
		double denominadorR = Math.sqrt((n * sumaX2 - sumaX * sumaX) * (n * sumaY2 - sumaY * sumaY));
		double r2 = Math.pow(numeradorR / denominadorR, 2);

		// Error cuadrático medio (loss): SSE = sumaY2 - intercepto*sumaY - pendiente*sumaXY
		double sse = sumaY2 - intercepto * sumaY - pendiente * sumaXY;
		double mse = sse / n;

		output.collect(llave, new Text("pendiente=" + pendiente + " intercepto=" + intercepto
				+ " R2=" + r2 + " loss_MSE=" + mse + " n=" + n));
	}
}
