package fyi.hrvanovicm.magacin.application.javafx.controllers;

import fyi.hrvanovicm.magacin.application.javafx.components.CellFactory;
import fyi.hrvanovicm.magacin.application.javafx.components.CellValueFactory;
import fyi.hrvanovicm.magacin.domain.products.ProductBasicResponse;
import fyi.hrvanovicm.magacin.domain.products.ProductCategory;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.products.ProductSpecification;
import fyi.hrvanovicm.magacin.domain.report.ReportDetailsResponse;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductRequest;
import fyi.hrvanovicm.magacin.domain.report.product.ReportProductResponse;
import fyi.hrvanovicm.magacin.domain.report.ReportService;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import fyi.hrvanovicm.magacin.domain.report.receipt.ReceiptReportRequest;
import fyi.hrvanovicm.magacin.domain.report.shipment.ShipmentReportRequest;
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
import net.rgielen.fxweaver.core.FxmlView;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@FxmlView("/views/report-edit.fxml")
public class ReportEditController {
    // Controller state.
    ReportDetailsResponse report;
    ReportType reportType;

    private List<ProductBasicResponse> products;

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
    @FXML
    private Button pdfExportBtn;

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

    @Value("classpath:pdf/receipt.jrxml")
    Resource resourceFile;

    @Autowired
    private ReportService reportService;

    public ReportEditController(UnitMeasureService unitMeasureService, ProductService productService) {
        this.unitMeasureService = unitMeasureService;
        this.productService = productService;
    }

    public void initialize() {
        productTable.setEditable(true);

        this.pdfExportBtn.setOnAction(event -> {
            try {
                InputStream jrxmlInputStream = getClass().getClassLoader().getResourceAsStream("pdf/receipt.jrxml");
                if (jrxmlInputStream == null) {
                    System.out.println("Resource not found!");
                    return;
                }
                JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInputStream);
                List<ReportDetailsResponse> beans = new ArrayList<>();
                beans.add(report);
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(beans);
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("title", "Your Custom Title");

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                JasperViewer.viewReport(jasperPrint, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        var productTableContextMenu = new ContextMenu();

        var addNewRowMenuItem = new MenuItem("Dodaj proizvod");
        productTableContextMenu.getItems().add(addNewRowMenuItem);
        addNewRowMenuItem.setOnAction(event -> {
            var product = new ReportProductResponse();

            product.setAmount(Float.valueOf("0.0"));

            productTable.getItems().add(product);
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
                cellData.getValue().getProduct() != null ? new SimpleObjectProperty<>(cellData.getValue().getProduct()) : null
        );
        productNameTableColumn.setEditable(true);
        productNameTableColumn.setCellFactory(CellFactory.productSearchableCombo(
                () -> products,
                ReportProductResponse::setProduct
        ));


        productAmountTableColumn.setCellValueFactory(CellValueFactory.productAmount());

        productAmountTableColumn.setEditable(true);
        productAmountTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productAmountTableColumn.setOnEditCommit(CellFactory.amountTextField(
                ReportProductResponse::setAmount
        ));

        this.saveBtn.setOnAction(event -> save());
        this.resetChangesBtn.setOnAction(event -> load(report.getId(), reportType));
    }

    public void load(Long reportId, ReportType reportType) {
        this.products = this.productService.getAll(ProductSpecification.hasCategories(
                List.of(ProductCategory.PRODUCT, ProductCategory.COMMERCIAL)
        ));
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
        if(reportType == ReportType.RECEIPT) {
            var request = new ReceiptReportRequest();

            request.setDate(this.signedOnDatePicker.getValue());
            request.setSignedByName(this.signedByNameInput.getText());
            request.setCode(this.codeInput.getText());
            request.setDescriptionHtml(this.descriptionHtmlEditor.getHtmlText());
            request.setPlaceOfPublish(this.signedOnPlaceInput.getText());
            request.setSupplierReportCode(this.supplierReportCodeInput.getText());
            request.setSupplierCompanyName(this.supplierCompanyNameInput.getText());
            request.setProducts(
                    productTable.getItems()
                            .stream()
                            .filter(item -> item.getProduct() != null)
                            .map(item -> {
                                var req = new ReportProductRequest();
                                req.setId(item.getId());
                                req.setProductId(item.getProduct().getId());
                                req.setAmount(item.getAmount());
                                return req;
                            })
                            .collect(Collectors.toList())
            );

            if(report != null) {
                request.setId(report.getId());
            }

            this.report = this.reportService.saveReceipt(request);
        } else if (reportType == ReportType.SHIPMENT) {
            var request = new ShipmentReportRequest();

            request.setDate(this.signedOnDatePicker.getValue());
            request.setSignedByName(this.signedByNameInput.getText());
            request.setCode(this.codeInput.getText());
            request.setDescriptionHtml(this.descriptionHtmlEditor.getHtmlText());
            request.setPlaceOfPublish(this.signedOnPlaceInput.getText());
            request.setReceiptCompanyName(this.receiptCompanyNameInput.getText());
            request.setProducts(
                    productTable.getItems()
                            .stream()
                            .filter(item -> item.getProduct() != null)
                            .map(item -> {
                                var req = new ReportProductRequest();
                                req.setId(item.getId());
                                req.setProductId(item.getId());
                                req.setAmount(item.getAmount());
                                return req;
                            })
                            .collect(Collectors.toList())
            );

            if(report != null) {
                request.setId(report.getId());
            }

            this.report = this.reportService.saveShipment(request);
        }

        this.load(this.report.getId(), this.reportType);
        this.productTable.refresh();
    }
}