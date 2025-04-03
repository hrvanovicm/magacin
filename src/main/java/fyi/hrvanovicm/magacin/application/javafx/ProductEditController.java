package fyi.hrvanovicm.magacin.application.javafx;

import fyi.hrvanovicm.magacin.domain.products.*;
import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionBasicResponse;
import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionUpdateRequest;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductResponse;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureResponse;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import fyi.hrvanovicm.magacin.infrastructure.javafx.Router;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.web.HTMLEditor;
import javafx.util.Callback;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.SearchableComboBox;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@FxmlView("/views/product-edit.fxml")
public class ProductEditController {
    // FXML Product input.
    @FXML
    private Label titleLabel;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField codeInput;
    @FXML
    private ComboBox<ProductCategory> categoryCombo;
    @FXML
    private TextField tagInput;
    @FXML
    private TextField inStockAmountInput;
    @FXML
    private ComboBox<UnitMeasureResponse> unitMeasureCombo;
    @FXML
    private TextField inStockWarningAmountInput;
    @FXML
    private Button saveProductBtn;
    @FXML
    private Button resetProductBtn;
    @FXML
    private Button deleteProductBtn;
    // FXML Reception table.
    @FXML
    private TableView<ProductReceptionBasicResponse> receptionTable;
    @FXML
    private TableColumn<ProductReceptionBasicResponse, String> rbReceptionTableColumn;
    @FXML
    private TableColumn<ProductReceptionBasicResponse, ProductBasicResponse> rawMaterialProductReceptionTableColumn;
    @FXML
    private TableColumn<ProductReceptionBasicResponse, String> productAmountTableColumn;
    // FXML ReportEntity table.
    @FXML
    private TableView<ReportProductResponse> reportTable;
    @FXML
    private TableColumn<ReportProductResponse, String> rbReportTableColumn;
    @FXML
    private TableColumn<ReportProductResponse, String> typeReportTableColumn;
    @FXML
    private TableColumn<ReportProductResponse, String> codeReportTableColumn;
    @FXML
    private TableColumn<ReportProductResponse, String> supplierCodeReportTableColumn;
    @FXML
    private TableColumn<ReportProductResponse, String> companyReportTableColumn;
    @FXML
    private TableColumn<ReportProductResponse, String> dateReportTableColumn;
    @FXML
    private TableColumn<ReportProductResponse, String> productAmountReportTableColumn;

    @FXML
    private HTMLEditor descriptionHtmlEditor;

    // Spring injections
    private final ProductService productService;
    private final UnitMeasureService unitMeasureService;

    // Controller state.
    ProductDetailsResponse product;
    private final Router router;

    public ProductEditController(
            Router router,
            UnitMeasureService unitMeasureService,
            ProductService productService
    ) {
        this.unitMeasureService = unitMeasureService;
        this.productService = productService;
        this.router = router;
    }

    public void initialize() {
        // Reception table config.
        receptionTable.setEditable(true);

        var receptionTableContextMenu = new ContextMenu();

        var addNewRowMenuItem = new MenuItem("Dodaj sirovinu");
        receptionTableContextMenu.getItems().add(addNewRowMenuItem);
        addNewRowMenuItem.setOnAction(event -> {
            var product = new ProductReceptionBasicResponse();

            product.setAmount(Float.valueOf("0.0"));
            product.setProduct(this.product.toBasicResponse());

            receptionTable.getItems().add(0, product);
        });

        var removeRowMenuItem = new MenuItem("Ukloni sirovinu");
        receptionTableContextMenu.getItems().add(removeRowMenuItem);
        removeRowMenuItem.setOnAction(event -> {
            ProductReceptionBasicResponse selectedItem = receptionTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                receptionTable.getItems().remove(selectedItem);
            }
        });

        receptionTable.setOnContextMenuRequested(event -> {
           if(receptionTable.getSelectionModel().getSelectedItem() != null) {
               removeRowMenuItem.setDisable(false);
           } else {
               removeRowMenuItem.setDisable(true);
           }
        });

        receptionTable.setOnMouseClicked(event -> {
           if(event.getButton() == MouseButton.SECONDARY) {
               receptionTableContextMenu.show(receptionTable, event.getScreenX(), event.getScreenY());
           }

           if(event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
               receptionTableContextMenu.hide();
           }
        });

        rbReceptionTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        receptionTable.getItems().indexOf(cellData.getValue()) + 1 + "."
                )
        );

        rawMaterialProductReceptionTableColumn.setCellValueFactory(cellData ->
           new SimpleObjectProperty<>(cellData.getValue().getRawMaterialProduct())
        );
        rawMaterialProductReceptionTableColumn.setEditable(true);
        rawMaterialProductReceptionTableColumn.setCellFactory(new Callback<TableColumn<ProductReceptionBasicResponse, ProductBasicResponse>, TableCell<ProductReceptionBasicResponse, ProductBasicResponse>>() {
            @Override
            public TableCell<ProductReceptionBasicResponse, ProductBasicResponse> call(
                    TableColumn<ProductReceptionBasicResponse,
                            ProductBasicResponse> param
            ) {
                return new TableCell<>() {
                    private SearchableComboBox<ProductBasicResponse> comboBox;

                    @Override
                    public void updateItem(ProductBasicResponse item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            var rawMaterials = FXCollections.observableArrayList(
                                    productService.getAll(ProductSpecification.isRawMaterial())
                            );
                            if (comboBox == null) {
                                comboBox = new SearchableComboBox<>();
                                comboBox.setItems(rawMaterials); // Populate the combo box
                                comboBox.setEditable(true);
                            }

                            comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                                if (getTableRow() != null && getTableRow().getItem() != null) {
                                    ProductReceptionBasicResponse rowItem = getTableRow().getItem();
                                    // Update the product when the ComboBox value changes
                                    rowItem.setRawMaterialProduct(newValue);  // Update the value in your model
                                }
                            });

                            comboBox.setValue(item);
                            setGraphic(comboBox);
                        }
                    }
                };
            }
        });

        productAmountTableColumn.setCellValueFactory(cellData -> {
                if(cellData.getValue().getRawMaterialProduct() == null) {
                    return new SimpleStringProperty("0");
                }

                return new SimpleStringProperty(
                        String.format(
                                cellData.getValue().getRawMaterialProduct().getUnitMeasure().getIsInteger()
                                        ? "%.0f"
                                        : "%.2f",
                                cellData.getValue().getAmount()
                        )
                );
            }
        );
        productAmountTableColumn.setEditable(true);
        productAmountTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productAmountTableColumn.setOnEditCommit(event -> {
            // Get the new value entered by the user
            String newValue = event.getNewValue();
            ProductReceptionBasicResponse editedItem = event.getRowValue();

            try {
                double amount = Double.parseDouble(newValue);
                editedItem.setAmount((float) amount); // Update the amount of the corresponding object

                event.getTableView().refresh();
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + newValue);
            }
        });

        rbReportTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        reportTable.getItems().indexOf(cellData.getValue()) + 1 + "."
                )
        );

        typeReportTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getReport().getType().toString())
        );

        codeReportTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getReport().getCode())
        );

        supplierCodeReportTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty("-")
        );

        companyReportTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty("-")
        );

        dateReportTableColumn.setCellValueFactory(cellData -> {
            String dateString = cellData.getValue().getReport().getDate(); // Assuming getDate() returns a String
            if (dateString != null && !dateString.isEmpty()) {
                LocalDate localDate = LocalDate.parse(dateString);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMM yyyy");
                String formattedDate = localDate.format(formatter);
                return new SimpleStringProperty(formattedDate);
            } else {
                return new SimpleStringProperty("");
            }
        });

        productAmountReportTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty("-")
        );

        saveProductBtn.setOnAction(event -> save());
        resetProductBtn.setOnAction(event -> reset());
        deleteProductBtn.setOnAction(event -> delete());
    }

    public void load(Long productId) {
        List<ProductCategory> categories = this.productService.getAllCategoryNames();
        categoryCombo.getItems().removeAll();
        categoryCombo.getItems().addAll(categories);

        List<UnitMeasureResponse> unitMeasures = this.unitMeasureService.getAll();
        unitMeasureCombo.getItems().removeAll();
        unitMeasureCombo.getItems().addAll(unitMeasures);

        if (productId != null) {
            product = this.productService.getById(productId);
            this.titleLabel.setText(product.getName());
            this.descriptionHtmlEditor.setHtmlText(product.getDescriptionHtml());
            this.nameInput.setText(product.getName());
            this.codeInput.setText(product.getCode());
            this.categoryCombo.setValue(product.getCategory());
            this.tagInput.setText(String.join(", ", product.getTags()));

            this.inStockAmountInput.setText(
                    product.getUnitMeasure().getIsInteger()
                            ? String.format("%.0f", product.getInStockAmount())
                            : String.format("%.2f", product.getInStockAmount())
            );

            this.inStockWarningAmountInput.setText(
                    product.getUnitMeasure().getIsInteger()
                            ? String.format("%.0f", product.getInStockWarningAmount())
                            : String.format("%.2f", product.getInStockWarningAmount())
            );

            this.unitMeasureCombo.getSelectionModel().select(product.getUnitMeasure());

            var receptions = FXCollections.observableArrayList(product.getReceptions());
            this.receptionTable.setItems(receptions);

            var reports = FXCollections.observableArrayList(product.getReports());
            this.reportTable.setItems(reports);
        } else {
            this.titleLabel.setText("Kreiraj proizvod");
        }
    }

    private void save() {
        if(product == null) {
            var request = new ProductCreateRequest();
            request.setName(nameInput.getText());
            request.setCode(codeInput.getText());
            request.setCategory(categoryCombo.getValue());
            request.setDescriptionHtml(descriptionHtmlEditor.getHtmlText());
            request.setJmId(
                    unitMeasureCombo.getSelectionModel().getSelectedItem().getId()
            );
            request.setInStockWarningAmount(Float.valueOf(inStockWarningAmountInput.getText()));
            request.setInStockAmount(Float.valueOf(inStockAmountInput.getText()));

            this.productService.create(request);
        } else {
            var request = new ProductUpdateRequest();

            request.setName(nameInput.getText());
            request.setCode(codeInput.getText());
            request.setCategory(categoryCombo.getValue());
            request.setDescriptionHtml(descriptionHtmlEditor.getHtmlText());
            request.setJmId(
                    unitMeasureCombo.getSelectionModel().getSelectedItem().getId()
            );
            request.setInStockWarningAmount(Float.valueOf(inStockWarningAmountInput.getText()));
            request.setInStockAmount(Float.valueOf(inStockAmountInput.getText()));

            this.productService.update(product.getId(), request);

            var receptionRequests = this.receptionTable
                    .getItems()
                    .stream()
                    .map(reception -> {
                        var req = new ProductReceptionUpdateRequest();

                        req.setReceptionId(reception.getId());
                        req.setProductId(reception.getProduct().getId());
                        req.setRawMaterialId(reception.getRawMaterialProduct().getId());
                        req.setAmount(reception.getAmount());

                        return req;
                    }).toList();

            this.productService.updateReceptions(product.getId(), receptionRequests);
        }

        this.load(product.getId());
        this.receptionTable.refresh();
    }

    private void reset() {
        this.load(product.getId());
    }

    private void delete() {
        this.productService.delete(product.getId());
        this.router.navigateTo(ProductIndexController.class);
    }
}