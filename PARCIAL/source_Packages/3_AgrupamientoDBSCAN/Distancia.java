package AgrupamientoDBSCAN;

// Distancia en km entre dos coordenadas (latitud, longitud en grados) sobre la
// superficie terrestre: fórmula de haversine. La usan la vecindad y las métricas.
public class Distancia {

	private static final double RADIO_TIERRA_KM = 6371.0088;

	public static double km(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		return 2 * RADIO_TIERRA_KM * Math.asin(Math.sqrt(a));
	}
}
