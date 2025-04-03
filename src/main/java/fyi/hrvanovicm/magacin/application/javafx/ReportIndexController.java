package fyi.hrvanovicm.magacin.application.javafx;

import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.report.*;
import fyi.hrvanovicm.magacin.infrastructure.javafx.Router;
import jakarta.persistence.criteria.Predicate;
import javafx.beans.property.SimpleStringProperty;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@FxmlView("/views/report-index.fxml")
public class ReportIndexController {
    @FXML
    private TableView<ReportResponse> tableView;

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
    private SearchableComboBox<String> filterProductCombo;

    @FXML
    private TableColumn<ReportResponse, String> rbTableColumn;

    @FXML
    private TableColumn<ReportResponse, String> typeTableColumn;

    @FXML
    private TableColumn<ReportResponse, String> codeTableColumn;

    @FXML
    private TableColumn<ReportResponse, String> dateTableColumn;

    @FXML
    private TableColumn<ReportResponse, String> signedByTableColumn;

    @FXML
    private Label tableResultStatusInfo;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button resetBtn;

    private final ReportService reportService;

    @Autowired
    private ProductService productService;

    @Autowired
    private Router router;

    @Autowired
    public ReportIndexController(ReportService reportService) {
        this.reportService = reportService;
    }

    public void load() {
        ObservableList<String> companies = FXCollections.observableArrayList();

        Specification<ReportEntity> spec = (root, query, builder) -> {
            Predicate predicates = builder.conjunction();

            if(!filterSearchInput.getText().isBlank()) {
             predicates = builder.and(predicates, ReportSpecification.search(filterSearchInput.getText()).toPredicate(root, query, builder));
            }

            if(!filterTypeChoice.getCheckModel().isEmpty()) {
                predicates = builder.and(
                        predicates,
                        ReportSpecification
                                .hasTypes(filterTypeChoice.getCheckModel().getCheckedItems())
                                .toPredicate(root, query, builder)
                );
            }

            if(
                    filterDateFromCombo.getValue() != null
                    || filterDateToCombo.getValue() != null
            ) {
                predicates = builder.and(
                        predicates,
                        ReportSpecification
                                .betweenDates(filterDateFromCombo.getValue(), filterDateToCombo.getValue())
                                .toPredicate(root, query, builder)
                );
            }

            if(filterCompanyCombo.getValue() != null) {
                predicates = builder.and(
                        predicates,
                        ReportSpecification
                                .hasCompanyName(filterCompanyCombo.getSelectionModel().getSelectedItem())
                                .toPredicate(root, query, builder)
                );
            }

            return predicates;
        };

        ObservableList<ReportResponse> reports = FXCollections.observableArrayList(reportService.getAll(spec));
        tableView.setItems(reports);

        List<String> products = productService.getAllProductNames();
        products.removeAll(filterProductCombo.getItems());
        filterProductCombo.getItems().addAll(products);

        List<ReportType> reportTypes = reportService.getAllReportTypes();
        reportTypes.removeAll(filterTypeChoice.getItems());
        filterTypeChoice.getItems().addAll(reportTypes);

        List<String> companyNames = reportService.getAllCompanyNames();
        companyNames.removeAll(filterCompanyCombo.getItems());
        filterCompanyCombo.getItems().addAll(companyNames);

        tableResultStatusInfo
                .setText(String.format("Broj pronađenih rezultata: %d", reports.size()));


        tableView.scrollTo(0);
    }

    public void initialize() {
        tableView.setEditable(false); // There is external view for resource create/update.
        tableView.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                ReportResponse report = tableView.getSelectionModel().getSelectedItem();
                if(report != null) {
                    this.router.navigateTo(ReportEditController.class, controller -> {
                        ((ReportEditController) controller).load(report.getId(), report.getType());
                    });
                }
            }
        });

        rbTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        tableView.getItems().indexOf(cellData.getValue()) + 1 + "."
                )
        );

        typeTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getType().toString())
        );

        codeTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCode())
        );

        dateTableColumn.setCellValueFactory(cellData -> {
                    String dateString = cellData.getValue().getDate();
                    if (dateString != null && !dateString.isEmpty()) {
                        LocalDate localDate = LocalDate.parse(dateString);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMM yyyy");
                        String formattedDate = localDate.format(formatter);
                        return new SimpleStringProperty(formattedDate);
                    } else {
                        return new SimpleStringProperty("");
                    }
                });

        signedByTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getSignedByName())
        );

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

        filterSearchInput.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> load());

        filterProductCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> load());
        filterCompanyCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> load());
        filterTypeChoice.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super ReportType>) observable -> load());
        filterDateFromCombo.setOnAction(event -> load());
        filterDateToCombo.setOnAction(event -> load());

        load();
    }
}