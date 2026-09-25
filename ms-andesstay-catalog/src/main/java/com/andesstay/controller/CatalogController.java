package com.andesstay.controller;

import com.andesstay.domain.Unit;
import com.andesstay.dto.AvailabilityChangeRequest;
import com.andesstay.dto.UnitRequest;
import com.andesstay.dto.UnitUpdateRequest;
import com.andesstay.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expuesto internamente en la red Docker como /api/catalog/*.
 * El único punto de entrada público desde Internet es ms-andesstay-bff,
 * que reenvía las requests hacia acá.
 */
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/units")
    public List<Unit> listUnits() {
        return catalogService.findAll();
    }

    @GetMapping("/units/{id}")
    public Unit getUnit(@PathVariable Long id) {
        return catalogService.findById(id);
    }

    @PostMapping("/units")
    public ResponseEntity<Unit> createUnit(@Valid @RequestBody UnitRequest request) {
        Unit created = catalogService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/units/{id}")
    public Unit updateUnit(@PathVariable Long id, @RequestBody UnitUpdateRequest request) {
        return catalogService.updateRateAndAvailability(id, request);
    }

    /** Llamado internamente por ms-andesstay-reservations (CatalogClient). */
    @PostMapping("/units/{id}/availability")
    public Unit changeAvailability(@PathVariable Long id, @RequestBody AvailabilityChangeRequest request) {
        return catalogService.changeAvailability(id, request.getDirection());
    }
}
