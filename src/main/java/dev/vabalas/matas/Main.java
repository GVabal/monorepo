package dev.vabalas.matas;

import dev.vabalas.matas.ui.App;

public class Main {

    // Log     : "WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module"
    // Solution: Add VM Option to runner -> --module-path "\path\to\javafx-sdk-22.0.1\lib" --add-modules javafx.controls,javafx.fxml
    public static void main(String[] args) {
        App.start(args);
    }
}
