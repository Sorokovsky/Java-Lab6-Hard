package server;

import common.TikTakToeLogic;
import common.model.Player;
import common.network.GameProtocol;
import common.network.MessageParser;
import common.network.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameSession implements Runnable {
    private final Socket playerX;
    private final Socket playerO;
    private final PrintWriter outX;
    private final PrintWriter outO;
    private final BufferedReader inX;
    private final BufferedReader inO;
    private final TikTakToeLogic game;
    private final ServerListener listener;

    public GameSession(Socket playerX, Socket playerO, ServerListener listener) throws IOException {
        this.playerX = playerX;
        this.playerO = playerO;
        this.listener = listener;
        this.game = new TikTakToeLogic();
        this.outX = new PrintWriter(playerX.getOutputStream(), true);
        this.outO = new PrintWriter(playerO.getOutputStream(), true);
        this.inX = new BufferedReader(new InputStreamReader(playerX.getInputStream()));
        this.inO = new BufferedReader(new InputStreamReader(playerO.getInputStream()));
    }

    @Override
    public void run() {
        try {
            initializePlayers();
            gameLoop();
        } catch (IOException exception) {
            listener.onError(exception.getMessage());
        } finally {
            closeConnections();
        }
    }

    private void initializePlayers() throws IOException {
        outX.println(GameProtocol.sendPlayer(Player.Christ.toString()));
        outX.println(GameProtocol.sendWait());
        outO.println(GameProtocol.sendPlayer(Player.ZERO.toString()));
        outX.println(GameProtocol.sendYourTurn());
        outO.println(GameProtocol.sendWait());
    }

    private void gameLoop() throws IOException {
        while (game.isGameActive()) {
            Player current = game.currentPlayer();
            BufferedReader currentIn = (current == Player.Christ) ? this.inX : this.inO;
            PrintWriter currentOut = (current == Player.Christ) ? this.outX : this.outO;
            final var message = currentIn.readLine();
            final var parsed = MessageParser.parse(message);
            if (parsed.type() == MessageType.MOVE && parsed.data() instanceof int[] move) {
                handleMove(move[0], move[1], current, currentOut);
            }
        }
    }

    private void handleMove(int row, int col, Player current, PrintWriter currentOut) {
        if (game.makeTurn(row, col, current)) {
            broadcastBoard();
            if (!game.isGameActive()) {
                endGame();
            } else {
                switchTurn();
            }
        } else {
            currentOut.println(GameProtocol.sendInvalid());
        }
    }

    private void broadcastBoard() {
        final var board = boardToString();
        outX.println(GameProtocol.sendBoard(board));
        outO.println(GameProtocol.sendBoard(board));
    }

    private void switchTurn() {
        if (game.currentPlayer() == Player.Christ) {
            outX.println(GameProtocol.sendYourTurn());
            outO.println(GameProtocol.sendWait());
        } else {
            outO.println(GameProtocol.sendYourTurn());
            outX.println(GameProtocol.sendWait());
        }
    }

    private void endGame() {
        char winner = game.getWinner();
        if (winner != ' ') {
            outX.println(GameProtocol.sendWin(winner));
            outO.println(GameProtocol.sendWin(winner));
            listener.onGameEnd("Winner: " + winner);
        } else {
            outX.println(GameProtocol.sendDraw());
            outO.println(GameProtocol.sendDraw());
            listener.onGameEnd("Draw");
        }
    }

    private String boardToString() {
        StringBuilder stringBuilder = new StringBuilder();
        char[][] board = game.getBoard();
        for (char[] row : board) {
            for (char cell : row) {
                stringBuilder.append(cell);
            }
        }
        return stringBuilder.toString();
    }

    private void closeConnections() {
        try {
            if (outX != null) outX.println(GameProtocol.sendServerStop());
            if (outO != null) outO.println(GameProtocol.sendServerStop());
            if (playerX != null) playerX.close();
            if (playerO != null) playerO.close();
        } catch (IOException e) {
            listener.onError(e.getMessage());
        }
    }
}
