package fyi.hrvanovicm.magacin.application.product.commands;

import fyi.hrvanovicm.magacin.application.product.dto.ProductDetailsDTO;
import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.application.product.requests.ProductEditRequest;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateProductHandler {
    private final ProductService productService;
    private final UnitMeasureService unitMeasureService;

    @Autowired
    public CreateProductHandler(ProductService productService, UnitMeasureService unitMeasureService) {
        this.productService = productService;
        this.unitMeasureService = unitMeasureService;
    }

    public ProductDetailsDTO handle(@Valid ProductEditRequest request) {
        UnitMeasureEntity unitMeasure = unitMeasureService.findById(request.getJmId()).orElseThrow(EntityNotFoundException::new);
        ProductEntity product = new ProductEntity();

        request.fill(product, unitMeasure);
        this.productService.save(product);

        return ProductDetailsDTO.fromEntity(product);
    }
}
