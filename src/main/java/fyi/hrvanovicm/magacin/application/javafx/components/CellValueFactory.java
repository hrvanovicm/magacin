package fyi.hrvanovicm.magacin.application.javafx.components;

import fyi.hrvanovicm.magacin.domain.products.ProductDTO;
import fyi.hrvanovicm.magacin.domain.report.Report;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureResponse;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Function;

public class CellValueFactory {

    public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> sequenceNumber(TableView<?> tableView) {
        return cellData -> new SimpleStringProperty(
                tableView.getItems().indexOf(cellData.getValue()) + 1 + "."
        );
    }

    public static <T> Callback<TableColumn.CellDataFeatures<UnitMeasureResponse, String>, ObservableValue<String>> unitMeasureName() {
        return cellData -> new SimpleStringProperty(cellData.getValue().getName());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<UnitMeasureResponse, String>, ObservableValue<String>> unitMeasureShortName() {
        return cellData -> new SimpleStringProperty(cellData.getValue().getShortName());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<UnitMeasureResponse, Boolean>, ObservableValue<Boolean>> unitMeasureIsInteger() {
        return cellData -> new SimpleBooleanProperty(cellData.getValue().getIsInteger());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<T, ProductDTO>, ObservableValue<ProductDTO>> product(
            Function<T, ProductDTO> productCallback
    ) {
        return cellData -> new SimpleObjectProperty<>(productCallback.apply(cellData.getValue()));
    }

    public static <T> Callback<TableColumn.CellDataFeatures<ProductDTO, String>, ObservableValue<String>> productName() {
        return cellData -> new SimpleStringProperty(cellData.getValue().getName());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<ProductDTO, String>, ObservableValue<String>> productCode() {
        return cellData -> new SimpleStringProperty(cellData.getValue().getCode());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<ProductDTO, String>, ObservableValue<String>> productCategory() {
        return cellData -> new SimpleStringProperty(cellData.getValue().getCategory().toString());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<ProductDTO, String>, ObservableValue<String>> productTag() {
        return cellData -> new SimpleStringProperty(cellData.getValue().getCategory().toString());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<Report, String>, ObservableValue<String>> reportType() {
        return cellData -> new SimpleStringProperty(cellData.getValue().getType().toString());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> reportType(
            Function<T, Report> reportExtractor
    ) {
        return cellData -> new SimpleStringProperty(reportExtractor.apply(cellData.getValue()).getType().toString());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<Report, String>, ObservableValue<String>> reportCode() {
        return cellData -> new SimpleStringProperty(cellData.getValue().getCode());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> reportCode(
            Function<T, Report> reportExtractor
    ) {
        return cellData -> new SimpleStringProperty(reportExtractor.apply(cellData.getValue()).getCode());
    }

    public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> unitMeasure(
            Function<T, ProductDTO> reportExtractor
    ) {
        return cellData -> {
            var product = reportExtractor.apply(cellData.getValue());

            if(product == null || product.getUnitMeasure() == null) {
                return new SimpleStringProperty("-");
            }

            return new SimpleStringProperty(
                    reportExtractor.apply(cellData.getValue()).getUnitMeasure().toString()
            );
        };
    }

    public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> reportSupplierCode(
            Function<T, Report> reportExtractor
    ) {
        return cellData -> {
            var report = reportExtractor.apply(cellData.getValue());

            if(report.getReceipt() == null) {
                return new SimpleStringProperty("-");
            }

            return new SimpleStringProperty(
                    report.getReceipt().getSupplierReportCode()
            );
        };
    }

    public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> reportCompanyName(
            Function<T, Report> reportExtractor
    ) {
        return cellData -> {
            var report = reportExtractor.apply(cellData.getValue());

            if(report.getReceipt() != null) {
                return new SimpleStringProperty(
                        report.getReceipt().getSupplierCompanyName()
                );
            }

            return new SimpleStringProperty(
                    report.getShipment().getReceiptCompanyName()
            );
        };
    }

    public static Callback<TableColumn.CellDataFeatures<Report, String>, ObservableValue<String>> reportDate() {
        return CellValueFactory.reportDate(v -> v);
    }

    public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> reportDate(
            Function<T, Report> reportExtractor
    ) {
        return cellData -> {
            String dateString = reportExtractor.apply(cellData.getValue()).getDate();
            if (dateString != null && !dateString.isEmpty()) {
                LocalDate localDate = LocalDate.parse(dateString);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMM yyyy");
                String formattedDate = localDate.format(formatter);
                return new SimpleStringProperty(formattedDate);
            } else {
                return new SimpleStringProperty("");
            }
        };
    }

    public static <T> Callback<TableColumn.CellDataFeatures<Report, String>, ObservableValue<String>> reportSignedByName() {
        return cellData -> new SimpleStringProperty(cellData.getValue().getSignedByName());
    }

    public static <T> Callback<
            TableColumn.CellDataFeatures<T, String>,
            ObservableValue<String>>
    amount(
            Function<T, ProductDTO> productExtractor,
            Function<T, Float> amountExtractor,
            boolean showUnitMeasure
    ) {
        return cellData -> {
            var product = productExtractor.apply(cellData.getValue());
            var amount = amountExtractor.apply(cellData.getValue());

            if(product == null || !Objects.requireNonNull(product.getUnitMeasure()).getIsInteger()) {
                return new SimpleStringProperty(
                        String.format("%.2f", amount)
                );
            }

            var unitMeasureVal = !showUnitMeasure ? "" : product.getUnitMeasure().toString();

            return new SimpleStringProperty(
                    String.format("%.0f %s", amount,unitMeasureVal)
            );
        };
    }
}
