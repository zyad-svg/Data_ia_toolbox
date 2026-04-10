package com.toolbox.module5;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Module5Controller {

    private String[] board = new String[9];
    private Button[] buttons = new Button[9];
    private boolean gameActive = true;

    private Label statusLabel;
    private Label statsLabel;
    private ComboBox<String> difficultyCombo;

    private GameDao dao;

    public VBox getView() {
        dao = new GameDao(); // Initialise la BDD

        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #F5F7FA;"); // Fond premium

        // Titre
        Label title = new Label("Tic-Tac-Toe vs IA");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");

        // Section Stats
        statsLabel = new Label();
        statsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");
        updateStatsDisplay();

        // Contrôles (Difficulté)
        difficultyCombo = new ComboBox<>();
        difficultyCombo.getItems().addAll("Niveau Facile", "Niveau Difficile");
        difficultyCombo.setValue("Niveau Facile");
        difficultyCombo.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-border-color: #D1D5DB; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        statusLabel = new Label("À toi de jouer (X) !");
        statusLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1D4ED8; -fx-font-weight: bold;");

        // La Grille dans une "Carte" blanche
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        for (int i = 0; i < 9; i++) {
            board[i] = "";
            Button btn = new Button("");
            btn.setPrefSize(100, 100);
            btn.setFont(new Font("Arial", 40));
            btn.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");

            final int index = i;
            btn.setOnAction(e -> handlePlayerClick(index));

            buttons[i] = btn;
            grid.add(btn, i % 3, i / 3);
        }

        // Bouton recommencer
        Button restartBtn = new Button("Nouvelle Partie");
        restartBtn.setStyle("-fx-background-color: #1D4ED8; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 6px; -fx-cursor: hand;");
        restartBtn.setOnAction(e -> resetGame());

        root.getChildren().addAll(title, statsLabel, difficultyCombo, statusLabel, grid, restartBtn);
        return root;
    }

    private void handlePlayerClick(int index) {
        if (!gameActive || !board[index].isEmpty()) return;

        // Coup du joueur
        makeMove(index, "X");

        if (checkGameState()) return;

        // Coup de l'IA
        boolean isHardMode = difficultyCombo.getValue().equals("Niveau Difficile");
        int aiMove = TicTacToeLogic.getComputerMove(board, isHardMode);

        if (aiMove != -1) {
            makeMove(aiMove, "O");
            checkGameState();
        }
    }

    private void makeMove(int index, String player) {
        board[index] = player;
        buttons[index].setText(player);
        if (player.equals("X")) {
            buttons[index].setStyle("-fx-background-color: white; -fx-text-fill: #1D4ED8; -fx-border-color: #93C5FD; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        } else {
            buttons[index].setStyle("-fx-background-color: white; -fx-text-fill: #4B5563; -fx-border-color: #D1D5DB; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        }
    }

    private boolean checkGameState() {
        String diff = difficultyCombo.getValue();
        if (TicTacToeLogic.checkWin(board, "X")) {
            endGame("Bravo, tu as gagné !", "#16A34A", diff, "Humain");
            return true;
        }
        if (TicTacToeLogic.checkWin(board, "O")) {
            endGame("L'IA a gagné !", "#DC2626", diff, "IA");
            return true;
        }
        if (TicTacToeLogic.isFull(board)) {
            endGame("Match Nul !", "#F59E0B", diff, "Nul");
            return true;
        }
        return false;
    }

    private void endGame(String message, String colorStr, String difficulty, String winner) {
        gameActive = false;
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + colorStr + ";");

        // Sauvegarde en Base de données
        dao.saveGame(difficulty, winner);
        // Met à jour l'affichage des scores
        updateStatsDisplay();
    }

    private void updateStatsDisplay() {
        int[] stats = dao.getStats();
        statsLabel.setText("Historique global ->  Victoires: " + stats[0] + " | Défaites: " + stats[1] + " | Nuls: " + stats[2]);
    }

    private void resetGame() {
        gameActive = true;
        statusLabel.setText("À toi de jouer (X) !");
        statusLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1D4ED8; -fx-font-weight: bold;");
        for (int i = 0; i < 9; i++) {
            board[i] = "";
            buttons[i].setText("");
            buttons[i].setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        }
    }
}