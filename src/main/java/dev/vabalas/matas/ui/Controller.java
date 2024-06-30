package dev.vabalas.matas.ui;

import dev.vabalas.matas.backend.service.NegateService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final NegateService negateService = new NegateService();
    private final FileChooser fileChooser = new FileChooser();

    private File selectedFile;

    @FXML
    private Button fileSelectButton;
    @FXML
    private Label fileSelectLabel;
    @FXML
    private TextField minClicksInput;
    @FXML
    private Button processButton;
    @FXML
    private ProgressBar progressIndicator;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        minClicksInput.setText("10");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("*.xlsx", "xlsx"));
        var currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
    }

    @FXML
    void selectFile(ActionEvent event) {
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            fileSelectLabel.setText(selectedFile.getName());
            minClicksInput.setDisable(false);
            processButton.setDisable(false);
        } else {
            minClicksInput.setDisable(true);
            processButton.setDisable(true);
        }
    }

    @FXML
    void validateMinClicksInput(KeyEvent event) {
        try {
            Integer.valueOf(minClicksInput.getText());
            if (selectedFile != null) {
                processButton.setDisable(false);
            }
        } catch (Exception e) {
            processButton.setDisable(true);
        }
    }

    @FXML
    void processFile(ActionEvent event) {
        processButton.setDisable(true);
        progressIndicator.setProgress(-1);

        new Thread(() -> {{
            var interestingRows = negateService.extractInterestingRows(selectedFile, Integer.parseInt(minClicksInput.getText()));
            var reportBytes = negateService.generateReport(interestingRows);
            var outputPath = selectedFile.getPath().replace(selectedFile.getName(), "upload-%d.xlsx".formatted(System.currentTimeMillis()));
            try {
                Files.write(Path.of(outputPath), reportBytes);
            } catch (IOException e) {
                Platform.runLater(() -> {
                    processButton.setDisable(false);
                    progressIndicator.setProgress(0);
                });
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                processButton.setDisable(false);
                progressIndicator.setProgress(0);
            });
        }}).start();
    }
}
