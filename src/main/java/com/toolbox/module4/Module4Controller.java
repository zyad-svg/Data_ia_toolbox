package com.toolbox.module4;

import com.toolbox.database.DatabaseManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class Module4Controller implements Initializable {

    @FXML private ScrollPane chatScrollPane;
    @FXML private VBox messagesContainer;
    @FXML private TextField messageInput;

    private HBox typingRow;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static final String[][] KNOWLEDGE = Module4Knowledge.KNOWLEDGE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDatabase();
        addBotMessage("Bonjour ! Je suis votre assistant virtuel. Comment puis-je vous aider ?");
    }

    private void initDatabase() {
        try {
            Connection conn = DatabaseManager.getConnection();
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS knowledge(id INTEGER PRIMARY KEY, keyword TEXT UNIQUE, response TEXT)");
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS unanswered(id INTEGER PRIMARY KEY, question TEXT, date TEXT)");
            PreparedStatement ps = conn.prepareStatement(
                "INSERT OR IGNORE INTO knowledge(keyword, response) VALUES(?, ?)");
            for (String[] e : KNOWLEDGE) { ps.setString(1, e[0]); ps.setString(2, e[1]); ps.executeUpdate(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private String normalize(String text) {
        String n = Normalizer.normalize(text, Normalizer.Form.NFD);
        n = Pattern.compile("[\\p{InCombiningDiacriticalMarks}]").matcher(n).replaceAll("");
        return n.toLowerCase().replaceAll("[^a-z\\s]", " ");
    }

    private String analyzeMessage(String userMessage) {
        String msg = normalize(userMessage);

        if (msg.matches(".*(bonjour|salut|hello|bonsoir|coucou).*"))
            return "Bonjour ! Je suis votre assistant virtuel. Comment puis-je vous aider aujourd'hui ?";
        if (msg.matches(".*(au revoir|aurevoir|bye|bonne journee|bonne nuit|a bientot).*"))
            return "Au revoir ! N'hésitez pas à revenir si vous avez d'autres questions. Bonne journée !";

        try {
            Connection conn = DatabaseManager.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT keyword, response FROM knowledge");
            List<String[]> matches = new ArrayList<>();

            while (rs.next()) {
                String kw = normalize(rs.getString("keyword"));
                if (msg.contains(kw)) matches.add(new String[]{kw, rs.getString("response")});
            }

            if (matches.isEmpty()) {
                String[] words = msg.trim().split("\\s+");
                rs = conn.createStatement().executeQuery("SELECT keyword, response FROM knowledge");
                while (rs.next()) {
                    String kw = normalize(rs.getString("keyword"));
                    for (String w : words) {
                        if (w.length() > 3 && (w.contains(kw) || kw.contains(w))) {
                            matches.add(new String[]{kw, rs.getString("response")});
                            break;
                        }
                    }
                }
            }

            if (matches.isEmpty()) {
                PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO unanswered(question, date) VALUES(?, ?)");
                ins.setString(1, userMessage);
                ins.setString(2, LocalDate.now().toString());
                ins.executeUpdate();
                return "Je n'ai pas pu identifier votre demande. Votre question a été transmise à notre équipe qui vous répondra sous 24h ouvrées.";
            }

            matches.sort((a, b) -> Integer.compare(b[0].length(), a[0].length()));
            if (matches.size() >= 2 && !matches.get(0)[1].equals(matches.get(1)[1]))
                return matches.get(0)[1] + "\n\n" + matches.get(1)[1];
            return matches.get(0)[1];

        } catch (SQLException e) {
            e.printStackTrace();
            return "Une erreur technique est survenue. Veuillez réessayer ou contacter notre service client.";
        }
    }

    @FXML
    private void sendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;
        addUserMessage(text);
        messageInput.clear();
        showTypingIndicator();
        new Timeline(new KeyFrame(Duration.millis(800), e -> {
            hideTypingIndicator();
            addBotMessage(analyzeMessage(text));
        })).play();
    }

    private void showTypingIndicator() {
        Label lbl = new Label("En train d'écrire...");
        lbl.setStyle("-fx-font-style: italic; -fx-text-fill: #9CA3AF; -fx-font-size: 12px;");
        typingRow = new HBox(lbl);
        typingRow.setAlignment(Pos.CENTER_LEFT);
        typingRow.setPadding(new Insets(2, 4, 2, 4));
        messagesContainer.getChildren().add(typingRow);
        scrollToBottom();
    }

    private void hideTypingIndicator() {
        if (typingRow != null) { messagesContainer.getChildren().remove(typingRow); typingRow = null; }
    }

    private void addUserMessage(String text) {
        Label bubble = new Label(text);
        bubble.getStyleClass().add("chat-bubble-user");
        bubble.setWrapText(true); bubble.setMaxWidth(420);
        Label ts = new Label(LocalTime.now().format(TIME_FMT));
        ts.setStyle("-fx-font-size: 10px; -fx-text-fill: #9CA3AF;");
        VBox content = new VBox(2, bubble, ts); content.setAlignment(Pos.CENTER_RIGHT);
        HBox row = new HBox(content); row.setAlignment(Pos.CENTER_RIGHT);
        row.setPadding(new Insets(2, 4, 2, 4));
        messagesContainer.getChildren().add(row);
        scrollToBottom();
    }

    private void addBotMessage(String text) {
        Label bubble = new Label(text);
        bubble.getStyleClass().add("chat-bubble-bot");
        bubble.setWrapText(true); bubble.setMaxWidth(420);
        Label ts = new Label(LocalTime.now().format(TIME_FMT));
        ts.setStyle("-fx-font-size: 10px; -fx-text-fill: #9CA3AF;");
        VBox content = new VBox(2, bubble, ts); content.setAlignment(Pos.CENTER_LEFT);
        HBox row = new HBox(content); row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 4, 2, 4));
        messagesContainer.getChildren().add(row);
        scrollToBottom();
    }

    @FXML
    private void clearConversation() {
        messagesContainer.getChildren().clear();
        addBotMessage("Conversation effacée. Comment puis-je vous aider ?");
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            chatScrollPane.applyCss(); chatScrollPane.layout(); chatScrollPane.setVvalue(1.0);
        });
    }

    @FXML
    private void showUnanswered() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Questions sans réponse");

        TableView<UnansweredRow> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        TableColumn<UnansweredRow, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date")); colDate.setPrefWidth(100);
        TableColumn<UnansweredRow, String> colQ = new TableColumn<>("Question");
        colQ.setCellValueFactory(new PropertyValueFactory<>("question"));
        table.getColumns().addAll(List.of(colDate, colQ));

        try {
            ResultSet rs = DatabaseManager.getConnection().createStatement()
                .executeQuery("SELECT question, date FROM unanswered ORDER BY id DESC");
            while (rs.next())
                table.getItems().add(new UnansweredRow(rs.getString("date"), rs.getString("question")));
        } catch (SQLException e) { e.printStackTrace(); }

        Label title = new Label("Questions sans réponse");
        title.getStyleClass().add("page-title"); title.setPadding(new Insets(0, 0, 12, 0));
        VBox root = new VBox(12, title, table); root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F5F7;");
        VBox.setVgrow(table, Priority.ALWAYS);
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(
            getClass().getResource("/com/toolbox/module4/module4.css").toExternalForm());
        popup.setScene(scene); popup.show();
    }

    public static class UnansweredRow {
        private final String date, question;
        public UnansweredRow(String d, String q) { date = d; question = q; }
        public String getDate()     { return date; }
        public String getQuestion() { return question; }
    }
}
