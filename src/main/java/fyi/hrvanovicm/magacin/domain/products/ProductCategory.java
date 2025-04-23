package fyi.hrvanovicm.magacin.domain.products;

import lombok.Getter;

@Getter
public enum ProductCategory {
    COMMERCIAL("KOMERCIJALA"),
    PRODUCT("PROIZVOD"),
    RAW_MATERIAL("SIROVINA");

    private final String value;

    ProductCategory(String value) {
        this.value = value;
    }

    public static ProductCategory fromValue(String value) {
        for (ProductCategory category : ProductCategory.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    @Override
    public String toString() {
        return switch (this) {
            case COMMERCIAL -> "Komercijala";
            case PRODUCT -> "Proizvod";
            case RAW_MATERIAL -> "Sirovina";
        };
    }
}
