package com.github.stachu540;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Buttons {
    public final ButtonType OK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    public final ButtonType CANCEL = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    public final ButtonType CLOSE = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
    public final ButtonType APPLY = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
    public final ButtonType SUBMIT = new ButtonType("Submit", ButtonBar.ButtonData.APPLY);
    public final ButtonType SAVE = new ButtonType("Save", ButtonBar.ButtonData.APPLY);
    public final ButtonType GOTO = new ButtonType("Go To", ButtonBar.ButtonData.OTHER);

    public final ButtonType YES = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    public final ButtonType NO = new ButtonType("No", ButtonBar.ButtonData.NO);

    public final ButtonType CREATE = new ButtonType("Create", ButtonBar.ButtonData.OTHER);
    public final ButtonType EDIT = new ButtonType("Edit", ButtonBar.ButtonData.OTHER);
    public final ButtonType RESET = new ButtonType("Reset", ButtonBar.ButtonData.OTHER);
    public final ButtonType BROWSE = new ButtonType("Browse", ButtonBar.ButtonData.OTHER);

    /**
     * Convert {@code {@linkplain ButtonType}} buttons to ordinal {@code {@linkplain Button}}
     * @param type the {@code {@linkplain ButtonType}}
     * @return converted to ordinal {@code {@linkplain Button}}
     */
    public Button convert(ButtonType type) {
        Button button = new Button(type.getText());
        button.setDefaultButton(type.getButtonData().isDefaultButton());
        button.setCancelButton(type.getButtonData().isCancelButton());
        return button;
    }
}
