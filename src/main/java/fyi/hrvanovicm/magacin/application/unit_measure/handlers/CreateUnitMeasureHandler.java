package fyi.hrvanovicm.magacin.application.unit_measure.handlers;

import fyi.hrvanovicm.magacin.application.BaseHandler;
import fyi.hrvanovicm.magacin.application.product.dto.ProductDetailsDTO;
import fyi.hrvanovicm.magacin.application.unit_measure.dto.UnitMeasureDTO;
import fyi.hrvanovicm.magacin.application.unit_measure.requests.UnitMeasureEditRequest;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateUnitMeasureHandler extends BaseHandler {
    private final UnitMeasureService unitMeasureService;

    @Autowired
    public CreateUnitMeasureHandler(UnitMeasureService unitMeasureService) {
        this.unitMeasureService = unitMeasureService;
    }

    public UnitMeasureDTO handle(@Valid UnitMeasureEditRequest request) {
        var unitMeasure = new UnitMeasureEntity();
        request.fill(unitMeasure);
        this.unitMeasureService.save(unitMeasure);

        return UnitMeasureDTO.fromEntity(unitMeasure);
    }
}
