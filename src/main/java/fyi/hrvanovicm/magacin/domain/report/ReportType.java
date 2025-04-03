package fyi.hrvanovicm.magacin.domain.report;

public enum ReportType {
    RECEIPT,
    SHIPMENT;

    @Override
    public String toString() {
        switch (this) {
            case RECEIPT:
                return "Primka";
            case SHIPMENT:
                return "Otpremnica";
            default:
                return "Nije poznato";
        }
    }
}
