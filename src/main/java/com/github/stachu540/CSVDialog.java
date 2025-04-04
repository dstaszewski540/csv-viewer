package com.github.stachu540;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.converter.CharacterStringConverter;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public class CSVDialog extends Dialog<ObservableList<Map<String, String>>> {
    private final ReadOnlyObjectWrapper<File> file;
    private final BooleanProperty headers = new SimpleBooleanProperty(this, "headers", false);
    private final Property<Charsets> charset = new SimpleObjectProperty<>(this, "charset", Charsets.getDefault());
    private final Property<Character> separator = new SimpleObjectProperty<>(this, "separator", ';');
    private final ListProperty<Map<String, String>> data = new SimpleListProperty<>(this, "data", FXCollections.emptyObservableList());
    private final BooleanProperty reload = new SimpleBooleanProperty(false);

    public CSVDialog(File file, Stage owner) {
        initOwner(owner);
        this.file = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(file));

        DialogPane pane = getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        pane.setContent(content());
        pane.setHeader(label());
        setTitle(((Label) pane.getHeader()).getText());

        reload.addListener((_, _, v) -> {
            if (v) {
                tableView().refresh();
            }
        });
        headers.addListener(onChange());
        charset.addListener(onChange());
        separator.addListener(onChange());
        setResultConverter(b -> {
            if (b == ButtonType.OK) {
                return data.get();
            }
            return null;
        });

        update();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void inputData(TableView<Map<String, String>> table, ObservableList<Map<String, String>> data) {
        table.getColumns().clear();
        data.getFirst().keySet().forEach((k) -> {
            TableColumn<Map<String, String>, String> col = new TableColumn<>(k);
            col.setCellValueFactory(new MapValueFactory(k));
            table.getColumns().add(col);
        });
    }

    private Label label() {
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setText("Setup Imported Parameters");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setPadding(new Insets(20, 10, 20, 10));
        return label;
    }

    private GridPane content() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(5);
        pane.getColumnConstraints().clear();
        pane.getRowConstraints().clear();
        ColumnConstraints left = new ColumnConstraints();
        left.setHalignment(HPos.RIGHT);
        left.setHgrow(Priority.SOMETIMES);
        ColumnConstraints right = new ColumnConstraints();
        right.setHalignment(HPos.LEFT);
        right.setHgrow(Priority.SOMETIMES);
        pane.getColumnConstraints().setAll(left, right);
        RowConstraints anyRow = new RowConstraints();
        anyRow.setVgrow(Priority.SOMETIMES);
        pane.getRowConstraints().setAll(anyRow, anyRow, anyRow, anyRow);
        pane.getChildren().clear();

        // Separator
        // Label
        Label sepLabel = new Label("Separator");
        GridPane.setRowIndex(sepLabel, 0);
        GridPane.setColumnIndex(sepLabel, 0);
        // NODE
        TextField sep = new TextField();
        sep.setTextFormatter(new TextFormatter<>(new CharacterStringConverter()));
        sep.textProperty().bindBidirectional(separator, new CharacterStringConverter());
        sep.setPromptText(separator.getValue().toString());
        GridPane.setRowIndex(sep, 0);
        GridPane.setColumnIndex(sep, 1);
        // END: Separator

        // Headers
        // Label
        Label headerLabel = new Label("Has Headers");
        GridPane.setRowIndex(headerLabel, 1);
        GridPane.setColumnIndex(headerLabel, 0);
        // NODE
        CheckBox header = new CheckBox();
        header.selectedProperty().bindBidirectional(headers);
        GridPane.setRowIndex(header, 1);
        GridPane.setColumnIndex(header, 1);
        // END: Headers

        // Charset
        // Label
        Label charLabel = new Label("Charset");
        GridPane.setRowIndex(charLabel, 2);
        GridPane.setColumnIndex(charLabel, 0);
        //NODE
        ChoiceBox<Charsets> ch = new ChoiceBox<>(FXCollections.observableArrayList(Charsets.values()));
        ch.valueProperty().bindBidirectional(charset);
        GridPane.setRowIndex(ch, 2);
        GridPane.setColumnIndex(ch, 1);
        // END: Charset

        // Table
        TableView<Map<String, String>> table = tableView();
        GridPane.setRowIndex(table, 3);
        GridPane.setColumnIndex(table, 0);
        GridPane.setColumnSpan(table, 2);
        GridPane.setMargin(table, new Insets(10));
        // END: Table

        pane.getChildren().addAll(sepLabel, sep, charLabel, ch, headerLabel, header, table);

        return pane;
    }

    private TableView<Map<String, String>> tableView() {
        TableView<Map<String, String>> table = new TableView<>();
        table.setEditable(false);
        data.addListener((_, _, l) -> {
            inputData(table, l);
        });

        table.itemsProperty().bindBidirectional(data);
        reload.addListener((_, _, r) -> {
            if (r) {
                table.refresh();
            }
        });
        return table;
    }

    public ListProperty<Map<String, String>> dataProperty() {
        return data;
    }

    public ObservableList<Map<String, String>> getData() {
        return dataProperty().get();
    }

    private <T> ChangeListener<T> onChange() {
        reload.set(true);
        return (_, _, _) -> update();
    }

    private void apply(boolean headers, char separator, Charset charset) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getValue()), charset))) {
            List<Map<String, String>> data = new ArrayList<>();
            List<String> lines = new LinkedList<>(br.lines().toList());
            String rawH = "";

            if (headers) {
                rawH = lines.getFirst();
                lines.removeFirst();
            }

            for (var l : lines) {
                data.add(line(rawH, separator, l));
            }
            this.data.setValue(FXCollections.observableList(data));
        } catch (Exception e) {
            Dialogs.showError(e, getOwner());
        }
    }

    @NonNull
    private Map<String, String> line(String rawH, char sep, String rawL) {
        Map<String, String> d = new LinkedHashMap<>();
        String[] h = {};
        String[] split = {rawL};
        if (sep != 0) {
            String ch = Character.toString(sep);
            split = rawL.split(ch);
            if (!rawH.isEmpty()) {
                h = rawH.split(ch);
            }
        } else {
            if (!rawH.isEmpty()) {
                h = new String[]{rawH};
            }
        }
        for (var i = 0; i < split.length; i++) {
            String hh;
            try {
                hh = h[i];
            } catch (ArrayIndexOutOfBoundsException _) {
                hh = "#COL_" + i;
            }
            d.put(hh, split[i]);
        }
        return d;
    }

    private void update() {
        boolean headers = this.headers.get();
        char sep = this.separator.getValue();
        Charset charset = this.charset.getValue().toCharset();
        apply(headers, sep, charset);
        reload.set(false);
    }
}
