package fyi.hrvanovicm.magacin.domain.article;

import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(
        name = "article_has_tag",
        uniqueConstraints = @UniqueConstraint(columnNames = {"article_id", "name"}),
        indexes = @Index(columnList = "article_id")
)
public class ArticleTag {
    public static final int NAME_MAX_CHARACTERS = 64;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "article_id")
    Article article;

    @Column(nullable = false, length = NAME_MAX_CHARACTERS)
    String name;

    @Embedded
    Audit audit;
}
