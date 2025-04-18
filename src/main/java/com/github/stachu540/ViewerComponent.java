package com.github.stachu540;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class ViewerComponent implements Initializable {

    private final Stage stage;
    @FXML
    TableView<Map<String, String>> tableData;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Button btn = Buttons.convert(Buttons.BROWSE);
        btn.setOnAction(_ -> load());
        tableData.setPlaceholder(btn);
        tableData.setEditable(false);
    }

    @FXML
    void load() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter csv = new FileChooser.ExtensionFilter("CSV file", "*.csv");
        chooser.getExtensionFilters().setAll(csv);
        chooser.setSelectedExtensionFilter(csv);
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            CSVDialog dialog = new CSVDialog(file, stage);
            dialog.showAndWait().ifPresent(rtn -> {
                CSVDialog.inputData(tableData, rtn);
                tableData.setItems(rtn);
            });
        }
    }

//    public void sortBy(SortEvent<TableView<CsvRecord>> event) {
//        event.getSource()
//    }

    @FXML
    public void close() {
        stage.close();
    }

    @FXML
    public void about() {
        Alert about = Dialogs.about();
        about.initOwner(Objects.requireNonNull(stage, "Window owner is required"));
        about.show();
    }
}
