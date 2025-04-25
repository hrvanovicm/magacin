package fyi.hrvanovicm.magacin.presentation.javafx.controllers;

import fyi.hrvanovicm.magacin.application.product.queries.ProductQueryService;
import fyi.hrvanovicm.magacin.application.report.dto.ReportDTO;
import fyi.hrvanovicm.magacin.application.report.queries.ReportQueryService;
import fyi.hrvanovicm.magacin.application.report.requests.ReportSearchParamsDTO;
import fyi.hrvanovicm.magacin.presentation.javafx.factory.CellValueFactory;
import fyi.hrvanovicm.magacin.application.product.dto.ProductDTO;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.report.*;
import fyi.hrvanovicm.magacin.presentation.javafx.app.Router;
import jakarta.persistence.criteria.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SearchableComboBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FxmlView("/views/report-index.fxml")
public class ReportIndexController implements AutoLoadController {
    @FXML
    private TableView<ReportDTO> tableView;

    @FXML
    private TextField filterSearchInput;

    @FXML
    private CheckComboBox<ReportType> filterTypeChoice;

    @FXML
    private DatePicker filterDateFromCombo;

    @FXML
    private DatePicker filterDateToCombo;

    @FXML
    private SearchableComboBox<String> filterCompanyCombo;

    @FXML
    private SearchableComboBox<ProductDTO> filterProductCombo;

    @FXML
    private TableColumn<ReportDTO, String> rbTableColumn;

    @FXML
    private TableColumn<ReportDTO, String> typeTableColumn;

    @FXML
    private TableColumn<ReportDTO, String> codeTableColumn;

    @FXML
    private TableColumn<ReportDTO, String> dateTableColumn;

    @FXML
    private TableColumn<ReportDTO, String> companyTableColumn;

    @FXML
    private TableColumn<ReportDTO, String> signedByTableColumn;

    @FXML
    private Label tableResultStatusInfo;

    @FXML
    private Button receiptCreateBtn;

    @FXML
    private Button shipmentCreateBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button resetBtn;

    private final ReportQueryService reportQueryService;
    private final ProductQueryService productQueryService;
    private final Router router;

    @Autowired
    public ReportIndexController(
            ProductQueryService productQueryService,
            ReportQueryService reportQueryService,
            Router router
    ) {
        this.reportQueryService = reportQueryService;
        this.productQueryService = productQueryService;
        this.router = router;
    }

    public void load(ReportType type) {
        filterTypeChoice.getItems().add(type);
        filterTypeChoice.getCheckModel().check(0);
        this.load();
    }

    public void load() {
        ObservableList<String> companies = FXCollections.observableArrayList();

        var params = ReportSearchParamsDTO.builder()
                .search(filterSearchInput.getText())
                .types(filterTypeChoice.getCheckModel().getCheckedItems().stream().map(ReportType::getValue).toList())
                .signedDateFrom(filterDateFromCombo.getValue() != null ? filterDateFromCombo.getValue().toString() : null)
                .signedDateTo(filterDateToCombo.getValue() != null ? filterDateToCombo.getValue().toString() : null)
                .company(filterCompanyCombo.getValue())
                .hasProductId(filterProductCombo.getValue() != null ? filterProductCombo.getValue().getId() : null)
                .build();

        ObservableList<ReportDTO> reports = FXCollections.observableArrayList(
                reportQueryService.getAll(params)
        );
        tableView.setItems(reports);

        List<ProductDTO> products = this.productQueryService.getAll();
        products.removeAll(filterProductCombo.getItems());
        filterProductCombo.getItems().addAll(products);

        List<ReportType> reportTypes = reportQueryService.getAllReportTypes();
        reportTypes.removeAll(filterTypeChoice.getItems());
        filterTypeChoice.getItems().addAll(reportTypes);

        List<String> companyNames = reportQueryService.getAllCompanyNames();
        companyNames.removeAll(filterCompanyCombo.getItems());
        filterCompanyCombo.getItems().addAll(companyNames);

        tableResultStatusInfo.setText(String.format("Broj pronađenih rezultata: %d", reports.size()));

        tableView.scrollTo(0);
    }

    public void initialize() {
        // Table configurations.
        tableView.setEditable(false); // There is external view for resource create/update.
        tableView.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                ReportDTO report = tableView.getSelectionModel().getSelectedItem();
                if(report != null) {
                    this.router.navigateTo(ReportEditController.class, controller -> {
                        controller.load(report.getId(), report.getType());
                    });
                }
            }
        });

        // Table value factory.
        rbTableColumn.setCellValueFactory(CellValueFactory.sequenceNumber(tableView));
        typeTableColumn.setCellValueFactory(CellValueFactory.reportType());
        codeTableColumn.setCellValueFactory(CellValueFactory.reportCode());
        dateTableColumn.setCellValueFactory(CellValueFactory.reportDate());
        companyTableColumn.setCellValueFactory(CellValueFactory.reportCompanyName(e -> e));
        signedByTableColumn.setCellValueFactory(CellValueFactory.reportSignedByName());

        // Table action buttons.
        receiptCreateBtn.setOnAction(event -> this.router.navigateTo(ReportEditController.class, controller -> {
            controller.load(ReportType.RECEIPT);
        }));
        shipmentCreateBtn.setOnAction(event -> this.router.navigateTo(ReportEditController.class, controller -> {
            controller.load(ReportType.SHIPMENT);
        }));
        refreshBtn.setOnAction(event -> load());
        resetBtn.setOnAction(event -> {
            filterSearchInput.clear();
            filterProductCombo.getSelectionModel().clearSelection();
            filterCompanyCombo.getSelectionModel().clearSelection();
            filterDateFromCombo.setValue(null);
            filterDateToCombo.setValue(null);
            filterTypeChoice.getCheckModel().clearChecks();

            load();
        });

        // Table filter controls.
        filterSearchInput.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> load());
        filterProductCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> load());
        filterCompanyCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> load());
        filterTypeChoice.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super ReportType>) observable -> load());
        filterDateFromCombo.setOnAction(event -> load());
        filterDateToCombo.setOnAction(event -> load());
    }
}