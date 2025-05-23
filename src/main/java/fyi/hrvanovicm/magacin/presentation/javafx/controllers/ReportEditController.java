package fyi.hrvanovicm.magacin.presentation.javafx.controllers;

import fyi.hrvanovicm.magacin.application.product.queries.ProductQueryService;
import fyi.hrvanovicm.magacin.application.report.commands.CreateReportHandler;
import fyi.hrvanovicm.magacin.application.report.commands.DeleteReportHandler;
import fyi.hrvanovicm.magacin.application.report.commands.ReportExportHandler;
import fyi.hrvanovicm.magacin.application.report.commands.UpdateReportHandler;
import fyi.hrvanovicm.magacin.application.report.queries.ReportQueryService;
import fyi.hrvanovicm.magacin.application.report.requests.ReportEditRequest;
import fyi.hrvanovicm.magacin.infrastructure.notification.NotificationService;
import fyi.hrvanovicm.magacin.presentation.javafx.factory.CellFactory;
import fyi.hrvanovicm.magacin.presentation.javafx.factory.CellValueFactory;
import fyi.hrvanovicm.magacin.application.product.dto.ProductDTO;
import fyi.hrvanovicm.magacin.application.product.dto.ProductReceptionDTO;
import fyi.hrvanovicm.magacin.application.report.dto.ReportDetailsDTO;
import fyi.hrvanovicm.magacin.application.report.requests.ReportProductRequest;
import fyi.hrvanovicm.magacin.application.report.dto.ReportProductDTO;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import fyi.hrvanovicm.magacin.application.report.requests.ReceiptReportEditRequest;
import fyi.hrvanovicm.magacin.application.report.requests.ShipmentReportEditRequest;
import fyi.hrvanovicm.magacin.presentation.javafx.factory.DialogFactory;
import fyi.hrvanovicm.magacin.presentation.javafx.app.Router;
import fyi.hrvanovicm.magacin.shared.exceptions.ValidationFailedException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.HTMLEditor;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@FxmlView("/views/report-edit.fxml")
public class ReportEditController {
    // Controller state.
    ReportDetailsDTO report;
    ReportType reportType;

    private List<ProductDTO> products;

    @FXML
    private TabPane tabPane;

    // FXML ReportEntity input.
    @FXML
    private Label titleLabel;
    @FXML
    private TextField codeInput;

    @FXML
    private FlowPane supplierReportCodeGroup;
    @FXML
    private TextField supplierReportCodeInput;

    @FXML
    private FlowPane supplierCompanyGroup;
    @FXML
    private TextField supplierCompanyNameInput;
    @FXML
    private CheckBox supplierLocalCheckbox;

    @FXML
    private FlowPane receiptCompanyGroup;
    @FXML
    private TextField receiptCompanyNameInput;

    @FXML
    private DatePicker signedOnDatePicker;
    @FXML
    private TextField signedByNameInput;
    @FXML
    private TextField signedOnPlaceInput;
    @FXML
    private HTMLEditor descriptionHtmlEditor;
    @FXML
    private Button saveBtn;
    @FXML
    private Button resetChangesBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button pdfExportBtn;

    // FXML Reception table.
    @FXML
    private TableView<ReportProductDTO> productTable;
    @FXML
    private TableColumn<ReportProductDTO, String> rbProductTableColumn;
    @FXML
    private TableColumn<ReportProductDTO, ProductDTO> productNameTableColumn;
    @FXML
    private TableColumn<ReportProductDTO, String> productAmountTableColumn;
    @FXML
    private TableColumn<ReportProductDTO, String> productUnitMeasureTableColumn;


    // FXML Raw material for product table.
    @FXML
    private TableView<ProductReceptionDTO> rawMaterialsTable;
    @FXML
    private TableColumn<ProductReceptionDTO, String> rbRawMaterialTableColumn;
    @FXML
    private TableColumn<ProductReceptionDTO, ProductDTO> rawMaterialTableColumn;
    @FXML
    private TableColumn<ProductReceptionDTO, String> rawMaterialAmountTableColumn;
    @FXML
    private TableColumn<ProductReceptionDTO, String> rawMaterialUnitMeasureTableColumn;

    // Spring injections.
    private final ProductQueryService productQueryService;

    private final CreateReportHandler createReportHandler;
    private final UpdateReportHandler updateReportHandler;
    private final DeleteReportHandler deleteReportHandler;
    private final ReportExportHandler reportExportHandler;

