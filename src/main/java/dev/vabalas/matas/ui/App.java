package dev.vabalas.matas.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    public static void start(String[] args) {
        launch();
    }

    @Override
    public void start(Stage doNotUse) throws IOException {
        Stage primaryStage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/matas.fxml")));
        primaryStage.show();
    }
}
