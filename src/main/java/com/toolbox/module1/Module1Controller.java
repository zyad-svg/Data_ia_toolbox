package com.toolbox.module1;

import com.toolbox.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class Module1Controller implements Initializable {

    @FXML private TextArea inputText;
    @FXML private Label wordCountLbl, sentenceCountLbl, charCountLbl;
    @FXML private Label scoreLbl, sentimentEmoji, sentimentLabel, sentimentDetail;
    @FXML private ProgressBar sentimentBar;
    @FXML private TableView<WordRow> topWordsTable;
    @FXML private TableColumn<WordRow, String> colWord;
    @FXML private TableColumn<WordRow, Integer> colCount;
    @FXML private VBox letterContainer;

    private static final Set<String> STOPWORDS = Set.of(
        "le","la","les","de","du","des","un","une","et","ou","mais","donc","or","ni","car",
        "ce","cet","cette","ces","mon","ma","mes","ton","ta","tes","son","sa","ses",
        "nous","vous","ils","elles","je","tu","il","elle","en","au","aux","par","sur",
        "sous","dans","avec","pour","que","qui","quoi","dont","se","si","ne","pas",
        "plus","tres","bien","aussi","comme","quand","alors","tout","tous","toute","toutes"
    );

    private final Set<String> positiveWords = new HashSet<>();
    private final Set<String> negativeWords = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDatabase();
        loadSentimentWords();

        colWord.setCellValueFactory(new PropertyValueFactory<>("word"));
        colCount.setCellValueFactory(new PropertyValueFactory<>("count"));
        topWordsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void initDatabase() {
        String[] positive = {
            "excellent","magnifique","parfait","super","genial","formidable","merveilleux",
            "fantastique","incroyable","remarquable","brillant","admirable","sublime",
            "exceptionnel","splendide","radieux","epanouissant","chaleureux","bienveillant",
            "enthousiasmant","positif","agreable","plaisant","reussi","accompli","heureux",
            "joyeux","serein","confiant","optimiste","dynamique","creatif","talentueux",
            "courageux","genereux","sincere","loyal","patient","sage","fort","beau","bon",
            "doux","calme","libre","pur","vrai","juste","noble","elegant","aimable"
        };
        String[] negative = {
            "terrible","horrible","affreux","catastrophique","desastreux","lamentable",
            "pitoyable","mediocre","decevant","nul","mauvais","triste","douloureux","penible",
            "difficile","problematique","negatif","inquietant","alarmant","dangereux",
            "violent","agressif","hostile","brutal","cruel","injuste","malheureux",
            "deprimant","angoissant","stressant","fatiguant","ennuyeux","monotone",
            "frustrant","irritant","agacant","lourd","complexe","incertain","sombre",
            "laid","faux","froid","dur","amer","haineux","colere","peur","echec","perte"
        };

        try {
            Connection c = DatabaseManager.getConnection();
            c.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS sentiment_words(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT UNIQUE, type TEXT)");
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT OR IGNORE INTO sentiment_words(word, type) VALUES(?, ?)")) {
                for (String w : positive) { ps.setString(1, w); ps.setString(2, "positive"); ps.executeUpdate(); }
                for (String w : negative) { ps.setString(1, w); ps.setString(2, "negative"); ps.executeUpdate(); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadSentimentWords() {
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT word, type FROM sentiment_words")) {
            while (rs.next()) {
                if ("positive".equals(rs.getString("type"))) positiveWords.add(rs.getString("word"));
                else negativeWords.add(rs.getString("word"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void onAnalyze() {
        String text = inputText.getText();
        if (text == null || text.isBlank()) return;
        analyzeText(text);
    }

    @FXML
    private void onLoadFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir un fichier texte");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
        File file = fc.showOpenDialog(inputText.getScene().getWindow());
        if (file == null) return;
        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            inputText.setText(content);
            analyzeText(content);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void analyzeText(String text) {
        String trimmed = text.trim();
        int chars = text.length();
        int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
        int sentences = trimmed.isEmpty() ? 0 : trimmed.split("[.!?]+").length;

        wordCountLbl.setText(String.valueOf(words));
        sentenceCountLbl.setText(String.valueOf(sentences));
        charCountLbl.setText(String.valueOf(chars));

        String normalized = stripAccents(text.toLowerCase());
        List<String> tokens = Arrays.stream(normalized.split("[^\\p{L}]+"))
            .filter(w -> w.length() >= 2)
            .collect(Collectors.toList());

        Map<String, Integer> freq = new HashMap<>();
        for (String w : tokens) {
            if (STOPWORDS.contains(w)) continue;
            freq.merge(w, 1, Integer::sum);
        }
        List<WordRow> top = freq.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(10)
            .map(e -> new WordRow(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
        topWordsTable.getItems().setAll(top);

        int pos = 0, neg = 0;
        for (String w : tokens) {
            if (positiveWords.contains(w)) pos++;
            else if (negativeWords.contains(w)) neg++;
        }
        int score = (pos + neg == 0) ? 50 : (int) Math.round(100.0 * pos / (pos + neg));
        updateSentimentUI(score, pos, neg);

        updateLetterFrequency(text.toLowerCase());
    }

    private void updateSentimentUI(int score, int pos, int neg) {
        scoreLbl.setText(score + "/100");
        sentimentBar.setProgress(score / 100.0);
        sentimentBar.getStyleClass().removeAll("bar-green", "bar-orange", "bar-red");
        sentimentDetail.setText(pos + " mots positifs, " + neg + " mots negatifs");

        String emoji, label, style, barClass;
        if (score >= 70) {
            emoji = ":)"; label = "Texte positif";
            style = "-fx-text-fill: #22C55E; -fx-font-weight: bold; -fx-font-size: 16px;";
            barClass = "bar-green";
        } else if (score >= 40) {
            emoji = ":|"; label = "Texte neutre";
            style = "-fx-text-fill: #F59E0B; -fx-font-weight: bold; -fx-font-size: 16px;";
            barClass = "bar-orange";
        } else {
            emoji = ":("; label = "Texte negatif";
            style = "-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 16px;";
            barClass = "bar-red";
        }
        sentimentEmoji.setText(emoji);
        sentimentLabel.setText(label);
        sentimentLabel.setStyle(style);
        sentimentBar.getStyleClass().add(barClass);
    }

    private void updateLetterFrequency(String lowered) {
        int[] letterFreq = new int[26];
        for (char ch : lowered.toCharArray()) {
            if (ch >= 'a' && ch <= 'z') letterFreq[ch - 'a']++;
        }
        List<int[]> letters = new ArrayList<>();
        for (int i = 0; i < 26; i++) if (letterFreq[i] > 0) letters.add(new int[]{i, letterFreq[i]});
        letters.sort((a, b) -> b[1] - a[1]);

        letterContainer.getChildren().clear();
        int max = letters.isEmpty() ? 1 : letters.get(0)[1];
        for (int i = 0; i < Math.min(10, letters.size()); i++) {
            int[] lt = letters.get(i);
            Label letterLbl = new Label(String.valueOf((char) ('a' + lt[0])));
            letterLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A5F; -fx-min-width: 22;");
            ProgressBar bar = new ProgressBar((double) lt[1] / max);
            bar.setPrefWidth(220);
            bar.getStyleClass().add("letter-bar");
            Label countLbl = new Label(String.valueOf(lt[1]));
            countLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280; -fx-min-width: 32;");
            HBox row = new HBox(12, letterLbl, bar, countLbl);
            row.setAlignment(Pos.CENTER_LEFT);
            letterContainer.getChildren().add(row);
        }
    }

    private String stripAccents(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static class WordRow {
        private final String word;
        private final int count;
        public WordRow(String word, int count) { this.word = word; this.count = count; }
        public String getWord()  { return word; }
        public int getCount()    { return count; }
    }
}
