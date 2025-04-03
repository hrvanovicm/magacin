package fyi.hrvanovicm.magacin.domain.products.tag;

import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.common.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(
        name = "product_has_tag",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "name"}),
        indexes = @Index(columnList = "product_id")
)
public class ProductTagEntity {
    public static final int NAME_MAX_CHARACTERS = 64;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    ProductEntity product;

    @Column(nullable = false, length = NAME_MAX_CHARACTERS)
    String name;

    @Embedded
    Audit audit;
}
