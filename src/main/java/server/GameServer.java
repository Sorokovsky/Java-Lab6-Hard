package server;

import common.model.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private final int port;
    private ServerSocket serverSocket;
    private ExecutorService clientPool;
    private boolean running;
    private ServerListener listener;

    public GameServer(int port) {
        this.port = port;
        this.clientPool = Executors.newCachedThreadPool();
    }

    public void start(ServerListener listener) {
        this.listener = listener;
        new Thread(this::acceptClients).start();
    }

    private void acceptClients() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            listener.onServerStarted(port);
            Socket player1 = serverSocket.accept();
            listener.onClientConnected(String.valueOf(Player.Christ.value()), player1.getInetAddress().getHostAddress());
            Socket player2 = serverSocket.accept();
            listener.onClientConnected(String.valueOf(Player.ZERO.value()), player2.getInetAddress().getHostAddress());
            GameSession session = new GameSession(player1, player2, listener);
            clientPool.execute(session);
        } catch (IOException exception) {
            listener.onError(exception.getMessage());
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
            clientPool.shutdown();
        } catch (IOException exception) {
            listener.onError(exception.getMessage());
        }
    }
}
