package PerceptronComun;

import java.util.Calendar;
import java.util.GregorianCalendar;

// Convierte una fila del CSV de SUTRAN (separador ";") en las 18 variables de
// entrada de la red y sus dos etiquetas. La usan los dos perceptrones.
//
// Variables (antes de normalizar):
//   0-1  hora del accidente (seno, coseno): las 23:00 y las 01:00 quedan cerca
//   2-3  mes (seno, coseno)
//   4-5  día de la semana (seno, coseno)
//   6-8  latitud, longitud, progresiva (km; NaN si falta: se completa con la media al normalizar)
//   9-16 modalidad en one-hot: CHOQUE, DESPISTE, ATROPELLO, VOLCADURA, INCENDIO, ESPECIAL, ACCIDENTE, EMBISTE
//   17   1 si CODIGO_VIA es "VIA URBANA"
// Etiquetas:
//   clasificación: 1 si hubo al menos un fallecido, 0 si no
//   regresión: log(1 + heridos)
// No se usan heridos para predecir fallecidos ni al revés: son consecuencias del
// mismo accidente y usarlas sería predecir el resultado con el resultado.
public class Variables {

	public static final int CANTIDAD = 18;
	private static final String[] MODALIDADES = {"CHOQUE", "DESPISTE", "ATROPELLO", "VOLCADURA", "INCENDIO", "ESPECIAL", "ACCIDENTE", "EMBISTE"};

	public String id;
	public boolean esTest;
	public double[] x = new double[CANTIDAD];
	public double yClasificacion;
	public double yRegresion;

	// Devuelve null si la fila no se puede usar (cabecera, campos faltantes o no numéricos)
	public static Variables desdeCsv(String linea) {
		String[] c = linea.split(";", -1);
		if (c.length < 14 || c[0].trim().equals("FECHA_CORTE")) {
			return null;
		}
		try {
			Variables v = new Variables();
			v.id = c[1].trim();                                  // NRO_REPORTE
			String fecha = c[2].trim();                          // FECHA_ACC (AAAAMMDD)
			String[] hora = c[3].trim().split(":");              // HORA_ACC (HH:mm:ss)
			String modalidad = c[4].trim();                      // MODALIDAD_ACC
			int fallecidos = Integer.parseInt(c[5].trim());      // CANT_FALLECIDOS
			int heridos = Integer.parseInt(c[6].trim());         // CANT_HERIDOS
			double latitud = Double.parseDouble(c[7].trim());    // LATITUD
			double longitud = Double.parseDouble(c[8].trim());   // LONGITUD
			String progresiva = c[9].trim();                     // PROGRESIVA
			String via = c[10].trim();                           // CODIGO_VIA

			int anio = Integer.parseInt(fecha.substring(0, 4));
			int mes = Integer.parseInt(fecha.substring(4, 6));
			int dia = Integer.parseInt(fecha.substring(6, 8));
			int diaSemana = new GregorianCalendar(anio, mes - 1, dia).get(Calendar.DAY_OF_WEEK); // 1 = domingo
			double horaDecimal = Integer.parseInt(hora[0]) + Integer.parseInt(hora[1]) / 60.0;

			v.x[0] = Math.sin(2 * Math.PI * horaDecimal / 24);
			v.x[1] = Math.cos(2 * Math.PI * horaDecimal / 24);
			v.x[2] = Math.sin(2 * Math.PI * (mes - 1) / 12);
			v.x[3] = Math.cos(2 * Math.PI * (mes - 1) / 12);
			v.x[4] = Math.sin(2 * Math.PI * (diaSemana - 1) / 7);
			v.x[5] = Math.cos(2 * Math.PI * (diaSemana - 1) / 7);
			v.x[6] = latitud;
			v.x[7] = longitud;
			v.x[8] = progresiva.isEmpty() ? Double.NaN : Double.parseDouble(progresiva);
			for (int m = 0; m < MODALIDADES.length; m++) {
				v.x[9 + m] = MODALIDADES[m].equals(modalidad) ? 1 : 0;
			}
			v.x[17] = via.equals("VIA URBANA") ? 1 : 0;

			v.yClasificacion = fallecidos > 0 ? 1 : 0;
			v.yRegresion = Math.log(1 + heridos);
			// 20 % para test, elegido por el número de reporte: siempre el mismo reparto
			v.esTest = (v.id.hashCode() & 0x7fffffff) % 5 == 0;
			return v;
		} catch (RuntimeException e) {
			return null; // NumberFormatException, fecha u hora con otro formato
		}
	}
}
