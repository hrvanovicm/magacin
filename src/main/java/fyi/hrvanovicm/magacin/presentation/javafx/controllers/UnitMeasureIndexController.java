package fyi.hrvanovicm.magacin.presentation.javafx.controllers;

import fyi.hrvanovicm.magacin.application.product.dto.ProductReceptionDTO;
import fyi.hrvanovicm.magacin.application.unit_measure.handlers.CreateUnitMeasureHandler;
import fyi.hrvanovicm.magacin.application.unit_measure.handlers.DeleteUnitMeasureHandler;
import fyi.hrvanovicm.magacin.application.unit_measure.handlers.UpdateUnitMeasureHandler;
import fyi.hrvanovicm.magacin.application.unit_measure.queries.UnitMeasureQueryService;
import fyi.hrvanovicm.magacin.application.unit_measure.requests.UnitMeasureEditRequest;
import fyi.hrvanovicm.magacin.presentation.javafx.factory.CellValueFactory;
import fyi.hrvanovicm.magacin.domain.products.*;
import fyi.hrvanovicm.magacin.application.unit_measure.dto.UnitMeasureDTO;
import fyi.hrvanovicm.magacin.presentation.javafx.app.Router;
import fyi.hrvanovicm.magacin.presentation.javafx.factory.DialogFactory;
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

import java.util.ArrayList;
import java.util.List;

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
    private Button saveBtn;

    @FXML
    private Button refreshBtn;

    private final UnitMeasureQueryService unitMeasureQueryService;

    private final CreateUnitMeasureHandler createUnitMeasureHandler;
    private final UpdateUnitMeasureHandler updateUnitMeasureHandler;
    private final DeleteUnitMeasureHandler deleteUnitMeasureHandler;

    private final Router router;
    private final DialogFactory dialogService;

    private List<UnitMeasureDTO> tableRowDeleteBucket = new ArrayList<>();

    @Autowired
    public UnitMeasureIndexController(
            UnitMeasureQueryService unitMeasureQueryService,
          CreateUnitMeasureHandler createUnitMeasureHandler,
          UpdateUnitMeasureHandler updateUnitMeasureHandler,
          DeleteUnitMeasureHandler deleteUnitMeasureHandler,
          Router router,
            DialogFactory dialogService
    ) {
        this.unitMeasureQueryService = unitMeasureQueryService;
        this.createUnitMeasureHandler = createUnitMeasureHandler;
        this.updateUnitMeasureHandler = updateUnitMeasureHandler;
        this.deleteUnitMeasureHandler = deleteUnitMeasureHandler;
        this.router = router;
        this.dialogService = dialogService;
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

        // Table context menu.
        var contextMenu = new ContextMenu();
        var addNewRowMenuItem = new MenuItem("Kreiraj");
        contextMenu.getItems().add(addNewRowMenuItem);
        addNewRowMenuItem.setOnAction(event -> {
            var unitMeasureDTO = new UnitMeasureDTO();
            unitMeasureDTO.setIsInteger(true);
            tableView.getItems().add(0, unitMeasureDTO);
        });

        var removeRowMenuItem = new MenuItem("Ukloni");
        contextMenu.getItems().add(removeRowMenuItem);
        removeRowMenuItem.setOnAction(event -> {
            UnitMeasureDTO selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                tableRowDeleteBucket.add(selectedItem);
                tableView.getItems().remove(selectedItem);
            }
        });

        tableView.setContextMenu(contextMenu);
        tableView.setOnContextMenuRequested(
                event -> removeRowMenuItem.setDisable(
                        tableView.getSelectionModel().getSelectedItem() == null
                )
        );

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
                    rowItem.setIsInteger(newVal);
                });
                return prop;
            });
            return cell;
        });

        nameCol.setOnEditCommit(event -> {
            event.getRowValue().setName(event.getNewValue());
        });
        shortNameCol.setOnEditCommit(event -> {
            event.getRowValue().setShortName(event.getNewValue());
        });
        isIntegerCol.setOnEditStart(event -> {
            event.getRowValue().setIsInteger(event.getNewValue());
        });

        // Table action buttons.
        refreshBtn.setOnAction(event -> load());
        saveBtn.setOnAction(event -> save());
    }

    public void save() {
        var unitMeasures = tableView.getItems();

        unitMeasures.forEach(unitMeasureDTO -> {
            var request = UnitMeasureEditRequest.from(unitMeasureDTO);

            if(unitMeasureDTO.getName() == null || unitMeasureDTO.getShortName() == null) {
                return;
            }

            try {
                if(tableRowDeleteBucket.contains(unitMeasureDTO)) {
                    deleteUnitMeasureHandler.handle(unitMeasureDTO.getId());
                } else if(unitMeasureDTO.getId() == null) {
                    createUnitMeasureHandler.handle(request);
                } else {
                    updateUnitMeasureHandler.handle(unitMeasureDTO.getId(), request);
                }
            } catch(Exception e) {
                dialogService.showErrorDialog(e.getMessage());
                e.printStackTrace();
            }
        });

        this.load();
    }
}