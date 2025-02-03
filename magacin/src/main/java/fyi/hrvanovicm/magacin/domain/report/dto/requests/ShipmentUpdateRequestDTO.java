package fyi.hrvanovicm.magacin.domain.report.dto.requests;

import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;

public final class ShipmentUpdateRequestDTO extends ShipmentReportRequestDTO {
    public Article toEntity(long id, UnitMeasure unitMeasure) {
        var entity = super.toEntity(unitMeasure);
        entity.setId(id);
        return entity;
    }
}
