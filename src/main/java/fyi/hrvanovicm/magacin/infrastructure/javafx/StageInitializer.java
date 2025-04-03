package fyi.hrvanovicm.magacin.infrastructure.javafx;

import fyi.hrvanovicm.magacin.application.javafx.ProductIndexController;
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
        this.applicationTitle = "Test";
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        stage.setWidth(500);
        stage.setHeight(600);
    //    stage.setScene(new Scene(fxWeaver.loadView(JavaFxLoginController.class), 1200, 800));
        stage.setTitle(applicationTitle);
        stage.show();

        this.router.setPrimaryStage(stage);
        this.router.navigateTo(ProductIndexController.class);
    }
}
