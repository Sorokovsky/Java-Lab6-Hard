package server;

public interface ServerListener {
    void onServerStarted(int port);
    void onClientConnected(String player, String ip);
    void onMove(String player, int row, int col);
    void onGameEnd(String result);
    void onError(String error);
}