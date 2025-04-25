package fyi.hrvanovicm.magacin.domain.products;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fyi.hrvanovicm.magacin.application.product.dto.ProductDetailsDTO;
import fyi.hrvanovicm.magacin.infrastructure.persistance.ProductRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;

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
    public List<ProductEntity> getAll() {
        return this.getAll(null);
    }

    @Transactional(readOnly = true)
    public List<ProductEntity> getAll(Specification<ProductEntity> specs) {
        return repository.findAll(specs);
    }

    @Transactional(readOnly = true)
    public Optional<ProductEntity> getById(long productId) {
        return repository.findById(productId);
    }

    public boolean exists(long productId) {
        return repository.existsById(productId);
    }

    @Transactional
    public void save(ProductEntity product) {
        repository.save(product);
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
    public void saveReceptions(long productId, List<ProductReceptionEntity> receptions) {
        var product = repository.findById(productId).orElseThrow();

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
    public void delete(long productId) {
        repository.deleteById(productId);
    }
}
