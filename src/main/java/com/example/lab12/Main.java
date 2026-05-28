package com.example.lab12;

import com.example.lab12.controller.MainController;
import com.example.lab12.util.AppLogger;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        AppLogger.setup();
        MainController controller = new MainController();
        controller.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
