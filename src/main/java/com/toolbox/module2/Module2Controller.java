package com.toolbox.module2;

import com.toolbox.database.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class Module2Controller implements Initializable {

    @FXML private Label statusLbl;
    @FXML private Label tempAvg, tempMin, tempMax, tempStd;
    @FXML private Label humAvg, humMin, humMax, humStd;
    @FXML private Label windAvg, windMin, windMax, windStd;
    @FXML private Label anomalyCount;
    @FXML private TableView<MeteoData> meteoTable;
    @FXML private TableColumn<MeteoData, String> colDate, colDesc;
    @FXML private TableColumn<MeteoData, Double> colTemp, colHum, colWind;

    private final Set<Integer> anomalyIndices = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDatabase();
        setupTable();
        refreshTable();
    }

    private void initDatabase() {
        try {
            Connection c = DatabaseManager.getConnection();
            c.createStatement().execute("CREATE TABLE IF NOT EXISTS meteo(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT UNIQUE, " +
                "temperature REAL, humidity REAL, wind_speed REAL, description TEXT)");

            String[] descs = {"Ensoleille","Nuageux","Pluvieux","Neige","Brouillard","Orageux","Couvert"};
            Random rnd = new Random(42);
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT OR IGNORE INTO meteo(date, temperature, humidity, wind_speed, description) VALUES(?, ?, ?, ?, ?)")) {
                for (int i = 1; i <= 30; i++) {
                    String date = String.format("2025-01-%02d", i);
                    double temp = Math.round((-5 + rnd.nextDouble() * 20) * 10) / 10.0;
                    double hum  = Math.round(40 + rnd.nextDouble() * 55);
                    double wind = Math.round(rnd.nextDouble() * 80);
                    // Injecte quelques anomalies pour la demo
                    if (i == 7)  temp = -18.0;
                    if (i == 14) wind = 120.0;
                    if (i == 22) hum  = 15.0;
                    ps.setString(1, date);
                    ps.setDouble(2, temp);
                    ps.setDouble(3, hum);
                    ps.setDouble(4, wind);
                    ps.setString(5, descs[rnd.nextInt(descs.length)]);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void setupTable() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTemp.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        colHum.setCellValueFactory(new PropertyValueFactory<>("humidity"));
        colWind.setCellValueFactory(new PropertyValueFactory<>("windSpeed"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        meteoTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(MeteoData item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("anomaly-row");
                if (item != null && !empty && anomalyIndices.contains(getIndex())) {
                    getStyleClass().add("anomaly-row");
                }
            }
        });
    }

    @FXML
    private void onLoadCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir un fichier CSV meteo");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        File file = fc.showOpenDialog(meteoTable.getScene().getWindow());
        if (file == null) return;

        int imported = parseCsv(file);
        statusLbl.setText(imported + " lignes importees");
        statusLbl.setStyle("-fx-text-fill: #16A34A; -fx-font-size: 13px;");
        refreshTable();
    }

    @FXML
    private void onDemo() {
        statusLbl.setText("Donnees de demonstration chargees");
        statusLbl.setStyle("-fx-text-fill: #1E3A5F; -fx-font-size: 13px;");
        refreshTable();
    }

    private int parseCsv(File file) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
             PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(
                "INSERT OR IGNORE INTO meteo(date, temperature, humidity, wind_speed, description) VALUES(?, ?, ?, ?, ?)")) {

            String header = br.readLine();
            if (header != null && header.startsWith("\uFEFF")) header = header.substring(1);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    String[] parts = splitCsvLine(line);
                    if (parts.length < 5) continue;
                    ps.setString(1, parts[0].trim());
                    ps.setDouble(2, Double.parseDouble(parts[1].trim()));
                    ps.setDouble(3, Double.parseDouble(parts[2].trim()));
                    ps.setDouble(4, Double.parseDouble(parts[3].trim()));
                    ps.setString(5, parts[4].trim());
                    ps.executeUpdate();
                    count++;
                } catch (Exception ignored) {}
            }
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    private String[] splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuote) {
                if (c == '"') inQuote = false;
                else sb.append(c);
            } else {
                if (c == '"') inQuote = true;
                else if (c == ',') { fields.add(sb.toString()); sb.setLength(0); }
                else sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    private void refreshTable() {
        List<MeteoData> data = loadData();

        double[] tempArr = data.stream().mapToDouble(d -> d.temperature).toArray();
        double[] humArr  = data.stream().mapToDouble(d -> d.humidity).toArray();
        double[] windArr = data.stream().mapToDouble(d -> d.windSpeed).toArray();

        double tAvg = avg(tempArr), tStd = std(tempArr, tAvg);
        double hAvg = avg(humArr),  hStd = std(humArr, hAvg);
        double wAvg = avg(windArr), wStd = std(windArr, wAvg);

        tempAvg.setText(fmt(tAvg) + " C");
        tempMin.setText(fmt(min(tempArr)) + " C");
        tempMax.setText(fmt(max(tempArr)) + " C");
        tempStd.setText(fmt(tStd));
        humAvg.setText(fmt(hAvg) + " %");
        humMin.setText(fmt(min(humArr)) + " %");
        humMax.setText(fmt(max(humArr)) + " %");
        humStd.setText(fmt(hStd));
        windAvg.setText(fmt(wAvg) + " km/h");
        windMin.setText(fmt(min(windArr)) + " km/h");
        windMax.setText(fmt(max(windArr)) + " km/h");
        windStd.setText(fmt(wStd));

        anomalyIndices.clear();
        for (int i = 0; i < data.size(); i++) {
            MeteoData d = data.get(i);
            if (Math.abs(d.temperature - tAvg) > 2 * tStd ||
                Math.abs(d.humidity    - hAvg) > 2 * hStd ||
                Math.abs(d.windSpeed   - wAvg) > 2 * wStd) {
                anomalyIndices.add(i);
            }
        }
        anomalyCount.setText(String.valueOf(anomalyIndices.size()));
        anomalyCount.setStyle(anomalyIndices.isEmpty()
            ? "-fx-text-fill: #1E3A5F; -fx-font-size: 28px; -fx-font-weight: bold;"
            : "-fx-text-fill: #DC2626; -fx-font-size: 28px; -fx-font-weight: bold;");

        ObservableList<MeteoData> items = FXCollections.observableArrayList(data);
        meteoTable.setItems(items);
    }

    private List<MeteoData> loadData() {
        List<MeteoData> list = new ArrayList<>();
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT date, temperature, humidity, wind_speed, description FROM meteo ORDER BY date")) {
            while (rs.next()) {
                list.add(new MeteoData(
                    rs.getString("date"),
                    rs.getDouble("temperature"),
                    rs.getDouble("humidity"),
                    rs.getDouble("wind_speed"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private double avg(double[] a) {
        if (a.length == 0) return 0;
        double s = 0; for (double v : a) s += v; return s / a.length;
    }
    private double std(double[] a, double mean) {
        if (a.length == 0) return 0;
        double s = 0; for (double v : a) s += (v - mean) * (v - mean);
        return Math.sqrt(s / a.length);
    }
    private double min(double[] a) {
        double m = Double.POSITIVE_INFINITY; for (double v : a) if (v < m) m = v; return a.length == 0 ? 0 : m;
    }
    private double max(double[] a) {
        double m = Double.NEGATIVE_INFINITY; for (double v : a) if (v > m) m = v; return a.length == 0 ? 0 : m;
    }
    private String fmt(double d) { return String.format(Locale.US, "%.1f", d); }

    public static class MeteoData {
        private final String date, description;
        private final double temperature, humidity, windSpeed;
        public MeteoData(String date, double temperature, double humidity, double windSpeed, String description) {
            this.date = date; this.temperature = temperature; this.humidity = humidity;
            this.windSpeed = windSpeed; this.description = description;
        }
        public String getDate()          { return date; }
        public double getTemperature()   { return temperature; }
        public double getHumidity()      { return humidity; }
        public double getWindSpeed()     { return windSpeed; }
        public String getDescription()   { return description; }
    }
}