    private final DialogFactory dialogService;
    private final Router router;
    private final NotificationService notificationService;

    private final ReportQueryService reportQueryService;

    public ReportEditController(
            ProductQueryService productQueryService,
            CreateReportHandler createReportHandler,
            UpdateReportHandler updateReportHandler,
            DeleteReportHandler deleteReportHandler,
            DialogFactory dialogService,
            Router router,
            NotificationService notificationService,
            ReportQueryService reportQueryService,
            ReportExportHandler reportExportHandler
    ) {
        this.productQueryService = productQueryService;
        this.createReportHandler = createReportHandler;
        this.updateReportHandler = updateReportHandler;
        this.deleteReportHandler = deleteReportHandler;
        this.dialogService = dialogService;
        this.router = router;
        this.notificationService = notificationService;
        this.reportQueryService = reportQueryService;
        this.reportExportHandler = reportExportHandler;
    }

    public void initialize() {
        productTable.setEditable(true);

        this.tabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldTab, newTab) -> {
                    // Raw materials for products is only shown when is product table in focus.
                    if(Objects.equals(newTab.getId(), "description")) {
                        rawMaterialsTable.setVisible(false);
                        rawMaterialsTable.setManaged(false);
                    }
                });

        this.pdfExportBtn.setOnAction(event -> this.reportExportHandler.handle(report.getId()));

        var productTableContextMenu = new ContextMenu();

        var addNewRowMenuItem = new MenuItem("Dodaj proizvod");
        productTableContextMenu.getItems().add(addNewRowMenuItem);
        addNewRowMenuItem.setOnAction(event -> {
            var product = new ReportProductDTO();
            product.setAmount(Float.valueOf("0.0"));
            productTable.getItems().add(product);
        });

        var removeRowMenuItem = new MenuItem("Ukloni proizvod");
        productTableContextMenu.getItems().add(removeRowMenuItem);
        removeRowMenuItem.setOnAction(event -> {
            ReportProductDTO selectedItem = productTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                productTable.getItems().remove(selectedItem);
            }
        });

        this.supplierLocalCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
           if(newValue) {
               this.supplierCompanyNameInput.clear();
               this.supplierCompanyNameInput.setDisable(true);

               var products = productTable.getItems().stream().map(product -> {
                   if(product.getReceptions().isEmpty()) {
                       product.setReceptions(
                               product.getProduct().getReceptions()
                                       .stream()
                                       .map(val -> {
                                           var rec =  new ProductReceptionDTO();
                                           rec.setRawMaterialProduct(val.getRawMaterialProduct());
                                           rec.setAmount(val.getAmount() * product.getAmount());
                                           return rec;
                                       }).toList()
                       );
                   }

                   return product;
               });
               productTable.setItems(FXCollections.observableArrayList(products.collect(Collectors.toList())));
               productTable.refresh();

           } else {
               this.supplierCompanyNameInput.clear();
               this.supplierCompanyNameInput.setDisable(false);
           }

           rawMaterialsTable.setVisible(false);
           rawMaterialsTable.setManaged(false);
        });

        rawMaterialsTable.setVisible(false);
        rawMaterialsTable.setManaged(false);

        productTable.setOnContextMenuRequested(event -> {
            if(productTable.getSelectionModel().getSelectedItem() != null) {
                removeRowMenuItem.setDisable(false);
            } else {
                removeRowMenuItem.setDisable(true);
            }
        });

        productTable.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                productTableContextMenu.show(productTable, event.getScreenX(), event.getScreenY());
            }

            if(event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                productTableContextMenu.hide();
            }
        });

        productTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(observable.getValue() == null || observable.getValue().getReceptions() == null) {
                return;
            }

            if(reportType == ReportType.RECEIPT && supplierLocalCheckbox.isSelected()) {
                rawMaterialsTable.setVisible(true);
                rawMaterialsTable.setManaged(true);
            }

            var receptions = FXCollections.observableArrayList(observable.getValue().getReceptions());
            rawMaterialsTable.getItems().clear();
            rawMaterialsTable.setItems(receptions);
        });

        rbProductTableColumn.setCellValueFactory(CellValueFactory.sequenceNumber(productTable));

        productNameTableColumn.setCellValueFactory(CellValueFactory.product(ReportProductDTO::getProduct));
        productNameTableColumn.setEditable(true);
        productNameTableColumn.setCellFactory(
                CellFactory.productSearchableCombo(
                    () -> products,
                        (a, b) -> {
                            a.setProduct(b);
                            a.setReceptions(
                                    b.getReceptions()
                                            .stream()
                                            .map(val -> {
                                                var rec =  new ProductReceptionDTO();
                                                rec.setRawMaterialProduct(val.getRawMaterialProduct());
                                                rec.setAmount(val.getAmount() * a.getAmount());
                                                return rec;
                                            }).toList()
                            );
                        }
                )
        );


        productAmountTableColumn.setCellValueFactory(CellValueFactory.amount(
                ReportProductDTO::getProduct,
                ReportProductDTO::getAmount,
                false
        ));
        productUnitMeasureTableColumn.setCellValueFactory(CellValueFactory.unitMeasure(
                ReportProductDTO::getProduct
        ));

        productAmountTableColumn.setEditable(true);
        productAmountTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productAmountTableColumn.setOnEditCommit(
                CellFactory.amountTextField(
                        (reportProduct, updatedAmount) -> {
                            var items = reportProduct.getReceptions().stream().peek(
                                    rawMaterial -> {
                                        var currentAmount = rawMaterial.getAmount();

                                        var expectedAmount = updatedAmount * reportProduct
                                                .getProduct()
                                                .getReceptions()
                                                .stream()
                                                .filter(reception -> Objects.equals(reception.getRawMaterialProduct().getId(), rawMaterial.getRawMaterialProduct().getId()))
                                                .map(ProductReceptionDTO::getAmount)
                                                .findFirst()
                                                .get();

                                        if(currentAmount == expectedAmount) {
                                            rawMaterial.setAmount(expectedAmount);
                                        } else {
                                            rawMaterial.setAmount(expectedAmount);
                                        }
                                    }
                            ).collect(Collectors.toList());

                            reportProduct.setAmount(updatedAmount);

                            reportProduct.setReceptions(items);
                            rawMaterialsTable.setItems(FXCollections.observableArrayList(items));
                            rawMaterialsTable.refresh();
                    }
                ));


        rbRawMaterialTableColumn.setCellValueFactory(CellValueFactory.sequenceNumber(rawMaterialsTable));
        rawMaterialTableColumn.setCellValueFactory(CellValueFactory.product(
                ProductReceptionDTO::getRawMaterialProduct
        ));
        rawMaterialTableColumn.setEditable(true);
        rawMaterialTableColumn.setCellFactory(
                CellFactory.productSearchableCombo(
                        () -> products,
                        ProductReceptionDTO::setRawMaterialProduct
                )
        );


        rawMaterialAmountTableColumn.setCellValueFactory(CellValueFactory.amount(
                ProductReceptionDTO::getRawMaterialProduct,
                ProductReceptionDTO::getAmount,
                false
        ));
        rawMaterialUnitMeasureTableColumn.setCellValueFactory(CellValueFactory.unitMeasure(
                ProductReceptionDTO::getRawMaterialProduct
        ));

        rawMaterialAmountTableColumn.setEditable(true);
        rawMaterialAmountTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        rawMaterialAmountTableColumn.setOnEditCommit(
                CellFactory.amountTextField(
                    ProductReceptionDTO::setAmount
                ));

        this.saveBtn.setOnAction(event -> save());
        this.resetChangesBtn.setOnAction(event -> load(report.getId(), reportType));
        this.deleteBtn.setOnAction(event -> delete());
    }

    public void load(ReportType reportType) {
        this.load(null, reportType);
    }

    public void load(Long reportId, ReportType reportType) {
        this.products = this.productQueryService.getAll();
        this.reportType = reportType;

        List<String> companyNames = this.reportQueryService.getAllCompanyNames();
        TextFields.bindAutoCompletion(supplierCompanyNameInput, companyNames);

        List<String> userNames = this.reportQueryService.getAllSignedUserNames();
        TextFields.bindAutoCompletion(signedByNameInput, userNames);

        if(reportType == ReportType.RECEIPT) {
            this.supplierReportCodeGroup.setManaged(true);
            this.supplierReportCodeGroup.setVisible(true);

            this.supplierCompanyGroup.setVisible(true);
            this.supplierCompanyGroup.setManaged(true);

            this.receiptCompanyGroup.setVisible(false);
            this.receiptCompanyGroup.setManaged(false);

        } else if (reportType == ReportType.SHIPMENT) {
            this.supplierReportCodeGroup.setVisible(false);
            this.supplierReportCodeGroup.setManaged(false);

            this.supplierCompanyGroup.setVisible(false);
            this.supplierCompanyGroup.setManaged(false);

            this.receiptCompanyGroup.setVisible(true);
        }

        if (reportId != null) {
            var report = this.reportQueryService.getById(reportId);

            this.titleLabel.setText(
                    String.format("%s %s", report.getType().toString(), report.getCode())
            );

            if(reportType == ReportType.RECEIPT) {
                this.supplierReportCodeInput.setText(
                        report.getReceipt().getSupplierReportCode()
                );
                this.supplierCompanyNameInput.setText(
                        report.getReceipt().getSupplierCompanyName()
                );
                this.supplierLocalCheckbox.setSelected(
                        report.getReceipt().getIsSupplierProduction()
                );
            } else if (reportType == ReportType.SHIPMENT) {
                this.receiptCompanyNameInput.setText(
                        report.getShipment().getReceiptCompanyName()
                );
            }

            if (report.getDate() != null && !report.getDate().isEmpty()) {
                this.signedOnDatePicker.setValue(LocalDate.parse(report.getDate()));
            }

            signedByNameInput.setText(report.getSignedByName());
            signedOnPlaceInput.setText(report.getPlaceOfPublish());
            descriptionHtmlEditor.setHtmlText(report.getDescriptionHtml());
            codeInput.setText(report.getCode());

            var reportProducts = FXCollections.observableArrayList(
                    report.getProducts()
            );
            productTable.setItems(reportProducts);

            this.report = report;
        } else {
            this.report = null;
            if(reportType == ReportType.RECEIPT) {
                this.titleLabel.setText("Nova primka");
            } else if(reportType == ReportType.SHIPMENT) {
                this.titleLabel.setText("Nova otpremnica");
            }
        }
    }

    private void save() {
        ReportEditRequest request;

        try {
            if(reportType == ReportType.RECEIPT) {
                request = new ReceiptReportEditRequest();

                ((ReceiptReportEditRequest) request).setSupplierReportCode(this.supplierReportCodeInput.getText());
                ((ReceiptReportEditRequest) request).setSupplierCompanyName(this.supplierCompanyNameInput.getText());
                ((ReceiptReportEditRequest) request).setIsSupplierProduction(this.supplierLocalCheckbox.isSelected());
            } else if (reportType == ReportType.SHIPMENT) {
                request = new ShipmentReportEditRequest();
                ((ShipmentReportEditRequest) request).setReceiptCompanyName(this.receiptCompanyNameInput.getText());
            } else {
                throw new RuntimeException("Unsupported report type: " + reportType);
            }

            request.setDate(this.signedOnDatePicker.getValue());
            request.setSignedByName(this.signedByNameInput.getText());
            request.setCode(this.codeInput.getText());
            request.setDescriptionHtml(this.descriptionHtmlEditor.getHtmlText());
            request.setPlaceOfPublish(this.signedOnPlaceInput.getText());
            request.setProducts(
                    productTable.getItems()
                            .stream()
                            .filter(item -> item.getProduct() != null)
                            .map(ReportProductRequest::from)
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            dialogService.showErrorDialog("Sva polja nisu validna!");
            e.printStackTrace();
            return;
        }

        try {
            this.report = this.report != null
                    ? this.updateReportHandler.handle(report.getId(), request)
                    : this.createReportHandler.handle(request);

            this.notificationService.notifyUser("Uspješno sačuvan izvještaj!");
            this.load(this.report.getId(), this.reportType);
            this.productTable.refresh();
        } catch (Exception e) {
            dialogService.showErrorDialog(e.getMessage());
            e.printStackTrace();
        }
    }

    private void delete() {
        var isUserConfirmed = this.dialogService.showConfirmDialog("Da li ste sigurni da želite izbrisati izvještaj!");

        if(isUserConfirmed) {
            try {
                this.deleteReportHandler.handle(report.getId());
                this.router.navigateTo(ReportIndexController.class);
            } catch (Exception e) {
                this.dialogService.showConfirmDialog("Da li ste sigurni da želite izbrisati izvještaj!");
            }
        }
    }
}