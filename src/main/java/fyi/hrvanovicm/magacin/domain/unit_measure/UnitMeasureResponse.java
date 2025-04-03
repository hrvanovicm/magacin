package fyi.hrvanovicm.magacin.domain.unit_measure;

import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import lombok.Data;

@Data
public class UnitMeasureResponse {
    Long id;
    String name;
    String shortName;
    Boolean isInteger;
    AuditDTO audit;

    public static UnitMeasureResponse fromEntity(UnitMeasure entity) {
        var dto = new UnitMeasureResponse();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setShortName(entity.getShortName());
        dto.setIsInteger(entity.getIsInteger());
        dto.setAudit(AuditDTO.fromEntity(entity.getAudit()));

        return dto;
    }

    public UnitMeasure toEntity() {
        var entity = new UnitMeasure();

        entity.setId(getId());
        entity.setName(getName());
        entity.setShortName(getShortName());
        entity.setIsInteger(getIsInteger());
        entity.setAudit(getAudit().toEntity());

        return entity;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, shortName);
    }
}
