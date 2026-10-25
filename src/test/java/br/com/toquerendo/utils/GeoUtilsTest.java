package br.com.toquerendo.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class GeoUtilsTest {

    @Test
    void distanciaEmKm_entreMesmoPonto_deveSerZero() {
        assertThat(GeoUtils.distanciaEmKm(-8.1189, -34.8998, -8.1189, -34.8998)).isZero();
    }

    @Test
    void distanciaEmKm_umGrauDeLongitudeNoEquador_deveSerAproximadamente111Km() {
        assertThat(GeoUtils.distanciaEmKm(0, 0, 0, 1)).isCloseTo(111.19, within(0.05));
    }

    @Test
    void distanciaEmKm_deveSerSimetrica() {
        double ida = GeoUtils.distanciaEmKm(-8.1189, -34.8998, -8.0476, -34.8770);
        double volta = GeoUtils.distanciaEmKm(-8.0476, -34.8770, -8.1189, -34.8998);

        assertThat(ida).isEqualTo(volta);
    }

    @Test
    void distanciaEmKm_umGrauDeLatitude_deveSerAproximadamente111Km() {
        assertThat(GeoUtils.distanciaEmKm(-8, -34.9, -9, -34.9)).isCloseTo(111.19, within(0.05));
    }
}
