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
        public static Message hello(boolean leader, int count) {
            return new Message(Command.HELLO, new byte[]{ (byte) (leader ? 1 : 0), (byte) count });
        }

        public static Message joined(int count) {
            return new Message(Command.JOINED, new byte[] { (byte) count });
        }

        public static Message leave() {
            return new Message(Command.LEAVE, new byte[0]);
        }

        public static Message left(int a) {
            return new Message(Command.LEFT, new byte[] { (byte) a });
        }

        public static Message begin(int id, int count) {
            return new Message(Command.BEGIN, new byte[] { (byte) id, (byte) count });
        }

        public static Message play(int row, int column, Card c) {
            return new Message(Command.PLAY, new byte[] { (byte) row, (byte) column, (byte) c.ordinal() });
        }

        public static Message played(int id, int row, int column) {
            return new Message(Command.PLAYED, new byte[] { (byte) id, (byte) row, (byte) column });
        }

        public static Message end(int s1, int s2, int s3, int s4, int s5) {
            return new Message(Command.END, new byte[] { (byte) s1, (byte) s2, (byte) s3, (byte) s4, (byte) s5 });
        }
    }
}
