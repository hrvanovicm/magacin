package fyi.hrvanovicm.magacin.presentation.javafx.controllers;

import fyi.hrvanovicm.magacin.presentation.javafx.factory.CellFactory;
import fyi.hrvanovicm.magacin.presentation.javafx.factory.CellValueFactory;
import fyi.hrvanovicm.magacin.application.product.dto.*;
import fyi.hrvanovicm.magacin.application.product.commands.CreateProductHandler;
import fyi.hrvanovicm.magacin.application.product.commands.DeleteProductHandler;
import fyi.hrvanovicm.magacin.application.product.commands.UpdateProductHandler;
import fyi.hrvanovicm.magacin.application.product.queries.ProductQueryService;
import fyi.hrvanovicm.magacin.application.product.requests.ProductEditRequest;
import fyi.hrvanovicm.magacin.application.product.requests.ProductSearchParamsDTO;
import fyi.hrvanovicm.magacin.application.product.requests.ReceptionEditRequestDTO;
import fyi.hrvanovicm.magacin.application.product.dto.ProductReportDTO;
import fyi.hrvanovicm.magacin.application.unit_measure.queries.UnitMeasureQueryService;
import fyi.hrvanovicm.magacin.presentation.javafx.factory.DialogFactory;
import fyi.hrvanovicm.magacin.domain.products.*;
import fyi.hrvanovicm.magacin.application.product.dto.ProductReceptionDTO;
import fyi.hrvanovicm.magacin.application.unit_measure.dto.UnitMeasureDTO;
import fyi.hrvanovicm.magacin.presentation.javafx.app.Router;
import fyi.hrvanovicm.magacin.infrastructure.notification.NotificationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.web.HTMLEditor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FxmlView("/views/product-edit.fxml")
public class ProductEditController implements AutoLoadController {
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
    private ComboBox<UnitMeasureDTO> unitMeasureCombo;
    @FXML
    private TextField inStockWarningAmountInput;
    @FXML
    private Button saveProductBtn;
    @FXML
    private Button resetProductBtn;
    @FXML
    private Button deleteProductBtn;
    @FXML
    private TableView<ProductReceptionDTO> receptionTable;
    @FXML
    private TableColumn<ProductReceptionDTO, String> rbReceptionTableColumn;
    @FXML
    private TableColumn<ProductReceptionDTO, ProductDTO> rawMaterialProductReceptionTableColumn;
    @FXML
    private TableColumn<ProductReceptionDTO, String> productAmountTableColumn;
    @FXML
    private TableColumn<ProductReceptionDTO, String> productUnitMeasureTableColumn;
    @FXML
    private TableView<ProductReportDTO> reportTable;
    @FXML
    private TableColumn<ProductReportDTO, String> rbReportTableColumn;
    @FXML
    private TableColumn<ProductReportDTO, String> typeReportTableColumn;
    @FXML
    private TableColumn<ProductReportDTO, String> codeReportTableColumn;
    @FXML
    private TableColumn<ProductReportDTO, String> supplierCodeReportTableColumn;
    @FXML
    private TableColumn<ProductReportDTO, String> companyReportTableColumn;
    @FXML
    private TableColumn<ProductReportDTO, String> dateReportTableColumn;
    @FXML
    private TableColumn<ProductReportDTO, String> productAmountReportTableColumn;
    @FXML
    private HTMLEditor descriptionHtmlEditor;
    private List<ProductDTO> rawMaterialProducts = List.of();

    private final CreateProductHandler createProductHandler;
    private final UpdateProductHandler updateProductHandler;
    private final DeleteProductHandler deleteProductHandler;
    private final ProductQueryService productQueryService;
    private final UnitMeasureQueryService unitMeasureQueryService;

    private final DialogFactory dialogService;
    private final Router router;

    ProductDetailsDTO product;
    NotificationService notificationService;

