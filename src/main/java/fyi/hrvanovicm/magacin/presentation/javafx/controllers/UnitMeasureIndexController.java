package fyi.hrvanovicm.magacin.presentation.javafx.controllers;

import fyi.hrvanovicm.magacin.application.unit_measure.handlers.CreateUnitMeasureHandler;
import fyi.hrvanovicm.magacin.application.unit_measure.handlers.DeleteUnitMeasureHandler;
import fyi.hrvanovicm.magacin.application.unit_measure.handlers.UpdateUnitMeasureHandler;
import fyi.hrvanovicm.magacin.application.unit_measure.queries.UnitMeasureQueryService;
import fyi.hrvanovicm.magacin.application.unit_measure.requests.UnitMeasureEditRequest;
import fyi.hrvanovicm.magacin.presentation.javafx.factory.CellValueFactory;
import fyi.hrvanovicm.magacin.domain.products.*;
import fyi.hrvanovicm.magacin.application.unit_measure.dto.UnitMeasureDTO;
import fyi.hrvanovicm.magacin.presentation.javafx.app.Router;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SearchableComboBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/views/unitmeasure-index.fxml")
public class UnitMeasureIndexController implements AutoLoadController {
    @FXML
    private TableView<UnitMeasureDTO> tableView;

    @FXML
    private TextField filterSearchInput;

    @FXML
    private CheckComboBox<ProductCategory> filterCategoryCombo;

    @FXML
    private SearchableComboBox<String> filterTagCombo;

    @FXML
    private CheckBox filterAmountBelowWarningAmount;

    @FXML
    private TableColumn<UnitMeasureDTO, String> rbCol;

    @FXML
    private TableColumn<UnitMeasureDTO, String> nameCol;

    @FXML
    private TableColumn<UnitMeasureDTO, String> shortNameCol;

    @FXML
    private TableColumn<UnitMeasureDTO, Boolean> isIntegerCol;

    @FXML
    private Label tableResultStatusInfo;

    @FXML
    private Button refreshBtn;

    private final UnitMeasureQueryService unitMeasureQueryService;

    private final CreateUnitMeasureHandler createUnitMeasureHandler;
    private final UpdateUnitMeasureHandler updateUnitMeasureHandler;
    private final DeleteUnitMeasureHandler deleteUnitMeasureHandler;

    private final Router router;

    @Autowired
    public UnitMeasureIndexController(
            UnitMeasureQueryService unitMeasureQueryService,
          CreateUnitMeasureHandler createUnitMeasureHandler,
          UpdateUnitMeasureHandler updateUnitMeasureHandler,
          DeleteUnitMeasureHandler deleteUnitMeasureHandler,
          Router router
    ) {
        this.unitMeasureQueryService = unitMeasureQueryService;
        this.createUnitMeasureHandler = createUnitMeasureHandler;
        this.updateUnitMeasureHandler = updateUnitMeasureHandler;
        this.deleteUnitMeasureHandler = deleteUnitMeasureHandler;
        this.router = router;
    }

    public void load() {
        ObservableList<UnitMeasureDTO> unitMeasures = FXCollections.observableArrayList(
                this.unitMeasureQueryService.getAll()
        );
        tableView.setItems(unitMeasures);
        tableResultStatusInfo.setText(String.format("Broj pronađenih rezultata: %d", unitMeasures.size()));

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
            CheckBoxTableCell<UnitMeasureDTO, Boolean> cell = new CheckBoxTableCell<>();
            cell.setSelectedStateCallback(index -> {
                BooleanProperty prop = new SimpleBooleanProperty(col.getTableView().getItems().get(index).getIsInteger());
                prop.addListener((obs, oldVal, newVal) -> {
                    UnitMeasureDTO rowItem = col.getTableView().getItems().get(index);
                    rowItem.setIsInteger(newVal); // if not already bound
                    update(rowItem); // manually call your save
                });
                return prop;
            });
            return cell;
        });

        nameCol.setOnEditCommit(event -> {
            event.getRowValue().setName(event.getNewValue());
            this.update(event.getRowValue());
        });
        shortNameCol.setOnEditCommit(event -> {
            event.getRowValue().setShortName(event.getNewValue());
            this.update(event.getRowValue());
        });
        isIntegerCol.setOnEditStart(event -> {
            event.getRowValue().setIsInteger(event.getNewValue());
            this.update(event.getRowValue());
        });

        // Table action buttons.
        refreshBtn.setOnAction(event -> load());
    }

    public void update(UnitMeasureDTO unitMeasureDTO) {
        var request = new UnitMeasureEditRequest();

        request.setName(unitMeasureDTO.getName());
        request.setShortName(unitMeasureDTO.getShortName());
        request.setIsInteger(unitMeasureDTO.getIsInteger());

        this.updateUnitMeasureHandler.handle(unitMeasureDTO.getId(), request);
    }
}