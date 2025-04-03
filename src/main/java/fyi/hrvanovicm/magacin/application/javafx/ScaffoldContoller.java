package fyi.hrvanovicm.magacin.application.javafx;

import fyi.hrvanovicm.magacin.infrastructure.javafx.Router;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/views/scaffold.fxml")
public class ScaffoldContoller {
    /**
     * JavaFX relationships.
     */
    @FXML
    public BorderPane main;

    @FXML
    public MenuItem menuProductBtn;

    @FXML
    public MenuItem menuCommercialProductBtn;

    @FXML
    public MenuItem menuRawMaterialProductBtn;

    @FXML
    public MenuItem menuReceiptReportBtn;

    @FXML
    public MenuItem menuShipmentReportBtn;

    @FXML
    public MenuItem menuProductsBtn;

    /**
     * Services.
     */
    Router router;

    @Autowired
    public ScaffoldContoller(Router router) {
        this.router = router;
    }

    public void initialize() {
        this.menuProductBtn.setOnAction(e -> {
           this.router.navigateTo(ProductIndexController.class);
        });

        this.menuCommercialProductBtn.setOnAction(e -> {
            this.router.navigateTo(ProductIndexController.class);
        });

        this.menuRawMaterialProductBtn.setOnAction(e -> {
            this.router.navigateTo(ProductIndexController.class);
        });

        this.menuReceiptReportBtn.setOnAction(e -> {
            this.router.navigateTo(ReportIndexController.class);
        });

        this.menuShipmentReportBtn.setOnAction(e -> {
            this.router.navigateTo(ReportIndexController.class);
        });
    }
}
