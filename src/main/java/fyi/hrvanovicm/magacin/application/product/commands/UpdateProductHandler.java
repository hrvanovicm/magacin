package fyi.hrvanovicm.magacin.application.product.commands;

import fyi.hrvanovicm.magacin.application.BaseHandler;
import fyi.hrvanovicm.magacin.application.product.dto.ProductDetailsDTO;
import fyi.hrvanovicm.magacin.domain.products.ProductEntity;
import fyi.hrvanovicm.magacin.application.product.requests.ProductEditRequest;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.products.ProductReceptionEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureEntity;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateProductHandler extends BaseHandler {
    private final ProductService productService;
    private final UnitMeasureService unitMeasureService;

    @Autowired
    public UpdateProductHandler(ProductService productService, UnitMeasureService unitMeasureService) {
        this.productService = productService;
        this.unitMeasureService = unitMeasureService;
    }

    @Transactional
    public ProductDetailsDTO handle(long productId, ProductEditRequest request) {
        UnitMeasureEntity unitMeasure = unitMeasureService.findById(request.getJmId()).orElseThrow(EntityNotFoundException::new);
        ProductEntity product = productService.getById(productId).orElseThrow(EntityNotFoundException::new);

        request.fill(product, unitMeasure);
        this.productService.save(product);

        var receptions = request.getReceptions().stream().map(receptionRequest -> {
            var reception = product.getReceptions().stream()
                    .filter(rec -> rec.getId().equals(receptionRequest.getId()))
                    .findFirst()
                    .orElseGet(ProductReceptionEntity::new);
            var rawMaterialProduct = this.productService.getById(receptionRequest.getRawMaterialId()).orElseThrow(EntityNotFoundException::new);
            receptionRequest.fill(reception, product, rawMaterialProduct);
            return reception;
        }).toList();

        this.productService.saveReceptions(productId, receptions);

        return ProductDetailsDTO.fromEntity(product);
    }
}
