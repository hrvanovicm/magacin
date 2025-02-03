package fyi.hrvanovicm.magacin.domain.article;

import fyi.hrvanovicm.magacin.domain.article.utils.ArticleValidationRulesUtils;
import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasure;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.type.YesNoConverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "articles")
@SoftDelete(strategy = SoftDeleteType.ACTIVE, converter = YesNoConverter.class)
public class Article {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_measure_id", nullable = false)
    private UnitMeasure unitMeasure;

    @OneToMany(mappedBy = "article", fetch = FetchType.EAGER)
    private List<ArticleTag> tags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private ArticleCategory category;

    @Column(nullable = false, length = ArticleValidationRulesUtils.NAME_MAX_CHARACTERS)
    private String name;

    @Column(nullable = false, length = ArticleValidationRulesUtils.CODE_MAX_CHARACTERS, unique = true)
    private String code;

    @Column(length = ArticleValidationRulesUtils.DESCRIPTION_MAX_CHARACTERS)
    private String description;

    @Column(columnDefinition = "DECIMAL(10,2)", nullable = false)
    @ColumnDefault(value = "0")
    private Float inStockAmount;

    @Embedded
    private Audit audit;
}
