package com.github.stachu540;

import com.sun.javafx.reflect.ReflectUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class CSViewer extends Application {
    public static void main(String[] args) {
        Application.launch(CSViewer.class, args);
    }

    @Override
    public void start(Stage root) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((_, e) -> Dialogs.showError(e, root));
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("viewer.fxml"), "no file"));
        loader.setControllerFactory(type -> {
            if (type == ViewerComponent.class) {
                return new ViewerComponent(root);
            }

            ReflectUtil.checkPackageAccess(type);
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                return null;
            }
        });
        BorderPane pane = loader.load();
        Scene scene = new Scene(pane);

        root.setScene(scene);

        root.show();
    }
}
