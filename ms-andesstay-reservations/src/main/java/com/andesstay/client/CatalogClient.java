package com.andesstay.client;

import com.andesstay.dto.UnitDto;
import com.andesstay.exception.BusinessException;
import com.andesstay.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Cliente REST hacia ms-andesstay-catalog. En una arquitectura de
 * microservicios, cada servicio es dueño de su propia base de datos —
 * reservations NO puede leer la tabla "units" directamente, así que
 * consulta y actualiza disponibilidad a través de la API HTTP de catalog.
 *
 * CATALOG_SVC_URL se configura por variable de entorno (ver application.yml
 * y docker-compose): dentro de Docker apunta al nombre del servicio
 * (http://ms-andesstay-catalog:8082); en local, a localhost:8082.
 */
@Component
public class CatalogClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${catalog.service-url}")
    private String catalogServiceUrl;

    public UnitDto getUnit(Long unitId) {
        try {
            UnitDto unit = restTemplate.getForObject(catalogServiceUrl + "/api/catalog/units/" + unitId, UnitDto.class);
            if (unit == null) throw new NotFoundException("Unidad no encontrada: " + unitId);
            return unit;
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Unidad no encontrada: " + unitId);
        } catch (Exception e) {
            throw new BusinessException("No se pudo contactar a ms-andesstay-catalog: " + e.getMessage());
        }
    }

    public void changeAvailability(Long unitId, String direction) {
        try {
            restTemplate.postForObject(
                    catalogServiceUrl + "/api/catalog/units/" + unitId + "/availability",
                    Map.of("direction", direction),
                    Void.class
            );
        } catch (Exception e) {
            throw new BusinessException("No se pudo actualizar disponibilidad en ms-andesstay-catalog: " + e.getMessage());
        }
    }
}
