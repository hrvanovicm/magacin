package fyi.hrvanovicm.magacin.application.javafx.components;

import fyi.hrvanovicm.magacin.domain.products.ProductBasicResponse;
import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionBasicResponse;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductResponse;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class CellValueFactory {
    public static Callback<
                TableColumn.CellDataFeatures<ReportProductResponse, String>,
                ObservableValue<String>>
    productAmount() {
        return cellData -> {
            if(cellData.getValue().getProduct() == null) {
                return new SimpleObjectProperty<>("0");
            }

            return new SimpleStringProperty(
                    String.format(
                            cellData.getValue().getProduct().getUnitMeasure().getIsInteger()
                                    ? "%.0f"
                                    : "%.2f",
                            cellData.getValue().getAmount()
                    )
            );
        };
    }

    public static Callback<
            TableColumn.CellDataFeatures<ProductReceptionBasicResponse, String>,
            ObservableValue<String>>
    receptionAmount(
            Function<ProductReceptionBasicResponse, ProductBasicResponse> productCallback
    ) {
        return cellData -> {
            var value = cellData.getValue();
            var product = productCallback.apply(value);

            if(product == null) {
                return new SimpleObjectProperty<>("0");
            }

            if(product.getUnitMeasure() == null) {
                return new SimpleStringProperty(String.format("%.2f", cellData.getValue().getAmount()));
            }

            return new SimpleStringProperty(
                    String.format(
                            product.getUnitMeasure().getIsInteger()
                                    ? "%.0f"
                                    : "%.2f",
                            cellData.getValue().getAmount()
                    )
            );
        };
    }

    public static <S> Callback<TableColumn.CellDataFeatures<S, String>, ObservableValue<String>>
        reportDate(Function<S, String> dateExtractor) {
        return cellData -> {
            String dateString = dateExtractor.apply(cellData.getValue());
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
}
