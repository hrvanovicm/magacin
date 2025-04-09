package fyi.hrvanovicm.magacin.application.javafx.controllers;

import fyi.hrvanovicm.magacin.infrastructure.javafx.Router;
import fyi.hrvanovicm.magacin.infrastructure.notification.NotificationEvent;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
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
    public MenuItem menuProductsBtn;

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
