package br.com.toquerendo.utils;

public class GeoUtils {

    private static final double RAIO_TERRA_KM = 6371.0088;

    private GeoUtils() {
    }

    public static double distanciaEmKm(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        double deltaLatitude = Math.toRadians(latitudeB - latitudeA);
        double deltaLongitude = Math.toRadians(longitudeB - longitudeA);

        double a = Math.pow(Math.sin(deltaLatitude / 2), 2)
                + Math.cos(Math.toRadians(latitudeA)) * Math.cos(Math.toRadians(latitudeB))
                * Math.pow(Math.sin(deltaLongitude / 2), 2);

        return RAIO_TERRA_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
