package fyi.hrvanovicm.magacin.domain.products;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionUpdateRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import jakarta.validation.Valid;

@Component
public class ProductService {

    private final ProductRepository repository;
    private final UnitMeasureService unitMeasureService;

    @Autowired
    public ProductService(
        final ProductRepository productRepository,
        final UnitMeasureService unitMeasureService
    ) {
        this.repository = productRepository;
        this.unitMeasureService = unitMeasureService;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAll() {
        return this.getAll(null);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAll(Specification<ProductEntity> specs) {
        return repository
            .findAll(specs)
            .stream()
            .map(ProductDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDetailsDTO getById(long productId) {
        return repository
                .findById(productId)
                .map(ProductDetailsDTO::fromEntity)
                .orElse(null);
    }

    @Transactional
    public ProductDetailsDTO create(@Valid ProductRequest request) {
        var unitMeasure = unitMeasureService
            .getById(request.getJmId())
            .orElseThrow()
            .toEntity();

        var product = request.toEntity(unitMeasure);

        repository.save(product);

        return ProductDetailsDTO.fromEntity(product);
    }

    @Transactional
    public ProductDetailsDTO update(
        @NotNull Long productId,
        @Valid ProductRequest request
    ) {
        var unitMeasure = unitMeasureService
            .getById(request.getJmId())
            .orElseThrow()
            .toEntity();
        var product = request.toEntity(productId, unitMeasure);

        repository.save(product);

        return ProductDetailsDTO.fromEntity(product);
    }

    @Transactional
    public ProductDetailsDTO increaseStock(
            @NotNull Long productId,
            Float amount
    ) {
        var product = repository.findById(productId).orElseThrow();
        product.setInStockAmount(product.getInStockAmount() + amount);
        repository.save(product);

        return ProductDetailsDTO.fromEntity(product);
    }

    @Transactional
    public ProductDetailsDTO decreaseStock(
            @NotNull Long productId,
            Float amount
    ) {
        var product = repository.findById(productId).orElseThrow();
        product.setInStockAmount(product.getInStockAmount() - amount);
        repository.save(product);

        return ProductDetailsDTO.fromEntity(product);
    }

    @Transactional
    public void updateReceptions(
            @NotNull Long productId,
            List<ProductReceptionUpdateRequest> receptionRequests
    ) {
        var product = repository.findById(productId).orElseThrow();

        var receptions = receptionRequests.stream().map(request -> {
           var rawMaterialProduct = repository.findById(request.getRawMaterialId()).orElseThrow();
           return request.toEntity(product, rawMaterialProduct);
        }).collect(Collectors.toList());

        product.getReceptions().clear();
        product.getReceptions().addAll(receptions);

        repository.save(product);
    }

    public List<String> getAllProductNames() {
        return repository.findDistinctProductNames();
    }

    public List<String> getAllTagNames() {
        return repository.findDistinctTagNames();
    }

    public List<ProductCategory> getAllCategoryNames() {
        return Arrays.stream(ProductCategory.values()).collect(Collectors.toList());
    }

    @Transactional
    public void delete(@NotNull Long id) {
        var product = repository.findById(id).orElseThrow();
        repository.delete(product);
    }
}
