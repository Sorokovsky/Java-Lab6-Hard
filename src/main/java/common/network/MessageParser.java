package common.network;

public class MessageParser {
    public static ParsedMessage parse(String message) {
        if (message == null || message.isEmpty()) {
            return new ParsedMessage(MessageType.QUIT, null);
        }
        final var parts = message.split(" ");
        final var type = MessageType.fromString(parts[0]);
        if (type == null) {
            return new ParsedMessage(null, null);
        }
        switch (type) {
            case PLAYER, BOARD:
                return new ParsedMessage(type, parts[1]);
            case WIN:
                return new ParsedMessage(type, String.valueOf(parts[1].charAt(0)));
            case MOVE:
                if (parts.length == 3) {
                    return new ParsedMessage(type, new int[]{
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2])
                    });
                }
                break;
            default:
                return new ParsedMessage(type, null);
        }
        return new ParsedMessage(type, null);
    }

    public record ParsedMessage(MessageType type, Object data) {
    }
}
