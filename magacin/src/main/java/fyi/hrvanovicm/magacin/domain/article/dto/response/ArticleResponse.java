package fyi.hrvanovicm.magacin.domain.article.dto.response;

import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.article.ArticleCategory;
import fyi.hrvanovicm.magacin.domain.unit_measure.dto.response.UnitMeasureResponse;
import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ArticleResponse {
    Long id;
    String name;
    ArticleCategoryResponse category;
    String description;
    String code;
    Float inStockAmount;
    UnitMeasureResponse unitMeasure;
    AuditDTO audit;

    @SuppressWarnings("DuplicatedCode")
    public static ArticleResponse fromEntity(Article article) {
        var dto = new ArticleResponse();
        dto.setId(article.getId());
        dto.setCategory(ArticleCategoryResponse.fromEntity(article.getCategory()));
        dto.setName(article.getName());
        dto.setCode(article.getCode());
        dto.setInStockAmount(article.getInStockAmount());
        dto.setUnitMeasure(UnitMeasureResponse.fromEntity(article.getUnitMeasure()));

        return dto;
    }
}
