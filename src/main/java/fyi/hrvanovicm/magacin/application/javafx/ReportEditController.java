package fyi.hrvanovicm.magacin.application.javafx;

import fyi.hrvanovicm.magacin.domain.products.ProductBasicResponse;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.products.ProductSpecification;
import fyi.hrvanovicm.magacin.domain.report.ReportDetailsResponse;
import fyi.hrvanovicm.magacin.domain.report.ReportProductResponse;
import fyi.hrvanovicm.magacin.domain.report.ReportService;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportCreateRequest;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportUpdateRequest;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportCreateRequest;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportUpdateRequest;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.HTMLEditor;
import javafx.util.Callback;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@FxmlView("/views/report-edit.fxml")
public class ReportEditController {
    // Controller state.
    ReportDetailsResponse report;
    ReportType reportType;

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

    // FXML Reception table.
    @FXML
    private TableView<ReportProductResponse> productTable;
    @FXML
    private TableColumn<ReportProductResponse, String> rbProductTableColumn;
    @FXML
    private TableColumn<ReportProductResponse, ProductBasicResponse> productNameTableColumn;
    @FXML
    private TableColumn<ReportProductResponse, String> productAmountTableColumn;

    // Spring injections.
    private ProductService productService;
    private UnitMeasureService unitMeasureService;

    @Autowired
    private ReportService reportService;

    public ReportEditController(UnitMeasureService unitMeasureService, ProductService productService) {
        this.unitMeasureService = unitMeasureService;
        this.productService = productService;
    }

    public void initialize() {
        productTable.setEditable(true);

        var productTableContextMenu = new ContextMenu();

        var addNewRowMenuItem = new MenuItem("Dodaj proizvod");
        productTableContextMenu.getItems().add(addNewRowMenuItem);
        addNewRowMenuItem.setOnAction(event -> {
            var product = new ReportProductResponse();

            product.setAmount(Float.valueOf("0.0"));

            productTable.getItems().add(0, product);
        });

        var removeRowMenuItem = new MenuItem("Ukloni proizvod");
        productTableContextMenu.getItems().add(removeRowMenuItem);
        removeRowMenuItem.setOnAction(event -> {
            ReportProductResponse selectedItem = productTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                productTable.getItems().remove(selectedItem);
            }
        });

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

        rbProductTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        productTable.getItems().indexOf(cellData.getValue()) + 1 + "."
                )
        );

        productNameTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getProduct())
        );
        productNameTableColumn.setEditable(true);
        productNameTableColumn.setCellFactory(new Callback<TableColumn<ReportProductResponse, ProductBasicResponse>, TableCell<ReportProductResponse, ProductBasicResponse>>() {
            @Override
            public TableCell<ReportProductResponse, ProductBasicResponse> call(
                    TableColumn<ReportProductResponse,
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
                                comboBox.setItems(rawMaterials);
                                comboBox.setEditable(true);
                            }

                            comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                                if (getTableRow() != null && getTableRow().getItem() != null) {
                                    ReportProductResponse rowItem = getTableRow().getItem();
                                    rowItem.setProduct(newValue);
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
                }
        );

        productAmountTableColumn.setEditable(true);
        productAmountTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productAmountTableColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            ReportProductResponse editedItem = event.getRowValue();

            try {
                double amount = Double.parseDouble(newValue);
                editedItem.setAmount((float) amount);

                event.getTableView().refresh();
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + newValue);
            }
        });

        this.saveBtn.setOnAction(event -> save());
        this.resetChangesBtn.setOnAction(event -> load(report.getId(), reportType));
    }

    public void load(Long reportId, ReportType reportType) {
        this.reportType = reportType;

        List<String> companyNames = this.reportService.getAllCompanyNames();
        TextFields.bindAutoCompletion(supplierCompanyNameInput, companyNames);

        List<String> userNames = this.reportService.getAllSignedUserNames();
        TextFields.bindAutoCompletion(signedByNameInput, userNames);

        if (reportId != null) {
            var report = this.reportService.get(reportId);

            this.titleLabel.setText(
                    String.format("%s %s", report.getType().toString(), report.getCode())
            );

            if(reportType == ReportType.RECEIPT) {
                this.supplierReportCodeGroup.setManaged(true);
                this.supplierReportCodeGroup.setVisible(true);
                this.supplierReportCodeInput.setText(
                        report.getReceipt().getSupplierReportCode()
                );

                this.supplierCompanyGroup.setVisible(true);
                this.supplierCompanyGroup.setManaged(true);
                this.supplierCompanyNameInput.setText(
                        report.getReceipt().getSupplierCompanyName()
                );

                this.receiptCompanyGroup.setVisible(false);
                this.receiptCompanyGroup.setManaged(false);

            } else if (reportType == ReportType.SHIPMENT) {
                this.supplierReportCodeGroup.setVisible(false);
                this.supplierReportCodeGroup.setManaged(false);

                this.supplierCompanyGroup.setVisible(false);
                this.supplierCompanyGroup.setManaged(false);

                this.receiptCompanyGroup.setVisible(true);
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
            if(reportType == ReportType.RECEIPT) {
                this.titleLabel.setText("Kreiraj primku");
            } else if(reportType == ReportType.SHIPMENT) {
                this.titleLabel.setText("Kreiraj otpremnicu");
            }
        }
    }

    private void save() {
        if (this.report == null) {
            if(reportType == ReportType.RECEIPT) {
                var request = new ReceiptReportCreateRequest();
                request.setDate(this.signedOnDatePicker.getValue());
                request.setSignedByName(this.signedByNameInput.getText());
                request.setCode(this.codeInput.getText());
                request.setDescriptionHtml(this.descriptionHtmlEditor.getHtmlText());
                request.setPlaceOfPublish(this.signedOnPlaceInput.getText());
                request.setSupplierReportCode(this.supplierReportCodeInput.getText());
                request.setSupplierCompanyName(this.supplierCompanyNameInput.getText());

            } else if (reportType == ReportType.SHIPMENT) {
                var request = new ShipmentReportCreateRequest();
                request.setDate(this.signedOnDatePicker.getValue());
                request.setSignedByName(this.signedByNameInput.getText());
                request.setCode(this.codeInput.getText());
                request.setDescriptionHtml(this.descriptionHtmlEditor.getHtmlText());
                request.setPlaceOfPublish(this.signedOnPlaceInput.getText());
                request.setReceiptCompanyName(this.receiptCompanyNameInput.getText());
            }

            this.load(this.report.getId(), this.reportType);
        }

        if(reportType == ReportType.RECEIPT) {
            var request = new ReceiptReportUpdateRequest();
            request.setDate(this.signedOnDatePicker.getValue());
            request.setSignedByName(this.signedByNameInput.getText());
            request.setCode(this.codeInput.getText());
            request.setDescriptionHtml(this.descriptionHtmlEditor.getHtmlText());
            request.setPlaceOfPublish(this.signedOnPlaceInput.getText());
            request.setSupplierReportCode(this.supplierReportCodeInput.getText());
            request.setSupplierCompanyName(this.supplierCompanyNameInput.getText());

            this.reportService.updateReceipt(report.getId(), request);
        } else if (reportType == ReportType.SHIPMENT) {
            var request = new ShipmentReportUpdateRequest();
            request.setDate(this.signedOnDatePicker.getValue());
            request.setSignedByName(this.signedByNameInput.getText());
            request.setCode(this.codeInput.getText());
            request.setDescriptionHtml(this.descriptionHtmlEditor.getHtmlText());
            request.setPlaceOfPublish(this.signedOnPlaceInput.getText());
            request.setReceiptCompanyName(this.receiptCompanyNameInput.getText());

            this.reportService.updateShipment(report.getId(), request);
        }

        this.load(this.report.getId(), this.reportType);
    }
}