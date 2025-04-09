package fyi.hrvanovicm.magacin.application.javafx.components;

import fyi.hrvanovicm.magacin.domain.products.ProductBasicResponse;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CellFactory {
    public static <T> Callback<TableColumn<T, ProductBasicResponse>, TableCell<T, ProductBasicResponse>> productSearchableCombo(Supplier<List<ProductBasicResponse>> dataCallback, BiConsumer<T, ProductBasicResponse> saveCallback) {
        return new Callback<TableColumn<T, ProductBasicResponse>, TableCell<T, ProductBasicResponse>>() {
            @Override
            public TableCell<T, ProductBasicResponse> call(TableColumn<T, ProductBasicResponse> param) {
                return new TableCell<>() {
                    private final SearchableComboBox<ProductBasicResponse> comboBox = new SearchableComboBox<>();

                    {
                        comboBox.setEditable(true);
                        comboBox.setConverter(new StringConverter<>() {
                            @Override
                            public String toString(ProductBasicResponse product) {
                                return product != null ? product.toString() : "Odaberite proizvod";
                            }

                            @Override
                            public ProductBasicResponse fromString(String string) {
                                return comboBox.getValue();
                            }
                        });
                        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                            if (getTableRow() != null && getTableRow().getItem() != null && newValue != null) {
                                saveCallback.accept(getTableRow().getItem(), newValue);
                            }
                        });
                    }

                    @Override
                    public void updateItem(ProductBasicResponse item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            var data = dataCallback.get();
                            comboBox.setItems(FXCollections.observableArrayList(data));
                            comboBox.setValue(item);
                            setGraphic(comboBox);
                        }
                    }
                };
            }
        };
    }

    public static <T> EventHandler<TableColumn.CellEditEvent<T, String>> amountTextField(BiConsumer<T, Float> saveCallback) {
        return event -> {
            String newValue = event.getNewValue();
            T editedItem = event.getRowValue();

            try {
                saveCallback.accept(editedItem, Float.parseFloat(newValue));
                event.getTableView().refresh();
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + newValue);
            }
        };
    }
}
