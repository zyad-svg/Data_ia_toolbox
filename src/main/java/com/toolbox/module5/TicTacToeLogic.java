package com.toolbox.module5;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicTacToeLogic {

    public static boolean checkWin(String[] board, String player) {
        int[][] wins = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Lignes
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Colonnes
                {0, 4, 8}, {2, 4, 6}             // Diagonales
        };
        for (int[] w : wins) {
            if (board[w[0]].equals(player) && board[w[1]].equals(player) && board[w[2]].equals(player)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFull(String[] board) {
        for (String c : board) {
            if (c.isEmpty()) return false;
        }
        return true;
    }

    public static int getComputerMove(String[] board, boolean isHardMode) {
        List<Integer> emptySpots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (board[i].isEmpty()) emptySpots.add(i);
        }

        if (emptySpots.isEmpty()) return -1;

        if (isHardMode) {
            // 1. L'IA peut gagner ?
            for (int spot : emptySpots) {
                board[spot] = "O";
                if (checkWin(board, "O")) { board[spot] = ""; return spot; }
                board[spot] = "";
            }
            // 2. Le joueur peut gagner ? On bloque !
            for (int spot : emptySpots) {
                board[spot] = "X";
                if (checkWin(board, "X")) { board[spot] = ""; return spot; }
                board[spot] = "";
            }
            // 3. Sinon, on prend le centre s'il est libre
            if (board[4].isEmpty()) return 4;
        }

        // Mode Facile ou aucun danger immédiat : aléatoire
        return emptySpots.get(new Random().nextInt(emptySpots.size()));
    }
}