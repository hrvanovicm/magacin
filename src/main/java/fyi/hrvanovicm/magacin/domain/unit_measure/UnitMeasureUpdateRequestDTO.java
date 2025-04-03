package fyi.hrvanovicm.magacin.domain.unit_measure;

public final class UnitMeasureUpdateRequestDTO extends UnitMeasureRequestDTO {
    public UnitMeasure toEntity(Long id) {
        var entity = super.toEntity();
        entity.setId(id);
        return entity;
    }
}
