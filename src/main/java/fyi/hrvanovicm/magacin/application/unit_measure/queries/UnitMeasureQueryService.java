package fyi.hrvanovicm.magacin.application.unit_measure.queries;

import fyi.hrvanovicm.magacin.application.unit_measure.dto.UnitMeasureDTO;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UnitMeasureQueryService {
    private final UnitMeasureService unitMeasureService;

    @Autowired
    public UnitMeasureQueryService(UnitMeasureService unitMeasureService) {
        this.unitMeasureService = unitMeasureService;
    }

    public List<UnitMeasureDTO> getAll() {
        return this.unitMeasureService.findAll()
                .stream()
                .map(UnitMeasureDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public UnitMeasureDTO getById(long id) {
        return this.unitMeasureService.findById(id)
                .map(UnitMeasureDTO::fromEntity)
                .orElseThrow(EntityNotFoundException::new);
    }
}
