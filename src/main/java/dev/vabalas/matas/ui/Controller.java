package dev.vabalas.matas.ui;

import dev.vabalas.matas.backend.service.NegateService;
import dev.vabalas.matas.model.SearchTermReportRow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
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
    private TableView<SearchTermReportRow> processedRowsTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        minClicksInput.setText("10");
    }

    @FXML
    void selectFile(ActionEvent event) {
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            fileSelectLabel.setText(selectedFile.getName());
            fileSelectButton.setDisable(true);
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
        var rows = negateService.extractInterestingRows(selectedFile, Integer.parseInt(minClicksInput.getText()));
        processedRowsTable.getItems().addAll(rows);
    }
}
