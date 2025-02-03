package fyi.hrvanovicm.magacin.domain.article.dto.request;

import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;

public final class ArticleCreateRequestDTO extends ArticleRequestDTO {
    @Override
    public Article toEntity(UnitMeasure unitMeasure) {
        return super.toEntity(unitMeasure);
    }
}
