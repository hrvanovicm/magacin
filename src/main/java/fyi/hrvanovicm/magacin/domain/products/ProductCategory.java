package fyi.hrvanovicm.magacin.domain.products;

public enum ProductCategory {
    COMMERCIAL,
    PRODUCT,
    RAW_MATERIAL;

    @Override
    public String toString() {
        switch (this) {
            case COMMERCIAL:
                return "Komercijala";
            case PRODUCT:
                return "Proizvod";
            case RAW_MATERIAL:
                return "Sirovina";
            default:
                return "Nije poznato";
        }
    }
}
