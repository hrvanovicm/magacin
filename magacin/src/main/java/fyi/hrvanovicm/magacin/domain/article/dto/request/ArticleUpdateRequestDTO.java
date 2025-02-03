package fyi.hrvanovicm.magacin.domain.article.dto.request;

import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;

public final class ArticleUpdateRequestDTO extends ArticleRequestDTO {
    public Article toEntity(long id, UnitMeasure unitMeasure) {
        var entity = super.toEntity(unitMeasure);
        entity.setId(id);
        return entity;
    }
}
