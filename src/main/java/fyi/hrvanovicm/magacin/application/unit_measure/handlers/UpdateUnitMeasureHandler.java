package fyi.hrvanovicm.magacin.application.unit_measure.handlers;

import fyi.hrvanovicm.magacin.application.BaseHandler;
import fyi.hrvanovicm.magacin.application.unit_measure.dto.UnitMeasureDTO;
import fyi.hrvanovicm.magacin.application.unit_measure.requests.UnitMeasureEditRequest;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateUnitMeasureHandler extends BaseHandler {
    private final UnitMeasureService unitMeasureService;

    @Autowired
    public UpdateUnitMeasureHandler(UnitMeasureService unitMeasureService) {
        this.unitMeasureService = unitMeasureService;
    }

    public UnitMeasureDTO handle(long unitMeasureId, @Valid UnitMeasureEditRequest request) {
        var unitMeasure = this.unitMeasureService.findById(unitMeasureId).orElseThrow(EntityNotFoundException::new);
        request.fill(unitMeasure);
        this.unitMeasureService.save(unitMeasure);

        return UnitMeasureDTO.fromEntity(unitMeasure);
    }
}
