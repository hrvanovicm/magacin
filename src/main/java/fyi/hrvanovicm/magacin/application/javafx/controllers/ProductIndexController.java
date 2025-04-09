package fyi.hrvanovicm.magacin.application.javafx.controllers;

import fyi.hrvanovicm.magacin.domain.products.*;
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
import java.util.List;

@Component
@FxmlView("/views/product-index.fxml")
public class ProductIndexController {
    /**
     * JavaFX relations.
     */
    @FXML
    private TableView<ProductBasicResponse> tableView;

    @FXML
    private TextField filterSearchInput;

    @FXML
    private CheckComboBox<ProductCategory> filterCategoryCombo;

    @FXML
    private SearchableComboBox<String> filterTagCombo;

    @FXML
    private CheckBox filterAmountBelowWarningAmount;

    @FXML
    private TableColumn<ProductBasicResponse, String> rbTableColumn;

    @FXML
    private TableColumn<ProductBasicResponse, String> nameTableColumn;

    @FXML
    private TableColumn<ProductBasicResponse, String> codeTableColumn;

    @FXML
    private TableColumn<ProductBasicResponse, String> categoryTableColumn;

    @FXML
    private TableColumn<ProductBasicResponse, String> tagTableColumn;

    @FXML
    private TableColumn<ProductBasicResponse, String> inStockAmountTableColumn;

    @FXML
    private Label tableResultStatusInfo;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button resetBtn;

    // Spring injections.
    private final ProductService productService;
    private final Router router;

    @Autowired
    public ProductIndexController(ProductService productService, Router router) {
        this.productService = productService;
        this.router = router;
    }

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
                                .hasCategories(filterCategoryCombo.getCheckModel().getCheckedItems().stream().toList())
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

        ObservableList<ProductBasicResponse> products = FXCollections.observableArrayList(this.productService.getAll(spec));
        tableView.setItems(products);

        List<String> tags = productService.getAllTagNames();
        tags.removeAll(filterTagCombo.getItems()); // Don't duplicate items in combo.
        filterTagCombo.getItems().addAll(tags);

        List<ProductCategory> categories = productService.getAllCategoryNames();
        categories.removeAll(filterCategoryCombo.getItems()); // Don't duplicate items in combo.
        filterCategoryCombo.getItems().addAll(categories);

        tableResultStatusInfo
                .setText(String.format("Broj pronađenih rezultata: %d", products.size()));

        tableView.scrollTo(0);
    }

    public void initialize() {
        tableView.setEditable(false); // There is external view for resource create/update.
        tableView.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                ProductBasicResponse product = tableView.getSelectionModel().getSelectedItem();
                if(product != null) {
                    this.router.navigateTo(ProductEditController.class, controller -> {
                        ((ProductEditController) controller).load(product.getId());
                    });
                }
            }
        });

        rbTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        tableView.getItems().indexOf(cellData.getValue()) + 1 + "."
                )
        );

        nameTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getName())
        );

        codeTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCode())
        );

        categoryTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCategory().toString())
        );

        tagTableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.join(", ", cellData.getValue().getTags()))
        );

        inStockAmountTableColumn.setCellValueFactory(
                cellData -> {
                    if(cellData.getValue().getUnitMeasure().getIsInteger()) {
                        return new SimpleStringProperty(
                                String.format("%.0f (%s)", cellData.getValue().getInStockAmount(), cellData.getValue().getUnitMeasure().getShortName())
                        );
                    }

                    return new SimpleStringProperty(
                            String.format("%.2f (%s)", cellData.getValue().getInStockAmount(), cellData.getValue().getUnitMeasure().getShortName())
                    );
                }
        );

        refreshBtn.setOnAction(event -> load());

        resetBtn.setOnAction(event -> {
            filterSearchInput.clear();
            filterCategoryCombo.getCheckModel().clearChecks();
            filterTagCombo.setValue(null);
            filterAmountBelowWarningAmount.setSelected(false);
            load();
        });

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

        load();
    }
}