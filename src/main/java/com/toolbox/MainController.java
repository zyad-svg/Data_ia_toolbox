package com.toolbox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button btn1;
    @FXML private Button btn2;
    @FXML private Button btn3;
    @FXML private Button btn4;
    @FXML private Button btn5;
    @FXML private Button btn6;

    private List<Button> navButtons;

    @FXML
    public void initialize() {
        navButtons = List.of(btn1, btn2, btn3, btn4, btn5, btn6);
        showWelcome();
    }

    private void showWelcome() {
        Label title = new Label("Bienvenue dans la Data & IA Toolbox");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Sélectionnez un module dans le menu");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        VBox welcome = new VBox(12, title, subtitle);
        welcome.setAlignment(Pos.CENTER);
        contentArea.getChildren().setAll(welcome);
    }

    @FXML private void onModule1() { loadModule(1); setActive(btn1); }
    @FXML private void onModule2() { loadModule(2); setActive(btn2); }
    @FXML private void onModule3() { loadModule(3); setActive(btn3); }
    @FXML private void onModule4() { loadModule(4); setActive(btn4); }
    @FXML private void onModule5() { loadModule(5); setActive(btn5); }
    @FXML private void onModule6() { loadModule(6); setActive(btn6); }

    private void loadModule(int index) {
        try {
            String fxmlPath = "/com/toolbox/module" + index + "/Module" + index + "View.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Node view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActive(Button active) {
        for (Button btn : navButtons) {
            btn.getStyleClass().remove("nav-active");
        }
        active.getStyleClass().add("nav-active");
    }
}
