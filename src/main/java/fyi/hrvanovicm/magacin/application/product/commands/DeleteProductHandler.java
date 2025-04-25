package fyi.hrvanovicm.magacin.application.product.commands;

import fyi.hrvanovicm.magacin.application.BaseHandler;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteProductHandler extends BaseHandler {
    private final ProductService productService;

    @Autowired
    public DeleteProductHandler(ProductService productService) {
        this.productService = productService;
    }

    public void handle(long productId) {
        var isExists = this.productService.exists(productId);

        if(!isExists) {
            throw new EntityNotFoundException();
        }

        this.productService.delete(productId);
    }
}