    public ProductEditController(
            CreateProductHandler createProductHandler,
            UpdateProductHandler updateProductHandler,
            DeleteProductHandler deleteProductHandler,
            ProductQueryService productQueryService,
            UnitMeasureQueryService unitMeasureQueryService,
            DialogFactory dialogService,
            Router router,
            NotificationService notificationService
    ) {
        this.createProductHandler = createProductHandler;
        this.updateProductHandler = updateProductHandler;
        this.deleteProductHandler = deleteProductHandler;
        this.productQueryService = productQueryService;
        this.unitMeasureQueryService = unitMeasureQueryService;
        this.dialogService = dialogService;
        this.router = router;
        this.notificationService = notificationService;
    }

    public void initialize() {
        this.rawMaterialProducts = this.productQueryService.getAll(
                ProductSearchParamsDTO.builder()
                        .isRawMaterial(true)
                        .build()
        );

        // Reception table config.
        receptionTable.setEditable(true);

        // Reception table context menu.
        var receptionTableContextMenu = new ContextMenu();
        var addNewRowMenuItem = new MenuItem("Dodaj sirovinu");
        receptionTableContextMenu.getItems().add(addNewRowMenuItem);
        addNewRowMenuItem.setOnAction(event -> {
            var product = new ProductReceptionDTO();

            product.setAmount(Float.valueOf("0.0"));
           // product.setProduct(this.product.toBasicResponse());

            receptionTable.getItems().add(0, product);
        });

        var removeRowMenuItem = new MenuItem("Ukloni sirovinu");
        receptionTableContextMenu.getItems().add(removeRowMenuItem);
        removeRowMenuItem.setOnAction(event -> {
            ProductReceptionDTO selectedItem = receptionTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                receptionTable.getItems().remove(selectedItem);
            }
        });

        receptionTable.setOnContextMenuRequested(
                event -> removeRowMenuItem.setDisable(receptionTable.getSelectionModel().getSelectedItem() == null)
        );

        receptionTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                receptionTableContextMenu.show(receptionTable, event.getScreenX(), event.getScreenY());
            }

            if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                receptionTableContextMenu.hide();
            }
        });

        // Reception table cell value factory.
        rbReceptionTableColumn.setCellValueFactory(CellValueFactory.sequenceNumber(receptionTable));
        rawMaterialProductReceptionTableColumn.setCellValueFactory(
                CellValueFactory.product(ProductReceptionDTO::getRawMaterialProduct)
        );
        productAmountTableColumn.setCellValueFactory(CellValueFactory.amount(
                ProductReceptionDTO::getRawMaterialProduct,
                ProductReceptionDTO::getAmount,
                false
        ));
        productUnitMeasureTableColumn.setCellValueFactory(
                CellValueFactory.unitMeasure(
                        ProductReceptionDTO::getRawMaterialProduct
                )
        );

        // Reception table cell factory.
        productAmountTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        rawMaterialProductReceptionTableColumn.setCellFactory(
                CellFactory.productSearchableCombo(
                        () -> rawMaterialProducts,
                        ProductReceptionDTO::setRawMaterialProduct
                )
        );

        // Reception table cell config.
        rawMaterialProductReceptionTableColumn.setEditable(true);
        productAmountTableColumn.setEditable(true);
        productAmountTableColumn.setOnEditCommit(CellFactory.amountTextField(ProductReceptionDTO::setAmount));

        // Report table config.
        this.reportTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ProductReportDTO report = reportTable.getSelectionModel().getSelectedItem();
                if (report != null) {
                    this.router.navigateTo(
                            ReportEditController.class,
                            controller -> controller.load(report.getReport().getId(), report.getReport().getType())
                    );
                }
            }
        });

        // Report table cell value factory.
        rbReportTableColumn.setCellValueFactory(CellValueFactory.sequenceNumber(reportTable));
        typeReportTableColumn.setCellValueFactory(CellValueFactory.reportType(ProductReportDTO::getReport));
        codeReportTableColumn.setCellValueFactory(CellValueFactory.reportCode(ProductReportDTO::getReport));
        supplierCodeReportTableColumn.setCellValueFactory(CellValueFactory.reportSupplierCode(ProductReportDTO::getReport));
        companyReportTableColumn.setCellValueFactory(CellValueFactory.reportCompanyName(ProductReportDTO::getReport));
        dateReportTableColumn.setCellValueFactory(CellValueFactory.reportDate(ProductReportDTO::getReport));
        productAmountReportTableColumn.setCellValueFactory(CellValueFactory.amount(
                (a) -> product,
                ProductReportDTO::getAmount,
                true
        ));

        categoryCombo.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != ProductCategory.PRODUCT) {
                       this.receptionTable.getItems().clear();
                       this.receptionTable.setDisable(true);
                    } else {
                        this.receptionTable.setDisable(false);
                    }
                });

        // Action buttons.
        saveProductBtn.setOnAction(event -> save());
        resetProductBtn.setOnAction(event -> reset());
        deleteProductBtn.setOnAction(event -> delete());
    }

    public void load() {
        this.load(null);
    }

    public void load(Long productId) {
        List<ProductCategory> categories = this.productQueryService.getAllCategoryNames();
        categoryCombo.getItems().removeAll();
        categoryCombo.getItems().addAll(categories);

        List<UnitMeasureDTO> unitMeasures = this.unitMeasureQueryService.getAll();
        unitMeasureCombo.getItems().removeAll();
        unitMeasureCombo.getItems().addAll(unitMeasures);

        if (productId != null) {
            product = this.productQueryService.getById(productId);

            this.titleLabel.setText(product.getName());

            this.nameInput.setText(product.getName());
            this.codeInput.setText(product.getCode());
            this.categoryCombo.setValue(product.getCategory());
            this.tagInput.setText(String.join(", ", product.getTags()));
            this.inStockAmountInput.setText(product.getUnitMeasure().getIsInteger() ? String.format("%.0f", product.getInStockAmount()) : String.format("%.2f", product.getInStockAmount()));
            this.inStockWarningAmountInput.setText(product.getUnitMeasure().getIsInteger() ? String.format("%.0f", product.getInStockWarningAmount()) : String.format("%.2f", product.getInStockWarningAmount()));
            this.unitMeasureCombo.getSelectionModel().select(product.getUnitMeasure());

            this.descriptionHtmlEditor.setHtmlText(product.getDescriptionHtml());

            var receptions = FXCollections.observableArrayList(product.getReceptions());
            this.receptionTable.setItems(receptions);

            var reports = FXCollections.observableArrayList(product.getReports());
            this.reportTable.setItems(reports);

            this.categoryCombo.setDisable(true);
            if(product.getCategory() != ProductCategory.PRODUCT) {
                this.receptionTable.setDisable(true);
            }
        } else {
            this.titleLabel.setText("Novi artikal");
        }

        this.receptionTable.refresh();
    }

    private void save() {
        var request = new ProductEditRequest();
        request.setName(nameInput.getText());
        request.setCode(codeInput.getText());
        request.setCategory(categoryCombo.getValue());
        request.setDescriptionHtml(descriptionHtmlEditor.getHtmlText());
        request.setJmId(unitMeasureCombo.getSelectionModel().getSelectedItem().getId());
        request.setInStockWarningAmount(Float.valueOf(inStockWarningAmountInput.getText()));
        request.setInStockAmount(Float.valueOf(inStockAmountInput.getText()));
        request.setReceptions(
                this.receptionTable.getItems()
                        .stream()
                        .filter((reception) -> reception.getRawMaterialProduct() != null)
                        .map(ReceptionEditRequestDTO::fromDTO)
                        .toList()
        );

        this.product = (product == null)
                ? this.createProductHandler.handle(request)
                : this.updateProductHandler.handle(product.getId(), request);

        this.load(product.getId());

        this.notificationService.notifyUser("Uspješno sačuvan artikal!");
    }

    private void reset() {
        if(product == null) {
            this.load();
            return;
        }

        this.load(product.getId());
    }

    private void delete() {
        var isUserConfirmed = this.dialogService.showConfirmDialog("Da li ste sigurni da želite izbrisati artikal!");

        if(isUserConfirmed) {
            this.deleteProductHandler.handle(product.getId());
            this.router.navigateTo(ProductIndexController.class);
        }
    }
}