package colourise.networking.protocol;

public class Message {
    private final Command command;
    private final byte[] arguments;

    public Command getCommand() {
        return command;
    }

    public int arguments() {
        return arguments.length;
    }

    public byte getArgument(int index) {
        return arguments[index];
    }

    public Message(Command command, byte[] arguments) {
        this.command = command;
        this.arguments = arguments;
        assert Command.getLength(command) == arguments.length;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[arguments.length + 1];
        bytes[0] = command.toByte();
        for (int i = 0; i < arguments.length; i++)
            bytes[i + 1] = arguments[i];
        return bytes;
    }

    public static class Factory {
        public static Message hello(int count) {
            return new Message(Command.HELLO, new byte[]{ (byte) count });
        }

        public static Message lead() {
            return new Message(Command.LEAD, new byte[0]);
        }

        public static Message joined(int count) {
            return new Message(Command.JOINED, new byte[] { (byte) count });
        }

        public static Message leave() {
            return new Message(Command.LEAVE, new byte[0]);
        }

        public static Message left(int a, int next) {
            return new Message(Command.LEFT, new byte[] { (byte) a, (byte) next });
        }

        public static Message begin(int id, int count, int r0, int c0, int r1, int c1, int r2, int c2, int r3, int c3, int r4, int c4) {
            return new Message(Command.BEGIN, new byte[] { (byte) id, (byte) count, (byte) r0, (byte) c0, (byte) r1, (byte) c1, (byte) r2, (byte) c2, (byte) r3, (byte) c3, (byte) r4, (byte) c4 });
        }

        public static Message play(int row, int column, Card c) {
            return new Message(Command.PLAY, new byte[] { (byte) row, (byte) column, (byte) c.ordinal() });
        }

        public static Message played(int id, int row, int column, Card card, int next) {
            return new Message(Command.PLAYED, new byte[] { (byte) id, (byte) row, (byte) column, (byte) card.ordinal(), (byte) next });
        }

        public static Message end(int s1, int s2, int s3, int s4, int s5) {
            return new Message(Command.END, new byte[] { (byte) s1, (byte) s2, (byte) s3, (byte) s4, (byte) s5 });
        }

        public static Message start() {
            return new Message(Command.START, new byte[0]);
        }

        public static Message error(Error error) { return new Message(Command.ERROR, new byte[] { error.toByte() }); }
    }
}
