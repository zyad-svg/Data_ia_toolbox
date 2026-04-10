package com.toolbox.module5;
import java.sql.*;

public class GameDao {
    // Le fichier SQLite se créera à la racine de ton projet
    private static final String URL = "jdbc:sqlite:toolbox.db";

    public GameDao() {
        // Création de la table au démarrage
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS tictactoe_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "difficulty TEXT," +
                    "winner TEXT," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveGame(String difficulty, String winner) {
        String sql = "INSERT INTO tictactoe_history (difficulty, winner) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, difficulty);
            pstmt.setString(2, winner);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int[] getStats() {
        // Index 0: Humain, 1: IA, 2: Nul
        int[] stats = new int[3];
        String sql = "SELECT winner, COUNT(*) as count FROM tictactoe_history GROUP BY winner";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String winner = rs.getString("winner");
                int count = rs.getInt("count");
                if ("Humain".equals(winner)) stats[0] = count;
                else if ("IA".equals(winner)) stats[1] = count;
                else if ("Nul".equals(winner)) stats[2] = count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}