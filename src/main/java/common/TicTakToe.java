package common;

import common.model.Player;

public interface TicTakToe {
    Player currentPlayer();
    boolean isGameActive();
    boolean makeTurn(int row, int col, Player player);
    char[][] getBoard();
}
