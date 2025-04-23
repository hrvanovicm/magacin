package fyi.hrvanovicm.magacin.presentation.javafx.app;

import fyi.hrvanovicm.magacin.presentation.javafx.controllers.ProductIndexController;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    private final String applicationTitle;
    private final FxWeaver fxWeaver;

    @Autowired
    private Router router;

    public StageInitializer(FxWeaver fxWeaver) {
        this.applicationTitle = "Magacin";
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        stage.setWidth(1300);
        stage.setHeight(700);
        stage.setTitle(applicationTitle);
        stage.centerOnScreen();
        stage.show();

        this.router.setPrimaryStage(stage);
        this.router.navigateTo(ProductIndexController.class);
    }
}
