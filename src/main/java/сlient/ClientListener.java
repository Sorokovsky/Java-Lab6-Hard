package сlient;

import common.model.Player;

public interface ClientListener {
    void onConnected();
    void onPlayerAssigned(Player player);
    void onGameStart();
    void onYourTurn();
    void onWait();
    void onBoardUpdate(String board);
    void onWin(boolean isWinner);
    void onDraw();
    void onServerStop();
    void onInvalidMove();
    void onError(String error);
}