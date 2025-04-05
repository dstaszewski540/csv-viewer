package com.github.stachu540;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class CSVDialogTest extends ApplicationTest {

    private CSVDialog csvDialog;
    private File testFile;

    @BeforeEach
    void setUp() {
        Platform.runLater(() -> {
            try {
                testFile = new File(Objects.requireNonNull(getClass().getResource("/sample.csv")).toURI()); // Ensure this test file exists
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            assertTrue(testFile.exists(), "Test CSV file must exist!");
            csvDialog = new CSVDialog(testFile, null);
        });
    }

    @Test
    void testImportData() {
        Platform.runLater(() -> {
            csvDialog.setHeaders(true);
            ObservableList<Map<String, String>> data = csvDialog.getData();
            assertNotNull(data, "Imported data should not be null");
            assertFalse(data.isEmpty(), "Imported data should not be empty");

            // Check headers
            Map<String, String> firstRow = data.getFirst();
            assertTrue(firstRow.containsKey("Name"), "CSV must contain column 'Name'");
            assertTrue(firstRow.containsKey("Age"), "CSV must contain column 'Age'");

            // Check data consistency
            assertEquals("Alice", firstRow.get("Name"));
            assertEquals("30", firstRow.get("Age"));
        });
    }

    @Test
    void testSeparatorChange() {
        Platform.runLater(() -> {
            csvDialog.separatorProperty().setValue(';');
            ObservableList<Map<String, String>> data = csvDialog.getData();
            assertNotNull(data, "Data should not be null after separator change");
            assertFalse(data.isEmpty(), "Data should still be valid");
        });
    }

    @Test
    void testCharsetChange() {
        Platform.runLater(() -> {
            csvDialog.charsetProperty().setValue(Charsets.UTF_8);
            ObservableList<Map<String, String>> data = csvDialog.getData();
            assertNotNull(data, "Data should be correctly parsed with UTF-8");
        });
    }

    @Test
    void testNoHeadersMode() {
        Platform.runLater(() -> {
            ObservableList<Map<String, String>> data = csvDialog.getData();
            assertTrue(data.getFirst().containsKey("#COL_0"), "Without headers, columns should be auto-named");
        });
    }

    @Test
    void testTableViewIntegration() {
        Platform.runLater(() -> {
            TableView<Map<String, String>> tableView = new TableView<>();
            CSVDialog.inputData(tableView, csvDialog.getData());
            assertFalse(tableView.getColumns().isEmpty(), "TableView should have generated columns");
        });
    }
}
