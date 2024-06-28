package dev.vabalas.matas.ui;

import dev.vabalas.matas.backend.service.NegateService;
import dev.vabalas.matas.model.SearchTermReportRow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {

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

    @FXML
    void selectFile(ActionEvent event) {
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            fileSelectLabel.setText(selectedFile.getName());
            fileSelectButton.setDisable(true);
            minClicksInput.setDisable(false);
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
