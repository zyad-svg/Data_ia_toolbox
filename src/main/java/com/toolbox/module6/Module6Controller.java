package com.toolbox.module6;

import com.toolbox.database.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Module6Controller implements Initializable {

    @FXML private Slider ageSlider, revenusSlider, chargesSlider;
    @FXML private Label ageValueLbl, revenusValueLbl, chargesValueLbl;
    @FXML private ComboBox<String> situationCombo, historyCombo;
    @FXML private Label scoreLabel, decisionBadge, decisionMessage;
    @FXML private ProgressBar scoreBar;
    @FXML private VBox detailsContainer;
    @FXML private TableView<ProfileRow> historyTable;
    @FXML private TableColumn<ProfileRow, String> colAge, colRevenus, colScore, colDecision, colDate;

    private int currentScore = 0;
    private String currentDecision = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDatabase();

        situationCombo.getItems().addAll("CDI", "CDD", "Independant", "Sans emploi", "Retraite");
        situationCombo.setValue("CDI");
        historyCombo.getItems().addAll("Excellent", "Bon", "Moyen", "Mauvais");
        historyCombo.setValue("Bon");

        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colRevenus.setCellValueFactory(new PropertyValueFactory<>("revenus"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colDecision.setCellValueFactory(new PropertyValueFactory<>("decision"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        colDecision.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                String color = switch (item) {
                    case "ACCEPTE" -> "#22C55E";
                    case "EXAMINE" -> "#F59E0B";
                    case "RISQUE"  -> "#EF4444";
                    case "REFUSE"  -> "#DC2626";
                    default -> "#6B7280";
                };
                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            }
        });

        ageSlider.valueProperty().addListener((o, ov, nv) -> {
            ageValueLbl.setText((int) nv.doubleValue() + " ans");
            recompute();
        });
        revenusSlider.valueProperty().addListener((o, ov, nv) -> {
            revenusValueLbl.setText((int) nv.doubleValue() + " EUR/mois");
            recompute();
        });
        chargesSlider.valueProperty().addListener((o, ov, nv) -> {
            chargesValueLbl.setText((int) nv.doubleValue() + " EUR/mois");
            recompute();
        });
        situationCombo.valueProperty().addListener((o, ov, nv) -> recompute());
        historyCombo.valueProperty().addListener((o, ov, nv) -> recompute());

        ageValueLbl.setText((int) ageSlider.getValue() + " ans");
        revenusValueLbl.setText((int) revenusSlider.getValue() + " EUR/mois");
        chargesValueLbl.setText((int) chargesSlider.getValue() + " EUR/mois");

        recompute();
        loadHistory();
    }

    private void initDatabase() {
        try (Statement st = DatabaseManager.getConnection().createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS profiles(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "age INTEGER, revenus REAL, score INTEGER, decision TEXT, date TEXT)");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /* ===== Scoring ===== */

    private int scoreAge(int age) {
        if (age < 18) return 0;
        if (age <= 25) return 5;
        if (age <= 35) return 15;
        if (age <= 50) return 20;
        if (age <= 65) return 15;
        return 5;
    }

    private int scoreRevenus(double r) {
        if (r < 1000) return 0;
        if (r < 2000) return 10;
        if (r < 3000) return 20;
        if (r < 5000) return 25;
        return 30;
    }

    private int scoreSituation(String s) {
        if (s == null) return 0;
        return switch (s) {
            case "CDI" -> 20;
            case "CDD" -> 10;
            case "Independant" -> 15;
            case "Retraite" -> 12;
            default -> 0;
        };
    }

    private int scoreHistory(String h) {
        if (h == null) return 0;
        return switch (h) {
            case "Excellent" -> 15;
            case "Bon" -> 10;
            case "Moyen" -> 5;
            default -> 0;
        };
    }

    private int scoreCharges(double revenus, double charges) {
        if (revenus <= 0) return 0;
        double ratio = charges / revenus;
        if (ratio < 0.2) return 10;
        if (ratio <= 0.4) return 5;
        return 0;
    }

    private int calculateScore() {
        int age = (int) ageSlider.getValue();
        if (age < 18) return 0;
        double revenus = revenusSlider.getValue();
        double charges = chargesSlider.getValue();
        int total = scoreAge(age)
                  + scoreRevenus(revenus)
                  + scoreSituation(situationCombo.getValue())
                  + scoreHistory(historyCombo.getValue())
                  + scoreCharges(revenus, charges);
        return Math.min(100, total);
    }

    /* ===== UI update ===== */

    private void recompute() {
        currentScore = calculateScore();
        updateUI(currentScore);
    }

    private void updateUI(int score) {
        scoreLabel.setText(score + "/100");
        scoreBar.setProgress(score / 100.0);

        scoreBar.getStyleClass().removeAll("gauge-accepted", "gauge-review", "gauge-risky", "gauge-refused");
        decisionBadge.getStyleClass().removeAll("badge-green", "badge-orange", "badge-red-light", "badge-red");

        String decision, message, gaugeClass, badgeClass;
        if (score >= 70) {
            decision = "ACCEPTE"; message = "Profil excellent, pret accorde";
            gaugeClass = "gauge-accepted"; badgeClass = "badge-green";
        } else if (score >= 50) {
            decision = "EXAMINE"; message = "Profil correct, examen approfondi requis";
            gaugeClass = "gauge-review"; badgeClass = "badge-orange";
        } else if (score >= 30) {
            decision = "RISQUE"; message = "Profil risque, garanties supplementaires requises";
            gaugeClass = "gauge-risky"; badgeClass = "badge-red-light";
        } else {
            decision = "REFUSE"; message = "Profil insuffisant, pret refuse";
            gaugeClass = "gauge-refused"; badgeClass = "badge-red";
        }
        currentDecision = decision;
        scoreBar.getStyleClass().add(gaugeClass);
        decisionBadge.setText(decision);
        decisionBadge.getStyleClass().add(badgeClass);
        decisionMessage.setText(message);

        detailsContainer.getChildren().clear();
        int age = (int) ageSlider.getValue();
        double rev = revenusSlider.getValue();
        double ch  = chargesSlider.getValue();
        addDetail("Age (" + age + " ans)", scoreAge(age));
        addDetail("Revenus (" + (int) rev + " EUR)", scoreRevenus(rev));
        addDetail("Situation : " + situationCombo.getValue(), scoreSituation(situationCombo.getValue()));
        addDetail("Historique : " + historyCombo.getValue(), scoreHistory(historyCombo.getValue()));
        addDetail("Ratio charges/revenus", scoreCharges(rev, ch));
    }

    private void addDetail(String label, int points) {
        Label l = new Label("+ " + points + " pts   " + label);
        l.getStyleClass().add("detail-line");
        detailsContainer.getChildren().add(l);
    }

    /* ===== Handlers ===== */

    @FXML
    private void onCalculate() {
        recompute();
    }

    @FXML
    private void onSave() {
        int age = (int) ageSlider.getValue();
        double revenus = revenusSlider.getValue();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(
                "INSERT INTO profiles(age, revenus, score, decision, date) VALUES(?, ?, ?, ?, ?)")) {
            ps.setInt(1, age);
            ps.setDouble(2, revenus);
            ps.setInt(3, currentScore);
            ps.setString(4, currentDecision);
            ps.setString(5, date);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        loadHistory();
    }

    private void loadHistory() {
        ObservableList<ProfileRow> rows = FXCollections.observableArrayList();
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT age, revenus, score, decision, date FROM profiles ORDER BY id DESC LIMIT 10")) {
            while (rs.next()) {
                rows.add(new ProfileRow(
                    rs.getInt("age") + " ans",
                    ((int) rs.getDouble("revenus")) + " EUR",
                    rs.getInt("score") + "/100",
                    rs.getString("decision"),
                    rs.getString("date")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        historyTable.setItems(rows);
    }

    public static class ProfileRow {
        private final String age, revenus, score, decision, date;
        public ProfileRow(String age, String revenus, String score, String decision, String date) {
            this.age = age; this.revenus = revenus; this.score = score;
            this.decision = decision; this.date = date;
        }
        public String getAge()      { return age; }
        public String getRevenus()  { return revenus; }
        public String getScore()    { return score; }
        public String getDecision() { return decision; }
        public String getDate()     { return date; }
    }
}
