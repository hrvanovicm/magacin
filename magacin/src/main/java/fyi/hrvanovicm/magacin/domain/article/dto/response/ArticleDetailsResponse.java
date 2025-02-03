package fyi.hrvanovicm.magacin.domain.article.dto.response;

import fyi.hrvanovicm.magacin.domain.article.Article;
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
public class ArticleDetailsResponse {
    Long id;
    String name;
    String description;
    String code;
    Float inStockAmount;
    UnitMeasureResponse jm;
    AuditDTO audit;

    @SuppressWarnings("DuplicatedCode")
    public static ArticleDetailsResponse fromEntity(Article article) {
        var dto = new ArticleDetailsResponse();
        dto.setId(article.getId());
        dto.setName(article.getName());
        dto.setCode(article.getCode());
        dto.setInStockAmount(article.getInStockAmount());
        dto.setJm(UnitMeasureResponse.fromEntity(article.getUnitMeasure()));

        return dto;
    }
}
