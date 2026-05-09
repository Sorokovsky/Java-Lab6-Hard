package common.model;

public enum Player {
    ZERO('0'),
    Christ('X');

    private final char value;

    Player(char value) {
        this.value = value;
    }

    public char value() {
        return value;
    }
}
