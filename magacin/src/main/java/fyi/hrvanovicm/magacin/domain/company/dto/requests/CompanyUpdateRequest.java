package fyi.hrvanovicm.magacin.domain.company.dto.requests;

import fyi.hrvanovicm.magacin.domain.company.Company;

public final class CompanyUpdateRequest extends CompanyRequestDTO {
    public Company toEntity(long id) {
        var entity = super.toEntity();
        entity.setId(id);
        return entity;
    }
}
