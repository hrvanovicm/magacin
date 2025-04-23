package fyi.hrvanovicm.magacin.domain.report;

public enum ReportType {
    RECEIPT,
    SHIPMENT;

    @Override
    public String toString() {
        return switch (this) {
            case RECEIPT -> "Primka";
            case SHIPMENT -> "Otpremnica";
        };
    }
}
