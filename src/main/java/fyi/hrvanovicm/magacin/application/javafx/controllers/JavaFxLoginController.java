package fyi.hrvanovicm.magacin.application.javafx.controllers;

import fyi.hrvanovicm.magacin.infrastructure.javafx.Router;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/views/auth/login.fxml")
public class JavaFxLoginController {
    @FXML
    private Button button;

    @Autowired
    Router router;

    private final FxWeaver fxWeaver;

    @Autowired
    public JavaFxLoginController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }


    @FXML
    public void initialize() {
        System.out.println("init");
        button.setOnAction((ActionEvent event) -> {
            this.router.navigateTo(ReportIndexController.class, controller -> {
                var con = (ReportIndexController) controller;
            });
        });
    }
}
