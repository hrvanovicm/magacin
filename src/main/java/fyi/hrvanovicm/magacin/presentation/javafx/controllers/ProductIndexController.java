package fyi.hrvanovicm.magacin.presentation.javafx.controllers;

import fyi.hrvanovicm.magacin.presentation.javafx.factory.CellValueFactory;
import fyi.hrvanovicm.magacin.application.product.dto.ProductDTO;
import fyi.hrvanovicm.magacin.application.product.queries.ProductQueryService;
import fyi.hrvanovicm.magacin.application.product.requests.ProductSearchParamsDTO;
import fyi.hrvanovicm.magacin.domain.products.*;
import fyi.hrvanovicm.magacin.presentation.javafx.app.Router;
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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@FxmlView("/views/product-index.fxml")
public class ProductIndexController implements AutoLoadController {
    @FXML
    private TableView<ProductDTO> tableView;

    @FXML
    private TextField filterSearchInput;

    @FXML
    private CheckComboBox<ProductCategory> filterCategoryCombo;

    @FXML
    private SearchableComboBox<String> filterTagCombo;

    @FXML
    private CheckBox filterAmountBelowWarningAmount;

    @FXML
    private TableColumn<ProductDTO, String> rbTableColumn;

    @FXML
    private TableColumn<ProductDTO, String> nameTableColumn;

    @FXML
    private TableColumn<ProductDTO, String> codeTableColumn;

    @FXML
    private TableColumn<ProductDTO, String> categoryTableColumn;

    @FXML
    private TableColumn<ProductDTO, String> tagTableColumn;

    @FXML
    private TableColumn<ProductDTO, String> inStockAmountTableColumn;

    @FXML
    private Label tableResultStatusInfo;

    @FXML
    private Button createBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button resetBtn;

    private final ProductQueryService productQueryService;
    private final Router router;

    @Autowired
    public ProductIndexController(ProductQueryService productQueryService, Router router) {
        this.productQueryService = productQueryService;
        this.router = router;
    }

    /**
     * Load data but with predefined product category.
     */
    public void load(ProductCategory category) {
        filterCategoryCombo.getItems().add(category);
        filterCategoryCombo.getCheckModel().check(0);

        this.load();
    }

    /**
     * Load data but without predefined filters.
     */
    public void load() {
        var params = ProductSearchParamsDTO.builder()
                .search(filterSearchInput.getText())
                .tags(Stream.of(filterTagCombo.getSelectionModel().getSelectedItem()).filter(Objects::nonNull).toList())
                .categories(filterCategoryCombo.getCheckModel().getCheckedItems().stream().map(ProductCategory::getValue).toList())
                .lowInMemoryStock(filterAmountBelowWarningAmount.isSelected() ? true : null)
                .build();

        ObservableList<ProductDTO> products = FXCollections.observableArrayList(
                this.productQueryService.getAll(params)
        );
        tableView.setItems(products);

        List<String> tags = productQueryService.getAllTagNames();
        tags.removeAll(filterTagCombo.getItems()); // Don't duplicate items in combo.
        filterTagCombo.getItems().addAll(tags);

        List<ProductCategory> categories = productQueryService.getAllCategoryNames();
        categories.removeAll(filterCategoryCombo.getItems()); // Don't duplicate items in combo.
        filterCategoryCombo.getItems().addAll(categories);

        tableResultStatusInfo.setText(
                String.format("Broj pronađenih rezultata: %d", products.size())
        );

        tableView.scrollTo(0);
    }

    public void initialize() {
        // Table configurations.
        tableView.setEditable(false); // There is external view for resource create/update.
        tableView.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                ProductDTO product = tableView.getSelectionModel().getSelectedItem();
                if(product != null) {
                    this.router.navigateTo(ProductEditController.class, controller -> {
                        controller.load(product.getId());
                    });
                }
            }
        });

        // Table value factory.
        rbTableColumn.setCellValueFactory(CellValueFactory.sequenceNumber(tableView));
        nameTableColumn.setCellValueFactory(CellValueFactory.productName());
        codeTableColumn.setCellValueFactory(CellValueFactory.productCode());
        categoryTableColumn.setCellValueFactory(CellValueFactory.productCategory());
        tagTableColumn.setCellValueFactory(CellValueFactory.productTag());
        inStockAmountTableColumn.setCellValueFactory(CellValueFactory.amount(
                p -> p,
                ProductDTO::getInStockAmount,
                true
        ));

        // Table action buttons.
        createBtn.setOnAction(event -> router.navigateTo(ProductEditController.class));
        refreshBtn.setOnAction(event -> load());
        resetBtn.setOnAction(event -> {
            filterSearchInput.clear();
            filterCategoryCombo.getCheckModel().clearChecks();
            filterTagCombo.setValue(null);
            filterAmountBelowWarningAmount.setSelected(false);
            load();
        });

        // Table filters.
        filterSearchInput.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> load());
        filterCategoryCombo
                .getCheckModel()
                .getCheckedItems()
                .addListener((ListChangeListener<? super ProductCategory>) observable -> load());
        filterTagCombo
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> load());
        filterAmountBelowWarningAmount.setOnAction(event -> load());
    }
}