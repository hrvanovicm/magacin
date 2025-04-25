package fyi.hrvanovicm.magacin.application.unit_measure.handlers;

import fyi.hrvanovicm.magacin.application.BaseHandler;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteUnitMeasureHandler extends BaseHandler {
    private final UnitMeasureService unitMeasureService;

    @Autowired
    public DeleteUnitMeasureHandler(UnitMeasureService unitMeasureService) {
        this.unitMeasureService = unitMeasureService;
    }

    public void handle(long unitMeasureId) {
        var isExists = this.unitMeasureService.exists(unitMeasureId);

        if(!isExists) {
            throw new EntityNotFoundException();
        }

        this.unitMeasureService.delete(unitMeasureId);
    }
}
