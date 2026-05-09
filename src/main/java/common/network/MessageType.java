package common.network;

public enum MessageType {
    PLAYER, START, YOUR_TURN, WAIT, BOARD, WIN, DRAW, SERVER_STOP, INVALID, MOVE, QUIT;

    public static MessageType fromString(String string) {
        try {
            return valueOf(string);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
