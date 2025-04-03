package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;

public final class ReportCreateRequestDTO extends ReportRequestDTO {
    @Override
    public ProductEntity toEntity(UnitMeasure unitMeasure) {
        return super.toEntity(unitMeasure);
    }
}
