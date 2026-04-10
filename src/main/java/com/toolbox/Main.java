package com.toolbox; // C'EST CETTE LIGNE QUI MANQUAIT !

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.toolbox.module5.Module5Controller; // LE CHEMIN EXACT DE TON MODULE 5

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Module5Controller module5 = new Module5Controller();
        Scene scene = new Scene(module5.getView(), 600, 700);

        primaryStage.setTitle("Data & IA Toolbox - Module 5");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}