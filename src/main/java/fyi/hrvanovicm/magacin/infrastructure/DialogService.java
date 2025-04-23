package fyi.hrvanovicm.magacin.infrastructure;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DialogService {
    public boolean showConfirmDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrda");
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType("Potvrdi");
        ButtonType noButton = new ButtonType("Otkaži");

        alert.getButtonTypes().setAll(yesButton, noButton);
        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == yesButton;
    }

    public void showErrorDialog() {
        this.showErrorDialog("Došlo je do greške!");
    }

    public void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greška");
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType confirmBtn = new ButtonType("Razumijem");

        alert.getButtonTypes().setAll(confirmBtn);
        alert.showAndWait();
    }
}
