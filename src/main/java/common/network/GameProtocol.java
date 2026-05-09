package common.network;

public class GameProtocol {
    public static String sendPlayer(String playerType) {
        return "%s %s".formatted(MessageType.PLAYER, playerType);
    }

    public static String sendBoard(String board) {
        return "%s %s".formatted(MessageType.BOARD, board);
    }

    public static String sendWin(char winner) {
        return "%s %s".formatted(MessageType.WIN, winner);
    }

    public static String sendMove(int row, int col) {
        return "%s %s %s".formatted(MessageType.MOVE, row, col);
    }

    public static String sendYourTurn() {
        return MessageType.YOUR_TURN.toString();
    }

    public static String sendWait() {
        return MessageType.WAIT.toString();
    }

    public static String sendDraw() {
        return MessageType.DRAW.toString();
    }

    public static String sendServerStop() {
        return MessageType.SERVER_STOP.toString();
    }

    public static String sendInvalid() {
        return MessageType.INVALID.toString();
    }
}
