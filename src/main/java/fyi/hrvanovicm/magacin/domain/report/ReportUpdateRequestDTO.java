package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;

public final class ReportUpdateRequestDTO extends ReportRequestDTO {
    public ProductEntity toEntity(long id, UnitMeasure unitMeasure) {
        var entity = super.toEntity(unitMeasure);
        entity.setId(id);
        return entity;
    }
}
