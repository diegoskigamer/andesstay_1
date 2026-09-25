package com.andesstay.service;

import com.andesstay.domain.Unit;
import com.andesstay.dto.UnitRequest;
import com.andesstay.dto.UnitUpdateRequest;
import com.andesstay.exception.NotFoundException;
import com.andesstay.repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogService {

    private final UnitRepository unitRepository;

    public CatalogService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public List<Unit> findAll() {
        return unitRepository.findAll();
    }

    public Unit findById(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidad no encontrada: " + id));
    }

    @Transactional
    public Unit create(UnitRequest request) {
        Unit unit = new Unit(
                request.getName(), request.getType(), request.getHostelName(),
                request.getCapacity(), request.getTotalCount(), request.getNightlyRate()
        );
        return unitRepository.save(unit);
    }

    @Transactional
    public Unit updateRateAndAvailability(Long id, UnitUpdateRequest request) {
        Unit unit = findById(id);
        if (request.getNightlyRate() != null) unit.setNightlyRate(request.getNightlyRate());
        if (request.getTotalCount() != null) unit.setTotalCount(request.getTotalCount());
        if (request.getAvailableCount() != null) unit.setAvailableCount(request.getAvailableCount());
        return unitRepository.save(unit);
    }

    /** Usado por ms-andesstay-reservations vía REST (ver CatalogController.changeAvailability). */
    @Transactional
    public Unit changeAvailability(Long id, String direction) {
        Unit unit = findById(id);
        if ("DECREASE".equalsIgnoreCase(direction)) {
            unit.decreaseAvailability();
        } else if ("INCREASE".equalsIgnoreCase(direction)) {
            unit.increaseAvailability();
        }
        return unitRepository.save(unit);
    }
}
