package com.github.stachu540;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@UtilityClass
public class Dialogs {

    public void showError(Throwable ex, Window owner) {
        showError(ex, owner, true);
    }

    public void showError(Throwable ex, Window owner, boolean stacktrace) {
        error(ex, owner, stacktrace).showAndWait();
    }

    public Alert error(Throwable ex, Window owner) {
        return error(ex, owner, true);
    }

    public Alert error(Throwable ex, Window owner, boolean stacktrace) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Details");
        alert.setHeaderText(ex.getLocalizedMessage());
        if (stacktrace) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();

            DialogPane pane = alert.getDialogPane();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane root = new GridPane();
            root.setMaxWidth(Double.MAX_VALUE);
            root.add(label, 0, 0);
            root.add(textArea, 0, 1);

            pane.setExpandableContent(root);
        }
        alert.initOwner(Objects.requireNonNull(owner, "Window owner is required"));
        alert.getButtonTypes().setAll(Buttons.OK);

        return alert;
    }

    public Alert question(String title, String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(question);
        alert.getButtonTypes().setAll(Buttons.YES, Buttons.NO);

        return alert;
    }

    public Alert about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(version());
        alert.setContentText(text());
        alert.getButtonTypes().setAll(Buttons.OK);

        return alert;
    }

    private String version() {
        return "CSViewer %s".formatted(manifestVersion());
    }

    private String manifestVersion() {
        try (InputStream is = Dialogs.class.getModule().getResourceAsStream("/META-INF/MANIFEST.MF")) {
            Manifest manifest = new Manifest(is);
            String version = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            return (version != null) ? "v" + version : "<undefined>";
        } catch (IOException e) {
            return "<undefined>";
        }
    }

    private String text() {
        return "Copyright 2024 © Damian Staszewski.";
    }
}
