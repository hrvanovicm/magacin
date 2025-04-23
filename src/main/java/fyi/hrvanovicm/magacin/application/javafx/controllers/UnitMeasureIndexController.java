package fyi.hrvanovicm.magacin.application.javafx.controllers;

import fyi.hrvanovicm.magacin.application.javafx.components.CellFactory;
import fyi.hrvanovicm.magacin.application.javafx.components.CellValueFactory;
import fyi.hrvanovicm.magacin.domain.products.*;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureRequest;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureResponse;
import fyi.hrvanovicm.magacin.domain.unit_measure.UnitMeasureService;
import fyi.hrvanovicm.magacin.infrastructure.javafx.Router;
import jakarta.persistence.criteria.Predicate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SearchableComboBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FxmlView("/views/unitmeasure-index.fxml")
public class UnitMeasureIndexController implements AutoLoadController {
    @FXML
    private TableView<UnitMeasureResponse> tableView;

    @FXML
    private TextField filterSearchInput;

    @FXML
    private CheckComboBox<ProductCategory> filterCategoryCombo;

    @FXML
    private SearchableComboBox<String> filterTagCombo;

    @FXML
    private CheckBox filterAmountBelowWarningAmount;

    @FXML
    private TableColumn<UnitMeasureResponse, String> rbCol;

    @FXML
    private TableColumn<UnitMeasureResponse, String> nameCol;

    @FXML
    private TableColumn<UnitMeasureResponse, String> shortNameCol;

    @FXML
    private TableColumn<UnitMeasureResponse, Boolean> isIntegerCol;

    @FXML
    private Label tableResultStatusInfo;

    @FXML
    private Button refreshBtn;

    private final UnitMeasureService unitMeasureService;
    private final Router router;

    @Autowired
    public UnitMeasureIndexController(UnitMeasureService unitMeasureService, Router router) {
        this.unitMeasureService = unitMeasureService;
        this.router = router;
    }

    public void load() {
        ObservableList<UnitMeasureResponse> unitMeasures = FXCollections.observableArrayList(
                this.unitMeasureService.getAll()
        );
        tableView.setItems(unitMeasures);

        tableResultStatusInfo
                .setText(String.format("Broj pronađenih rezultata: %d", unitMeasures.size()));

        tableView.scrollTo(0);
    }

    public void initialize() {
        // Table configurations.
        tableView.setEditable(true);

        // Table value factory.
        rbCol.setCellValueFactory(CellValueFactory.sequenceNumber(tableView));
        nameCol.setCellValueFactory(CellValueFactory.unitMeasureName());
        shortNameCol.setCellValueFactory(CellValueFactory.unitMeasureShortName());
        isIntegerCol.setCellValueFactory(CellValueFactory.unitMeasureIsInteger());

        // Table cell factory.
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        shortNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        isIntegerCol.setCellFactory(col -> {
            CheckBoxTableCell<UnitMeasureResponse, Boolean> cell = new CheckBoxTableCell<>();
            cell.setSelectedStateCallback(index -> {
                BooleanProperty prop = new SimpleBooleanProperty(col.getTableView().getItems().get(index).getIsInteger());
                prop.addListener((obs, oldVal, newVal) -> {
                    UnitMeasureResponse rowItem = col.getTableView().getItems().get(index);
                    rowItem.setIsInteger(newVal); // if not already bound
                    save(rowItem); // manually call your save
                });
                return prop;
            });
            return cell;
        });

        nameCol.setOnEditCommit(event -> {
            event.getRowValue().setName(event.getNewValue());
            this.save(event.getRowValue());
        });
        shortNameCol.setOnEditCommit(event -> {
            event.getRowValue().setShortName(event.getNewValue());
            this.save(event.getRowValue());
        });
        isIntegerCol.setOnEditStart(event -> {
            event.getRowValue().setIsInteger(event.getNewValue());
            this.save(event.getRowValue());
        });

        // Table action buttons.
        refreshBtn.setOnAction(event -> load());
    }

    public void save(UnitMeasureResponse response) {
        var req = new UnitMeasureRequest();

        req.setId(response.getId());
        req.setName(response.getName());
        req.setShortName(response.getShortName());
        req.setIsInteger(response.getIsInteger());

        this.unitMeasureService.save(req);
    }
}