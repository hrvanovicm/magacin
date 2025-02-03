package fyi.hrvanovicm.magacin.domain.unit_measure.dto.request;

import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;

public final class UnitMeasureUpdateRequestDTO extends UnitMeasureRequestDTO {
    public UnitMeasure toEntity(Long id) {
        var entity = super.toEntity();
        entity.setId(id);
        return entity;
    }
}
