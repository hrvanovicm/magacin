package fyi.hrvanovicm.magacin.domain.article;

import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "article_categories")
public class ArticleCategory {

    public static final int NAME_MAX_CHARACTERS = 64;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = NAME_MAX_CHARACTERS, unique = true)
    String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    Set<Article> article = new HashSet<>();

    @Embedded
    Audit audit;
}
