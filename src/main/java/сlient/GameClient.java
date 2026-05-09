package сlient;

import common.model.Player;
import common.network.GameProtocol;
import common.network.MessageParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ClientListener listener;
    private Player player;
    private boolean myTurn;
    private String currentBoard;

    public void connect(String ip, int port, ClientListener listener) {
        this.listener = listener;
        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                listener.onConnected();
                listenToServer();
            } catch (IOException exception) {
                listener.onError(exception.getMessage());
            }
        }).start();
    }

    private void listenToServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                final var parsed = MessageParser.parse(line);
                handleMessage(parsed);
            }
        } catch (IOException exception) {
            listener.onError(exception.getMessage());
        }
    }

    private void handleMessage(MessageParser.ParsedMessage parsed) {
        switch (parsed.type()) {
            case PLAYER:
                player = parsed.data().equals(Player.Christ.toString()) ? Player.Christ : Player.ZERO;
                listener.onPlayerAssigned(player);
                break;
            case START:
                listener.onGameStart();
                break;
            case YOUR_TURN:
                myTurn = true;
                listener.onYourTurn();
                break;
            case WAIT:
                myTurn = false;
                listener.onWait();
                break;
            case BOARD:
                if (parsed.data() instanceof String) {
                    currentBoard = (String) parsed.data();
                    listener.onBoardUpdate(currentBoard);
                }
                break;
            case WIN:
                char winner = ((String) parsed.data()).charAt(0);
                boolean isWinner = (winner == Player.Christ.value() && player == Player.Christ) ||
                        (winner == Player.ZERO.value() && player == Player.ZERO);
                listener.onWin(isWinner);
                break;
            case DRAW:
                listener.onDraw();
                break;
            case SERVER_STOP:
                listener.onServerStop();
                break;
            case INVALID:
                listener.onInvalidMove();
                break;
        }
    }

    public void sendMove(int row, int col) {
        if (myTurn) {
            out.println(GameProtocol.sendMove(row, col));
            myTurn = false;
        } else {
            listener.onInvalidMove();
        }
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException exception) {
            listener.onError(exception.getMessage());
        }
    }

    public  Player getPlayer() {
        return player;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public String getCurrentBoard() {
        return currentBoard;
    }
}
