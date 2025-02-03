package fyi.hrvanovicm.magacin.domain.article.dto.response;

import fyi.hrvanovicm.magacin.domain.article.ArticleCategory;
import fyi.hrvanovicm.magacin.domain.common.embedded.AuditDTO;
import lombok.Data;

@Data
public class ArticleCategoryResponse {
    Long id;
    String name;
    AuditDTO audit;

    @SuppressWarnings("DuplicatedCode")
    public static ArticleCategoryResponse fromEntity(ArticleCategory entity) {
        var dto = new ArticleCategoryResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAudit(AuditDTO.fromEntity(entity.getAudit()));

        return dto;
    }
}
