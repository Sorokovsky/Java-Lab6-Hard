package common;

import common.model.Player;

import java.util.Arrays;

public class TikTakToeLogic implements TicTakToe {
    private char[][] board;
    private Player currentPlayer;
    private boolean gameActive;
    public TikTakToeLogic() {
        board = new char[3][3];
        currentPlayer = Player.Christ;
        gameActive = true;
        initializeBoard();
    }

    public boolean makeTurn(int row, int col, Player player) {
        if (gameActive && currentPlayer == player && row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == ' ') {
            board[row][col] = player.value();
            currentPlayer = (currentPlayer == Player.Christ) ? Player.ZERO : Player.Christ;
            checkGameStatus();
            return true;
        }
        return false;
    }

    public boolean isGameActive() { return gameActive; }
    public Player currentPlayer() { return currentPlayer; }
    public char getWinner() {
        if (!gameActive) {
            for (int i = 0; i < 3; i++) {
                if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return board[i][0];
                if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return board[0][i];
            }
            if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return board[0][0];
            if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return board[0][2];
        }
        return ' ';
    }

    public char[][] getBoard() {
        return board;
    }

    private void checkGameStatus() {
        if (checkWin() || isBoardFull()) {
            gameActive = false;
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return true;
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return true;
        }
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return true;
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return true;
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') return false;
            }
        }
        return true;
    }

    private void initializeBoard() {
        for (char[] chars : board) {
            Arrays.fill(chars, ' ');
        }
    }
}
