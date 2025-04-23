package fyi.hrvanovicm.magacin.application.javafx.controllers;

import fyi.hrvanovicm.magacin.application.javafx.components.CellValueFactory;
import fyi.hrvanovicm.magacin.domain.products.*;
import fyi.hrvanovicm.magacin.infrastructure.javafx.Router;
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

    private final ProductService productService;
    private final Router router;

    @Autowired
    public ProductIndexController(ProductService productService, Router router) {
        this.productService = productService;
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
        Specification<ProductEntity> spec = (root, query, builder) -> {
            Predicate predicates = builder.conjunction();

            if (!filterSearchInput.getText().isBlank()) {
                predicates = builder.and(
                        predicates,
                        ProductSpecification
                                .search(filterSearchInput.getText())
                                .toPredicate(root, query, builder)
                );
            }

            if(!filterTagCombo.getSelectionModel().isEmpty()) {
                predicates = builder.and(
                        predicates,
                        ProductSpecification
                                .hasTag(filterTagCombo.getSelectionModel().getSelectedItem())
                                .toPredicate(root, query, builder)
                );
            }

            if(!filterCategoryCombo.getCheckModel().getCheckedItems().isEmpty()) {
                predicates = builder.and(
                        predicates,
                        ProductSpecification
                                .hasCategory(filterCategoryCombo.getCheckModel().getCheckedItems().stream().toList())
                                .toPredicate(root, query, builder)
                );
            }

            if(filterAmountBelowWarningAmount.isSelected()) {
                predicates = builder.and(
                        predicates,
                        ProductSpecification
                                .hasLowInStockAmount()
                                .toPredicate(root, query, builder)
                );
            }

            return predicates;
        };

        ObservableList<ProductDTO> products = FXCollections.observableArrayList(
                this.productService.getAll(spec)
        );
        tableView.setItems(products);

        List<String> tags = productService.getAllTagNames();
        tags.removeAll(filterTagCombo.getItems()); // Don't duplicate items in combo.
        filterTagCombo.getItems().addAll(tags);

        List<ProductCategory> categories = productService.getAllCategoryNames();
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