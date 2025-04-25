package fyi.hrvanovicm.magacin.domain.report;

import fyi.hrvanovicm.magacin.domain.products.ProductCategory;
import lombok.Getter;

@Getter
public enum ReportType {
    RECEIPT("RECEIPT"),
    SHIPMENT("SHIPMENT");

    private final String value;

    ReportType(String value) {
        this.value = value;
    }

    public static ReportType fromValue(String value) {
        for (ReportType category : ReportType.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    @Override
    public String toString() {
        return switch (this) {
            case RECEIPT -> "Primka";
            case SHIPMENT -> "Otpremnica";
        };
    }
}
