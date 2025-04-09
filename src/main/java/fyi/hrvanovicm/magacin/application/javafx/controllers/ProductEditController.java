package fyi.hrvanovicm.magacin.application.javafx.controllers;

import fyi.hrvanovicm.magacin.application.javafx.components.CellFactory;
import fyi.hrvanovicm.magacin.application.javafx.components.CellValueFactory;
import fyi.hrvanovicm.magacin.domain.products.*;
import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionBasicResponse;
import fyi.hrvanovicm.magacin.domain.products.reception.ProductReceptionUpdateRequest;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductResponse;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureResponse;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import fyi.hrvanovicm.magacin.infrastructure.javafx.Router;
import fyi.hrvanovicm.magacin.infrastructure.notification.NotificationService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.web.HTMLEditor;
import net.rgielen.fxweaver.core.FxmlView;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@FxmlView("/views/product-edit.fxml")
public class ProductEditController {
    // FXML CellValueFactory input.
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

    private List<ProductBasicResponse> rawMaterialProducts = List.of();

    // Controller state.
    ProductDetailsResponse product;
    private final Router router;

    @Autowired
    NotificationService notificationService;

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
        this.rawMaterialProducts = this.productService.getAll(ProductSpecification.isRawMaterial());

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
        rawMaterialProductReceptionTableColumn.setCellFactory(
                CellFactory.productSearchableCombo(() -> rawMaterialProducts, ProductReceptionBasicResponse::setRawMaterialProduct)
        );

        productAmountTableColumn.setCellValueFactory(CellValueFactory.receptionAmount(
                ProductReceptionBasicResponse::getProduct
        ));
        productAmountTableColumn.setEditable(true);
        productAmountTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productAmountTableColumn.setOnEditCommit(CellFactory.amountTextField(ProductReceptionBasicResponse::setAmount));

        // Report table.
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

        dateReportTableColumn.setCellValueFactory(
                CellValueFactory.reportDate(
                        (report) -> report.getReport().getDate()
                )
        );

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
                    .filter((reception) -> reception.getRawMaterialProduct() != null)
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

        this.notificationService.notifyUser("Uspješno sačuvan proizvod!");
    }

    private void reset() {
        this.load(product.getId());
    }

    private void delete() {
        this.productService.delete(product.getId());
        this.router.navigateTo(ProductIndexController.class);
    }
}