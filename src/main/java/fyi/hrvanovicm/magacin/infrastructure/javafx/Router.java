package fyi.hrvanovicm.magacin.infrastructure.javafx;

import fyi.hrvanovicm.magacin.application.javafx.controllers.ScaffoldContoller;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class Router {
    private final FxWeaver fxWeaver;

    @Setter
    private Stage primaryStage;

    @Autowired
    public Router(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    public void navigateTo(Class<?> controller) {
        this.navigateTo(controller, null);
    }

    public void navigateTo(Class<?> controller, Consumer<Object> initState) {
        var fxController = fxWeaver.load(controller);
        var fxBaseController = fxWeaver.load(ScaffoldContoller.class);

        if(initState != null) {
            initState.accept(fxController.getController());
        }

        fxBaseController
                .getController()
                .main
                .setCenter(fxController.getView().get());

        Optional<Node> view = fxBaseController.getView();
        view.ifPresent(node -> {
            Parent root = (Parent) node;
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }
}
