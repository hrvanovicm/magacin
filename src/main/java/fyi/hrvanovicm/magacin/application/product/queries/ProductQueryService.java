package fyi.hrvanovicm.magacin.application.product.queries;

import fyi.hrvanovicm.magacin.application.product.dto.ProductDTO;
import fyi.hrvanovicm.magacin.application.product.dto.ProductDetailsDTO;
import fyi.hrvanovicm.magacin.application.product.requests.ProductSearchParamsDTO;
import fyi.hrvanovicm.magacin.domain.products.ProductCategory;
import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.products.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductQueryService {
    private final ProductService productService;

    @Autowired
    public ProductQueryService(ProductService productService) {
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAll() {
        List<ProductEntity> products = this.productService.getAll(null);
        return products
                .stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAll(ProductSearchParamsDTO params) {
        Specification<ProductEntity> spec = Specification.where(null);

        if (params.getSearch() != null && !params.getSearch().isEmpty()) {
            spec = spec.and(ProductSpecification.search(params.getSearch()));
        }

        if (params.getIsRawMaterial() != null) {
            spec = spec.and(ProductSpecification.isRawMaterial());
        }

        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            List<ProductCategory> categories = params.getCategories().stream()
                    .map(ProductCategory::fromValue) // Assuming categories are passed as string values of the enum
                    .collect(Collectors.toList());

            spec = spec.and(ProductSpecification.hasCategory(categories));
        }

        if (params.getTags() != null && !params.getTags().isEmpty()) {
            spec = spec.and(ProductSpecification.hasTags(params.getTags()));
        }

        List<ProductEntity> products = this.productService.getAll(spec);
        return products
                .stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDetailsDTO getById(long id) {
        return this.productService.getById(id)
                .map(ProductDetailsDTO::fromEntity)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<String> getAllProductNames() {
        return this.productService.getAllProductNames();
    }

    public List<String> getAllTagNames() {
        return this.productService.getAllTagNames();
    }

    public List<ProductCategory> getAllCategoryNames() {
        return this.productService.getAllCategoryNames();
    }
}
