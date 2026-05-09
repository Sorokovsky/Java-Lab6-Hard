package сlient;

import common.model.Player;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TicTacToeNetworkClient extends JFrame {
    private final ConnectionPanel connectionPanel;
    private final BoardPanel boardPanel;
    private final JTextArea logArea;
    private final JLabel statusLabel;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Player myPlayer;
    private boolean myTurn;
    private boolean gameActive;
    private String currentBoard;

    public TicTacToeNetworkClient() {
        setTitle("Хрестики-нолики - Клієнт");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        connectionPanel = new ConnectionPanel(this::connectToServer);
        add(connectionPanel, BorderLayout.NORTH);

        boardPanel = new BoardPanel(this::makeMove);
        add(boardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Не підключено", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);

        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        bottomPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void connectToServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            connectionPanel.setConnected(true);
            gameActive = true;
            myTurn = false;
            currentBoard = "         ";
            statusLabel.setText("Підключено...");

            new Thread(this::listenToServer).start();
            log("Підключено до сервера " + ip + ":" + port);
        } catch (IOException e) {
            log("Помилка підключення: " + e.getMessage());
            connectionPanel.setConnected(false);
            statusLabel.setText("Не підключено");
        }
    }

    private void listenToServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("PLAYER")) {
                    String playerType = line.substring(7);
                    myPlayer = playerType.equals("Christ") ? Player.Christ : Player.ZERO;
                    log("Ви граєте за " + (myPlayer == Player.Christ ? "Хрестик (X)" : "Нулик (O)"));
                    statusLabel.setText("Ви граєте за " + (myPlayer.value()));

                } else if (line.equals("START")) {
                    log("Гра починається!");
                    gameActive = true;

                } else if (line.equals("YOUR_TURN")) {
                    myTurn = true;
                    log("Ваш хід!");
                    statusLabel.setText("Ваш хід!");
                    updateBoard();

                } else if (line.equals("WAIT")) {
                    myTurn = false;
                    log("Очікуйте ходу суперника...");
                    statusLabel.setText("Хід суперника...");
                    updateBoard();

                } else if (line.startsWith("BOARD")) {
                    currentBoard = line.substring(6);
                    updateBoard();

                } else if (line.startsWith("WIN")) {
                    char winner = line.charAt(4);
                    gameActive = false;
                    boolean isWinner = (winner == 'X' && myPlayer == Player.Christ) ||
                            (winner == 'O' && myPlayer == Player.ZERO);
                    if (isWinner) {
                        log("ВИ ПЕРЕМОГЛИ!");
                        statusLabel.setText("ВИ ПЕРЕМОГЛИ!");
                        JOptionPane.showMessageDialog(this, "Вітаю! Ви перемогли!");
                    } else {
                        log("Ви програли!");
                        statusLabel.setText("Ви програли!");
                        JOptionPane.showMessageDialog(this, "Ви програли!");
                    }
                    boardPanel.enableAll(false);

                } else if (line.equals("DRAW")) {
                    gameActive = false;
                    log("Нічия!");
                    statusLabel.setText("Нічия!");
                    JOptionPane.showMessageDialog(this, "Нічия!");
                    boardPanel.enableAll(false);

                } else if (line.equals("SERVER_STOP")) {
                    log("Сервер зупинено");
                    statusLabel.setText("Сервер зупинено");
                    closeConnection();
                    break;

                } else if (line.equals("INVALID")) {
                    log("Невірний хід! Спробуйте ще раз");
                    myTurn = true;
                    statusLabel.setText("Невірний хід! Ваша черга");
                    updateBoard();
                }
            }
        } catch (IOException e) {
            log("Втрачено з'єднання: " + e.getMessage());
            closeConnection();
        }
    }

    private void makeMove(int row, int col) {
        if (!gameActive || !myTurn) {
            log("Зараз не ваш хід!");
            return;
        }

        int index = row * 3 + col;
        if (currentBoard.charAt(index) != ' ') {
            log("Ця клітинка вже зайнята!");
            return;
        }

        out.println("MOVE " + row + " " + col);
        myTurn = false;
        statusLabel.setText("Хід відправлено...");
        log("Ви зробили хід у клітинку (" + row + "," + col + ")");
        updateBoard();
    }

    private void updateBoard() {
        boardPanel.updateBoard(currentBoard, myTurn && gameActive, gameActive);
    }

    private void closeConnection() {
        try {
            gameActive = false;
            if (socket != null) socket.close();
            connectionPanel.setConnected(false);
            statusLabel.setText("Не підключено");
            boardPanel.enableAll(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToeNetworkClient().setVisible(true));
    }
}