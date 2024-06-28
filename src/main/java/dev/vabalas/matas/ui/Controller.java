package dev.vabalas.matas.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;

public class Controller {

    @FXML
    private ImageView racoonImage;

    @FXML
    private ToggleButton showHideButton;

    @FXML
    void toggleRacoonImage(ActionEvent event) {
        showHideButton.setText(racoonImage.isVisible() ? "Show" : "Hide");
        racoonImage.setVisible(!racoonImage.isVisible());
    }
}
