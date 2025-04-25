package fyi.hrvanovicm.magacin.presentation.javafx.app;

import fyi.hrvanovicm.magacin.presentation.javafx.controllers.AutoLoadController;
import fyi.hrvanovicm.magacin.presentation.javafx.controllers.ScaffoldContoller;
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
    private double width;

    @Setter
    private double height;

    @Setter
    private Stage primaryStage;

    @Autowired
    public Router(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    public <C> void navigateTo(Class<C> controller) {
        this.navigateTo(controller, null);
    }

    public <C> void navigateTo(Class<C> controller, Consumer<C> initState) {
        var fxController = fxWeaver.load(controller);
        var fxBaseController = fxWeaver.load(ScaffoldContoller.class);

        if(initState != null) {
            initState.accept(fxController.getController());
        } else if(fxController.getController() instanceof AutoLoadController) {
            ((AutoLoadController) fxController.getController()).load();
        }

        fxBaseController
                .getController()
                .main
                .setCenter(fxController.getView().get());

        Optional<Node> view = fxBaseController.getView();
        view.ifPresent(node -> {
            Parent root = (Parent) node;

            var selectedWidth = width;
            var selectedHeight = height;
            Scene scene = new Scene(root, width, height);

            scene.getRoot().setStyle(
                    String.format("-fx-width: %f; -fx-height: %f;", width, height)
            );

            primaryStage.setScene(scene);
            primaryStage.sizeToScene();

            width = selectedWidth;
            height = selectedHeight;
        });
    }
}
