package fyi.hrvanovicm.magacin.domain.report.dto.responses;

import fyi.hrvanovicm.magacin.domain.article.Article;
import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import fyi.hrvanovicm.magacin.domain.unit_measure.dto.response.UnitMeasureResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportResponse {
    Long id;
    String name;
    String description;
    String code;
    Float inStockAmount;
    UnitMeasureResponse jm;
    AuditDTO audit;

    @SuppressWarnings("DuplicatedCode")
    public static ReportResponse fromEntity(Article article) {
        var dto = new ReportResponse();
        dto.setId(article.getId());
        dto.setName(article.getName());
        dto.setCode(article.getCode());
        dto.setInStockAmount(article.getInStockAmount());
        dto.setJm(UnitMeasureResponse.fromEntity(article.getUnitMeasure()));

        return dto;
    }

    @SuppressWarnings("DuplicatedCode")
    public Article toEntity() {
        Article entity = new Article();

        entity.setId(this.getId());
        entity.setName(this.getName());
        entity.setCode(this.getCode());
        entity.setInStockAmount(this.getInStockAmount());
        entity.setUnitMeasure(this.getJm().toEntity());

        return entity;
    }
}
