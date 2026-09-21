package RegresionRecursosPorDistritos;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

	// Misma regresión lineal por fórmula cerrada que RegresionLongitudDesdeLatitud
	// (7a), pero acá X = cantidad de distritos distintos de la provincia,
	// Y = cantidad de recursos turísticos de la provincia (~190 puntos, uno
	// por provincia, en vez de ~4947 puntos individuales).
	public void reduce(Text llave, Iterator<Text> valores, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		int n = 0;
		double sumaX = 0, sumaY = 0, sumaXY = 0, sumaX2 = 0, sumaY2 = 0;
		while (valores.hasNext()) {
			String[] partes = valores.next().toString().split(",");
			double x = Double.parseDouble(partes[0]); // cantidad de distritos
			double y = Double.parseDouble(partes[1]); // cantidad de recursos
			n++;
			sumaX += x;
			sumaY += y;
			sumaXY += x * y;
			sumaX2 += x * x;
			sumaY2 += y * y;
		}

		double pendiente = (n * sumaXY - sumaX * sumaY) / (n * sumaX2 - sumaX * sumaX);
		double intercepto = (sumaY - pendiente * sumaX) / n;

		double numeradorR = n * sumaXY - sumaX * sumaY;
		double denominadorR = Math.sqrt((n * sumaX2 - sumaX * sumaX) * (n * sumaY2 - sumaY * sumaY));
		double r2 = Math.pow(numeradorR / denominadorR, 2);

		double sse = sumaY2 - intercepto * sumaY - pendiente * sumaXY;
		double mse = sse / n;

		output.collect(llave, new Text("pendiente=" + pendiente + " intercepto=" + intercepto
				+ " R2=" + r2 + " loss_MSE=" + mse + " n=" + n));
	}
}
