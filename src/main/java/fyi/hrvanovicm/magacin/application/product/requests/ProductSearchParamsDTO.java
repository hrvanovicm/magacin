package fyi.hrvanovicm.magacin.application.product.requests;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProductSearchParamsDTO {
    String search;
    Boolean isRawMaterial;
    List<String> categories;
    List<String> tags;
    Boolean lowInMemoryStock;
    Float fromInStockAmount;
}
