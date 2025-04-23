package fyi.hrvanovicm.magacin.application.javafx.components;

import fyi.hrvanovicm.magacin.domain.products.ProductDTO;
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
    public static <T> Callback<TableColumn<T, ProductDTO>, TableCell<T, ProductDTO>> productSearchableCombo(
            Supplier<List<ProductDTO>> dataCallback,
            BiConsumer<T, ProductDTO> saveCallback
    ) {
        return new Callback<>() {
            @Override
            public TableCell<T, ProductDTO> call(TableColumn<T, ProductDTO> param) {
                return new TableCell<>() {
                    private final SearchableComboBox<ProductDTO> comboBox = new SearchableComboBox<>();
                    private boolean isEditingDone = false;
                    private boolean isUpdating = false; {
                        comboBox.setEditable(true);
                        comboBox.setConverter(new StringConverter<>() {
                            @Override
                            public String toString(ProductDTO product) {
                                return product != null ? product.toString() : "Odaberite proizvod";
                            }

                            @Override
                            public ProductDTO fromString(String string) {
                                return comboBox.getValue();
                            }
                        });

                        comboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
                            if (isUpdating || newValue == null || getTableRow() == null || getTableRow().getItem() == null)
                                return;

                            T rowItem = getTableRow().getItem();
                            saveCallback.accept(rowItem, newValue);


                            isEditingDone = true;
                            comboBox.setDisable(true);

                            getTableView().refresh();

                            comboBox.setDisable(true);
                        });
                    }

                    @Override
                    protected void updateItem(ProductDTO item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            if (item == null) {
                                isUpdating = true;
                                var data = dataCallback.get();
                                comboBox.setItems(FXCollections.observableArrayList(data));
                                comboBox.setValue(null);
                                isUpdating = false;

                                setGraphic(comboBox);
                                setText(null);
                            } else {
                                if (isEditingDone) {
                                    setText(item.toString());
                                    setGraphic(null);
                                } else {
                                    isUpdating = true;
                                    var data = dataCallback.get();
                                    comboBox.setItems(FXCollections.observableArrayList(data));
                                    comboBox.setValue(item);
                                    isUpdating = false;

                                    if(comboBox.getSelectionModel().getSelectedItem() == null) {
                                        setGraphic(comboBox);
                                        setText(null);
                                    } else {
                                        setGraphic(null);
                                        setText(comboBox.getSelectionModel().getSelectedItem().toString());
                                    }
                                }
                            }
                        }
                    }
                };
            }
        };
    }

    public static <T> EventHandler<TableColumn.CellEditEvent<T, String>> amountTextField(
            BiConsumer<T, Float> saveCallback
    ) {
        return event -> {
            String newValue = event.getNewValue();
            T editedItem = event.getRowValue();

            try {
                saveCallback.accept(editedItem, Float.parseFloat(newValue.split(" ")[0]));
                event.getTableView().refresh();
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + newValue);
            }
        };
    }
}
