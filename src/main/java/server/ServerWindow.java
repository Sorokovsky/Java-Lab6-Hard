package server;

import javax.swing.*;
import java.awt.*;

public class ServerWindow extends JFrame implements ServerListener {
    private final ControlPanel controlPanel;
    private final LogPanel  logPanel;
    private GameServer gameServer;

    public ServerWindow() {
        setTitle("Хрестики-нолики - Сервер");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        controlPanel = new ControlPanel(this::startServer, this::stopServer);
        add(controlPanel, BorderLayout.NORTH);
        logPanel = new LogPanel();
        add(logPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServerWindow().setVisible(true));
    }

    @Override
    public void onServerStarted(int port) {
        logPanel.log("Сервер запущено на порту " + port);
        logPanel.log("Очікування гравця Х (Хрестик)...");
    }

    @Override
    public void onClientConnected(String player, String ip) {
        logPanel.log("Гравець " + player + " підключився: " + ip);
        if (player.equals("X")) {
            logPanel.log("Очікування гравця О (Нулик)...");
        } else {
            logPanel.log("Гра розпочата! Хід гравця Х");
        }
    }

    @Override
    public void onMove(String player, int row, int col) {
        logPanel.log("Хід гравця " + player + " (" + row + "," + col + ")");
    }

    @Override
    public void onGameEnd(String result) {
        logPanel.log(result);
    }

    @Override
    public void onError(String error) {
        logPanel.log("Помилка: " + error);
        controlPanel.setServerRunning(false);
    }

    private void startServer(int port) {
        gameServer = new GameServer(port);
        gameServer.start(this);
    }

    private void stopServer() {
        if (gameServer != null) {
            gameServer.stop();
        }
    }
}
