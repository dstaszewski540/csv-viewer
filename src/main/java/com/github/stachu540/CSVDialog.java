package com.github.stachu540;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CSVDialog extends Dialog<CSVParser> {
    private final ReadOnlyObjectWrapper<File> file;
    private final BooleanProperty headers = new SimpleBooleanProperty(this, "headers", false);
    private final Property<Charsets> charset = new SimpleObjectProperty<>(this, "charset", Charsets.WINDOWS_1252);
    private final StringProperty separator = new SimpleStringProperty(this, "separator", ";");
    private final ListProperty<CSVRecord> data = new SimpleListProperty<>(this, "data", FXCollections.observableArrayList());
    private final MapProperty<Integer, String> headerNames = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final BooleanProperty reload = new SimpleBooleanProperty(false);

    public CSVDialog(File file) {
        this.file = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(file));

        DialogPane pane = getDialogPane();
        pane.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        pane.setContent(content());
        pane.setHeader(label());
        setOnCloseRequest(e -> {
            e.consume();
            this.hide();
        });

        headers.addListener(onChange());
        charset.addListener(onChange());
        separator.addListener(onChange());

        update();
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
        sep.textProperty().bindBidirectional(separator);
        sep.setPromptText(",");
        GridPane.setRowIndex(sep, 0);
        GridPane.setColumnIndex(sep, 1);
        // END: Separator

        // Charset
        // Label
        Label charLabel = new Label("Charset");
        GridPane.setRowIndex(charLabel, 1);
        GridPane.setColumnIndex(charLabel, 0);
        //NODE
        ChoiceBox<Charsets> ch = new ChoiceBox<>(FXCollections.observableArrayList(Charsets.values()));
        ch.valueProperty().bindBidirectional(charset);
        GridPane.setRowIndex(ch, 1);
        GridPane.setColumnIndex(ch, 1);
        // END: Charset

        // Headers
        // Label
        Label headerLabel = new Label("Has Headers");
        GridPane.setRowIndex(headerLabel, 2);
        GridPane.setColumnIndex(headerLabel, 0);
        // NODE
        CheckBox header = new CheckBox();
        header.selectedProperty().bindBidirectional(headers);
        GridPane.setRowIndex(header, 2);
        GridPane.setColumnIndex(header, 1);
        // END: Headers

        // Table
        TableView<CSVRecord> table = tableView();
        GridPane.setRowIndex(table, 3);
        GridPane.setColumnIndex(table, 0);
        GridPane.setColumnSpan(table, 2);
        GridPane.setMargin(table, new Insets(10));
        // END: Table

        pane.getChildren().addAll(sepLabel, sep, charLabel, ch, headerLabel, header, table);

        return pane;
    }

    private TableView<CSVRecord> tableView() {
        TableView<CSVRecord> table = new TableView<>();
        table.setEditable(false);
        headerNames.addListener((_, _, v) -> {
            List<TableColumn<CSVRecord, String>> cols = v.entrySet().stream().map(it -> {
                boolean noKey = it.getValue().isBlank();
                String key = (noKey) ? "COL_" + it.getKey() : it.getValue();
                TableColumn<CSVRecord, String> col = new TableColumn<>(key);
                col.setCellValueFactory(data -> {
                    CSVRecord record = data.getValue();
                    String value = (noKey) ? record.get(it.getKey()) : record.get(it.getValue());
                    return new SimpleStringProperty(value);
                });
                return col;
            }).toList();
            table.getColumns().setAll(cols);
        });
        table.itemsProperty().bindBidirectional(data);

        return table;
    }

    public MapProperty<Integer, String> headerNamesProperty() {
        return headerNames;
    }

    public ObservableMap<Integer, String> getHeaderNames() {
        return headerNamesProperty().get();
    }

    public ListProperty<CSVRecord> dataProperty() {
        return data;
    }

    public ObservableList<CSVRecord> getData() {
        return dataProperty().get();
    }

    private <T> ChangeListener<T> onChange() {
        reload.set(true);
        return (_, _, _) -> update();
    }

    private void update() {
        try (InputStream is = new FileInputStream(file.get())) {
            try (Reader r = new BufferedReader(new InputStreamReader(is, charset.getValue().io))) {
                CSVFormat.Builder builder = CSVFormat.DEFAULT.builder();
                if (this.separator.isNotEmpty().get()) {
                    builder.setDelimiter(this.separator.get());
                }
                try (CSVParser p = builder.build().parse(r)) {
                    List<CSVRecord> records = p.stream().toList();
                    System.out.println(records.size());
                    List<String> headers = Arrays.stream(records.getFirst().values()).toList();
                    if (this.headers.not().get()) {
                        int colNumber = headers.size();
                        headers = IntStream.range(0, colNumber)
                                .mapToObj(_ -> "")
                                .toList();
                        records = records.subList(1, records.size() - 1);
                    }
                    final List<String> fh = headers;
                    Map<Integer, String> headerMap = IntStream.range(0, headers.size())
                            .mapToObj(i -> new Pair<>(i, fh.get(i)))
                            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
                    this.headerNames.setValue(FXCollections.observableMap(headerMap));
                    this.data.setAll(records);
                    this.setResultConverter(b -> {
                        if (b != ButtonType.OK) {
                            return null;
                        }
                        return p;
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialogs.showError(e, getOwner());
        }
        reload.set(false);
    }
}
