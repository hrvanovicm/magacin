package fyi.hrvanovicm.magacin.domain.report.dto.requests;

import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;

public final class ReportCreateRequestDTO extends ReportRequestDTO {
    @Override
    public Article toEntity(UnitMeasure unitMeasure) {
        return super.toEntity(unitMeasure);
    }
}
