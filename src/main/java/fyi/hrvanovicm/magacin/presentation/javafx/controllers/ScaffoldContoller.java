package fyi.hrvanovicm.magacin.presentation.javafx.controllers;

import fyi.hrvanovicm.magacin.domain.products.ProductCategory;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import fyi.hrvanovicm.magacin.presentation.javafx.app.Router;
import fyi.hrvanovicm.magacin.infrastructure.notification.NotificationEvent;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.NotificationPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/views/scaffold.fxml")
public class ScaffoldContoller {
    /**
     * JavaFX relationships.
     */
    @FXML
    NotificationPane notificationPane;

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
    public MenuItem menuUnitMeasureBtn;

    /**
     * Services.
     */
    Router router;

    // State
    private PauseTransition pauseTransition;

    @Autowired
    public ScaffoldContoller(Router router) {
        this.router = router;
    }

    public void initialize() {
        this.menuProductBtn.setOnAction(e -> {
           this.router.navigateTo(ProductIndexController.class, (controller) -> {
               controller.load(ProductCategory.PRODUCT);
           });
        });

        this.menuCommercialProductBtn.setOnAction(e -> {
            this.router.navigateTo(ProductIndexController.class, (controller) -> {
                controller.load(ProductCategory.COMMERCIAL);
            });
        });

        this.menuRawMaterialProductBtn.setOnAction(e -> {
            this.router.navigateTo(ProductIndexController.class, (controller) -> {
                controller.load(ProductCategory.RAW_MATERIAL);
            });
        });

        this.menuReceiptReportBtn.setOnAction(e -> {
            this.router.navigateTo(ReportIndexController.class, (controller) -> {
                controller.load(ReportType.RECEIPT);
            });
        });

        this.menuShipmentReportBtn.setOnAction(e -> {
            this.router.navigateTo(ReportIndexController.class, (controller) -> {
                controller.load(ReportType.SHIPMENT);
            });
        });

        this.menuUnitMeasureBtn.setOnAction(e -> {
            this.router.navigateTo(UnitMeasureIndexController.class);
        });
    }

    @EventListener
    public void onNewNotification(NotificationEvent notification) {
        this.notificationPane.setText(notification.getMessage());
        this.notificationPane.setVisible(true);
        this.notificationPane.show();

        if(pauseTransition != null) {
            pauseTransition.stop();
        }

        pauseTransition = new PauseTransition(notification.getDuration());
        pauseTransition.setOnFinished(e -> { this.notificationPane.hide(); });
        pauseTransition.play();
    }
}
