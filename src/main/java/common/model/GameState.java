package common.model;

public class GameState {
    private final char[][] board;
    private final Player currentPlayer;
    private boolean gameActive;
    private final char winner;

    public GameState(char[][] board, Player currentPlayer, boolean gameActive, char winner) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.gameActive = gameActive;
        this.winner = winner;
    }

    private char[][] copyBoard(char[][] board) {
        char[][] newBoard = new char[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, 3);
        }
        return newBoard;
    }

    public char getCell(int row, int col) {
        return board[row][col];
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameActive() {
        return gameActive;
    }

    public char getWinner() {
        return winner;
    }
}
