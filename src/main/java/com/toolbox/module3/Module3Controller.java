package com.toolbox.module3;

import com.toolbox.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Module3Controller implements Initializable {

    @FXML private Label importStatus;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> genreFilter;
    @FXML private TableView<MovieRow> moviesTable;
    @FXML private TableColumn<MovieRow, String> colTitle;
    @FXML private TableColumn<MovieRow, String> colGenres;
    @FXML private VBox recoSection;
    @FXML private Label recoTitle;
    @FXML private VBox recoContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDatabase();

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colGenres.setCellValueFactory(new PropertyValueFactory<>("genres"));
        moviesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Genre tags : chaque genre affiché comme un badge coloré
        colGenres.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String genres, boolean empty) {
                super.updateItem(genres, empty);
                if (empty || genres == null || genres.isBlank()) { setGraphic(null); return; }
                HBox tags = new HBox(4);
                tags.setAlignment(Pos.CENTER_LEFT);
                Arrays.stream(genres.split("[|,]"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !"(no genres listed)".equals(s))
                    .forEach(g -> { Label lbl = new Label(g); lbl.getStyleClass().add("genre-tag"); tags.getChildren().add(lbl); });
                setGraphic(tags);
                setText(null);
            }
        });

        // Real-time filter listeners
        searchField.textProperty().addListener((obs, o, v) -> applyFilters());
        genreFilter.valueProperty().addListener((obs, o, v) -> applyFilters());

        // On row selection → show recommendations
        moviesTable.getSelectionModel().selectedItemProperty()
            .addListener((obs, o, selected) -> { if (selected != null) showRecommendations(selected); });

        genreFilter.getItems().add("Tous les genres");
        genreFilter.setValue("Tous les genres");
        loadMoviesIntoTable(null, null);
        refreshGenres();
    }

    private void initDatabase() {
        try (Statement st = DatabaseManager.getConnection().createStatement()) {
            st.execute("DROP TABLE IF EXISTS movies");
            st.execute("CREATE TABLE movies(id INTEGER PRIMARY KEY, title TEXT, genres TEXT, description TEXT)");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void loadCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir un fichier CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        File file = fc.showOpenDialog(importStatus.getScene().getWindow());
        if (file == null) return;

        try (Statement st = DatabaseManager.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM movies");
            int existing = rs.getInt(1);
            rs.close();
            if (existing > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    existing + " film(s) déjà présents. Écraser les données ?",
                    ButtonType.YES, ButtonType.NO);
                alert.setHeaderText("Confirmation d'import");
                if (alert.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
                st.execute("DELETE FROM movies");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        int imported = parseCsv(file);
        if (imported > 0) {
            importStatus.setText(imported + " films chargés avec succès");
            importStatus.setStyle("-fx-text-fill: #16A34A; -fx-font-size: 13px;");
            refreshGenres();
            applyFilters();
        } else if (!importStatus.getStyle().contains("DC2626")) {
            importStatus.setText("Aucun film importé — vérifiez le format du fichier (id,title,genres)");
            importStatus.setStyle("-fx-text-fill: #D97706; -fx-font-size: 13px;");
        }
    }

    private int parseCsv(File file) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            // En-tête — retire le BOM UTF-8 si présent
            String headerLine = br.readLine();
            if (headerLine == null) return 0;
            if (headerLine.startsWith("\uFEFF")) headerLine = headerLine.substring(1);

            // Détection du séparateur
            char sep = detectSeparator(headerLine);
            System.out.println("[CSV] Séparateur détecté : '" + sep + "'");

            String[] headers = splitLine(headerLine, sep);
            System.out.println("[CSV] Colonnes : " + String.join(" | ", headers));

            // Détection dynamique des colonnes titre et genre(s)
            int titleIdx = -1;
            List<Integer> genreIdxList = new ArrayList<>();
            for (int i = 0; i < headers.length; i++) {
                String h = headers[i].trim().toLowerCase().replaceAll("[^a-z0-9]", "");
                if (titleIdx == -1 && (h.equals("title") || h.equals("seriestitle") || h.equals("movietitle") || h.equals("name")))
                    titleIdx = i;
                // Collecte toutes les colonnes genre/subgenre
                if (h.equals("genre") || h.equals("genres") || h.startsWith("genre") || h.startsWith("subgenre"))
                    genreIdxList.add(i);
            }
            if (titleIdx == -1) titleIdx = 0;
            if (genreIdxList.isEmpty()) genreIdxList.add(titleIdx + 1);
            System.out.println("[CSV] → titre=col[" + titleIdx + "] genres=cols" + genreIdxList);

            try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(
                    "INSERT OR IGNORE INTO movies(id, title, genres, description) VALUES(?, ?, ?, ?)")) {

                String line;
                int rowNum = 1;
                // Affiche les 5 premières lignes brutes pour debug
                int debugLines = 0;
                while ((line = br.readLine()) != null) {
                    if (line.isBlank()) continue;
                    if (debugLines < 5) {
                        System.out.println("[CSV] Ligne " + rowNum + " brute : " + line);
                        debugLines++;
                    }
                    try {
                        String[] parts = splitLine(line, sep);
                        if (parts.length <= titleIdx) {
                            System.err.println("[CSV] Ligne ignorée (colonnes insuffisantes) : " + line);
                            continue;
                        }
                        String title = parts[titleIdx].trim();
                        // Combine toutes les colonnes genre/subgenre en une seule chaîne
                        String genres = genreIdxList.stream()
                            .filter(idx -> idx < parts.length)
                            .map(idx -> parts[idx].trim())
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.joining(", "));
                        if (title.isEmpty()) continue;

                        ps.setInt(1, rowNum);
                        ps.setString(2, title);
                        ps.setString(3, genres);
                        ps.setString(4, null);
                        ps.executeUpdate();
                        count++;
                        rowNum++;
                    } catch (Exception lineEx) {
                        System.err.println("[CSV] Ligne ignorée (erreur) : " + lineEx.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            importStatus.setText("Erreur CSV: " + e.getMessage());
            importStatus.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 13px;");
        }
        return count;
    }

    /** Détecte le séparateur en comptant les occurrences hors guillemets. */
    private char detectSeparator(String line) {
        int commas = 0, semicolons = 0, tabs = 0;
        boolean inQ = false;
        for (char c : line.toCharArray()) {
            if (c == '"') { inQ = !inQ; continue; }
            if (inQ) continue;
            if (c == ',')  commas++;
            else if (c == ';') semicolons++;
            else if (c == '\t') tabs++;
        }
        if (tabs > commas && tabs > semicolons) return '\t';
        if (semicolons > commas) return ';';
        return ',';
    }

    /** Parse une ligne CSV avec un séparateur donné, en gérant les champs entre guillemets. */
    private String[] splitLine(String line, char sep) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        int i = 0;
        while (i < line.length()) {
            char c = line.charAt(i);
            if (inQuote) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        sb.append('"'); i += 2; continue; // guillemet doublé
                    }
                    inQuote = false;
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '"') { inQuote = true; }
                else if (c == sep) { fields.add(sb.toString()); sb.setLength(0); }
                else { sb.append(c); }
            }
            i++;
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    private void refreshGenres() {
        Set<String> genres = new TreeSet<>();
        try {
            ResultSet rs = DatabaseManager.getConnection().createStatement()
                .executeQuery("SELECT genres FROM movies");
            while (rs.next()) {
                String g = rs.getString("genres");
                if (g != null)
                    Arrays.stream(g.split("[|,]")).map(String::trim)
                        .filter(s -> !s.isEmpty() && !"(no genres listed)".equals(s))
                        .forEach(genres::add);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        String current = genreFilter.getValue();
        genreFilter.getItems().clear();
        genreFilter.getItems().add("Tous les genres");
        genreFilter.getItems().addAll(genres);
        genreFilter.setValue(genres.contains(current) ? current : "Tous les genres");
    }

    private void applyFilters() {
        String search = searchField.getText();
        String genre  = genreFilter.getValue();
        loadMoviesIntoTable(
            (search == null || search.isBlank()) ? null : search.toLowerCase(),
            "Tous les genres".equals(genre)      ? null : genre
        );
    }

    private void loadMoviesIntoTable(String search, String genre) {
        moviesTable.getItems().clear();
        recoSection.setVisible(false);
        recoSection.setManaged(false);
        try {
            StringBuilder sql = new StringBuilder("SELECT title, genres FROM movies WHERE 1=1");
            List<Object> params = new ArrayList<>();
            if (search != null) { sql.append(" AND LOWER(title) LIKE ?"); params.add("%" + search + "%"); }
            if (genre  != null) { sql.append(" AND genres LIKE ?");       params.add("%" + genre + "%"); }
            sql.append(" ORDER BY title LIMIT 1000");

            PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                moviesTable.getItems().add(new MovieRow(rs.getString("title"), rs.getString("genres")));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void showRecommendations(MovieRow selected) {
        Set<String> selGenres = parseGenres(selected.getGenres());
        if (selGenres.isEmpty()) return;

        List<ScoredMovie> scored = new ArrayList<>();
        try {
            ResultSet rs = DatabaseManager.getConnection().createStatement()
                .executeQuery("SELECT title, genres FROM movies ORDER BY title");
            while (rs.next()) {
                String t = rs.getString("title");
                if (t.equals(selected.getTitle())) continue;
                Set<String> mg = parseGenres(rs.getString("genres"));
                long common = selGenres.stream().filter(mg::contains).count();
                Set<String> union = new HashSet<>(selGenres);
                union.addAll(mg);
                int score = union.isEmpty() ? 0 : (int) Math.round(100.0 * common / union.size());
                if (score > 0) scored.add(new ScoredMovie(new MovieRow(t, rs.getString("genres")), score));
            }
        } catch (SQLException e) { e.printStackTrace(); return; }

        scored.sort(Comparator.comparingInt(s -> -s.score));
        List<ScoredMovie> top5 = scored.size() > 5 ? scored.subList(0, 5) : scored;

        recoContainer.getChildren().clear();
        recoTitle.setText("Films similaires à « " + selected.getTitle() + " »");

        int rank = 1;
        for (ScoredMovie sm : top5) {
            String scoreClass = sm.score > 60 ? "score-green" : sm.score > 30 ? "score-orange" : "score-red";
            String cardClass  = sm.score > 60 ? "movie-card-green" : sm.score > 30 ? "movie-card-orange" : "movie-card-red";

            Label rankLbl = new Label("#" + rank++);
            rankLbl.getStyleClass().add("rank-number");
            rankLbl.setMinWidth(50);

            Label titleLbl = new Label(sm.movie.getTitle());
            titleLbl.getStyleClass().add("movie-title");
            titleLbl.setWrapText(true);
            titleLbl.setMaxWidth(Double.MAX_VALUE);

            Label genresLbl = new Label(sm.movie.getGenres() != null ? sm.movie.getGenres() : "");
            genresLbl.getStyleClass().add("movie-genre");
            genresLbl.setMaxWidth(Double.MAX_VALUE);

            Label badge = new Label(sm.score + "% similaire");
            badge.getStyleClass().addAll("score-badge", scoreClass);
            badge.setMinWidth(Region.USE_PREF_SIZE);

            VBox info = new VBox(4, titleLbl, genresLbl);
            info.setAlignment(Pos.CENTER_LEFT);
            info.setFillWidth(true);
            HBox.setHgrow(info, Priority.ALWAYS);

            HBox card = new HBox(16, rankLbl, info, badge);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setMaxWidth(Double.MAX_VALUE);
            card.getStyleClass().addAll("movie-card", cardClass);
            recoContainer.getChildren().add(card);
        }

        recoSection.setVisible(true);
        recoSection.setManaged(true);
    }

    private Set<String> parseGenres(String genres) {
        if (genres == null || genres.isBlank()) return Collections.emptySet();
        return Arrays.stream(genres.split("[|,]"))
            .map(String::trim)
            .filter(s -> !s.isEmpty() && !"(no genres listed)".equals(s))
            .collect(Collectors.toSet());
    }

    public static class MovieRow {
        private final String title, genres;
        public MovieRow(String title, String genres) { this.title = title; this.genres = genres; }
        public String getTitle()  { return title; }
        public String getGenres() { return genres; }
    }

    private static class ScoredMovie {
        final MovieRow movie;
        final int score;
        ScoredMovie(MovieRow m, int s) { movie = m; score = s; }
    }
}
